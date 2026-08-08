package com.adroid.cryptosignal.data.remote.websocket

import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.ConnectionState
import com.adroid.cryptosignal.domain.model.Ticker
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Manages a single shared connection to BtcTurk Pro's websocket feed
 * (wss://ws-feed-pro.btcturk.com). Reconnects with exponential backoff and re-subscribes to
 * every pair that was active at the time of the drop. See [BtcTurkSocketProtocol] for the
 * caveat on response message identification.
 */
@Singleton
class BtcTurkWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var webSocket: WebSocket? = null
    private var reconnectJob: Job? = null
    private var reconnectAttempt = 0
    private var manuallyClosed = true

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val candleFlows = ConcurrentHashMap<String, MutableSharedFlow<Candle>>()
    private val tickerFlows = ConcurrentHashMap<String, MutableSharedFlow<Ticker>>()

    // Reference-counted, not a plain set: multiple independent collectors (the background
    // monitoring service, the watchlist screen, the pair detail screen) can all be observing
    // the same pair's candles/ticker at once. A naive set would let one collector's unsubscribe
    // silently kill the feed for everyone else still watching that pair.
    private val tradeViewRefCounts = ConcurrentHashMap<String, Int>()
    private val tickerRefCounts = ConcurrentHashMap<String, Int>()

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            reconnectAttempt = 0
            _connectionState.value = ConnectionState.CONNECTED
            resubscribeAll()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            handleMessage(text)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _connectionState.value = ConnectionState.DISCONNECTED
            scheduleReconnect()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.value = ConnectionState.DISCONNECTED
            if (!manuallyClosed) scheduleReconnect()
        }
    }

    fun candlesFor(pairSymbol: String): SharedFlow<Candle> =
        candleFlows.getOrPut(pairSymbol) { MutableSharedFlow(extraBufferCapacity = 16) }

    fun tickerFor(pairSymbol: String): SharedFlow<Ticker> =
        tickerFlows.getOrPut(pairSymbol) { MutableSharedFlow(extraBufferCapacity = 16) }

    fun subscribeTradeView(pairSymbol: String) {
        ensureConnected()
        val subscriberCount = tradeViewRefCounts.merge(pairSymbol, 1, Int::plus)
        if (subscriberCount == 1) {
            sendSubscribe(BtcTurkSocketProtocol.CHANNEL_TRADEVIEW, BtcTurkSocketProtocol.tradeViewEvent(pairSymbol))
        }
    }

    fun unsubscribeTradeView(pairSymbol: String) {
        val remaining = tradeViewRefCounts.computeIfPresent(pairSymbol) { _, count -> count - 1 }
        if (remaining == null || remaining <= 0) {
            tradeViewRefCounts.remove(pairSymbol)
            sendSubscribe(BtcTurkSocketProtocol.CHANNEL_TRADEVIEW, BtcTurkSocketProtocol.tradeViewEvent(pairSymbol), join = false)
        }
    }

    fun subscribeTicker(pairSymbol: String) {
        ensureConnected()
        val subscriberCount = tickerRefCounts.merge(pairSymbol, 1, Int::plus)
        if (subscriberCount == 1) {
            sendSubscribe(BtcTurkSocketProtocol.CHANNEL_TICKER, pairSymbol)
        }
    }

    fun unsubscribeTicker(pairSymbol: String) {
        val remaining = tickerRefCounts.computeIfPresent(pairSymbol) { _, count -> count - 1 }
        if (remaining == null || remaining <= 0) {
            tickerRefCounts.remove(pairSymbol)
            sendSubscribe(BtcTurkSocketProtocol.CHANNEL_TICKER, pairSymbol, join = false)
        }
    }

    fun disconnect() {
        manuallyClosed = true
        reconnectJob?.cancel()
        webSocket?.close(NORMAL_CLOSURE_CODE, "client_closed")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    private fun ensureConnected() {
        if (webSocket != null) return
        connect()
    }

    private fun connect() {
        manuallyClosed = false
        _connectionState.value = ConnectionState.CONNECTING
        val request = Request.Builder().url(WS_URL).build()
        webSocket = okHttpClient.newWebSocket(request, listener)
    }

    private fun resubscribeAll() {
        tradeViewRefCounts.keys.forEach { pair ->
            sendSubscribe(BtcTurkSocketProtocol.CHANNEL_TRADEVIEW, BtcTurkSocketProtocol.tradeViewEvent(pair))
        }
        tickerRefCounts.keys.forEach { pair -> sendSubscribe(BtcTurkSocketProtocol.CHANNEL_TICKER, pair) }
    }

    private fun sendSubscribe(channel: String, event: String, join: Boolean = true) {
        webSocket?.send(BtcTurkSocketProtocol.subscribeMessage(channel, event, join))
    }

    private fun handleMessage(text: String) {
        try {
            val element = json.parseToJsonElement(text)
            val array = element as? JsonArray ?: return
            if (array.size < 2) return
            val payload = array[1] as? JsonObject ?: return
            val pairHint = SocketPayloadParser.extractPairSymbol(payload)

            val candle = SocketPayloadParser.tryParseCandle(pairHint.orEmpty(), payload)
            if (candle != null && pairHint != null) {
                candleFlows.getOrPut(pairHint) { MutableSharedFlow(extraBufferCapacity = 16) }.tryEmit(candle)
                return
            }

            val ticker = SocketPayloadParser.tryParseTicker(pairHint.orEmpty(), payload)
            if (ticker != null) {
                tickerFlows.getOrPut(ticker.pairSymbol) { MutableSharedFlow(extraBufferCapacity = 16) }.tryEmit(ticker)
            }
        } catch (_: Exception) {
            // Malformed or unrecognized frame; ignore and keep the connection alive.
        }
    }

    private fun scheduleReconnect() {
        if (manuallyClosed) return
        reconnectJob?.cancel()
        val delayMs = min(MAX_RECONNECT_DELAY_MS, BASE_RECONNECT_DELAY_MS * 2.0.pow(reconnectAttempt).toLong())
        reconnectAttempt++
        _connectionState.value = ConnectionState.RECONNECTING
        reconnectJob = scope.launch {
            delay(delayMs)
            if (!manuallyClosed) connect()
        }
    }

    private companion object {
        const val WS_URL = "wss://ws-feed-pro.btcturk.com"
        const val NORMAL_CLOSURE_CODE = 1000
        const val BASE_RECONNECT_DELAY_MS = 1_000L
        const val MAX_RECONNECT_DELAY_MS = 30_000L
    }
}
