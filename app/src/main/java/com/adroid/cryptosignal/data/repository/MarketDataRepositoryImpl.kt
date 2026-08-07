package com.adroid.cryptosignal.data.repository

import com.adroid.cryptosignal.data.mapper.toDomain
import com.adroid.cryptosignal.data.mapper.toDomainCandles
import com.adroid.cryptosignal.data.remote.BtcTurkApiService
import com.adroid.cryptosignal.data.remote.BtcTurkGraphApiService
import com.adroid.cryptosignal.data.remote.websocket.BtcTurkWebSocketClient
import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.ConnectionState
import com.adroid.cryptosignal.domain.model.Ticker
import com.adroid.cryptosignal.domain.model.TradingPair
import com.adroid.cryptosignal.domain.repository.MarketDataRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Singleton
class MarketDataRepositoryImpl @Inject constructor(
    private val apiService: BtcTurkApiService,
    private val graphApiService: BtcTurkGraphApiService,
    private val webSocketClient: BtcTurkWebSocketClient
) : MarketDataRepository {

    override suspend fun fetchExchangeInfo(): Result<List<TradingPair>> = runCatching {
        val response = apiService.getExchangeInfo()
        check(response.success) { response.message ?: "exchangeinfo request failed" }
        response.data?.symbols.orEmpty().map { it.toDomain() }
    }

    override suspend fun fetchRecentCandles(pairSymbol: String, count: Int): Result<List<Candle>> = runCatching {
        val nowSeconds = System.currentTimeMillis() / 1000
        val fromSeconds = nowSeconds - count * SECONDS_PER_MINUTE
        val response = graphApiService.getKlineHistory(
            symbol = pairSymbol,
            resolution = "1",
            fromUnixSeconds = fromSeconds,
            toUnixSeconds = nowSeconds
        )
        response.toDomainCandles()
    }

    /**
     * Emits an initial REST backfill, then live candles from the shared websocket. On every
     * reconnect (DISCONNECTED/RECONNECTING -> CONNECTED transition) a fresh REST backfill is
     * re-emitted so any candles missed while the socket was down are still delivered.
     */
    override fun observeCandleUpdates(pairSymbol: String): Flow<Candle> = channelFlow {
        fetchRecentCandles(pairSymbol).getOrNull()?.forEach { send(it) }

        webSocketClient.subscribeTradeView(pairSymbol)

        val socketJob = launch {
            webSocketClient.candlesFor(pairSymbol).collect { send(it) }
        }

        val resyncJob = launch {
            webSocketClient.connectionState
                .drop(1)
                .filter { it == ConnectionState.CONNECTED }
                .collect {
                    fetchRecentCandles(pairSymbol).getOrNull()?.forEach { send(it) }
                }
        }

        awaitClose {
            socketJob.cancel()
            resyncJob.cancel()
            webSocketClient.unsubscribeTradeView(pairSymbol)
        }
    }

    override fun observeTicker(pairSymbol: String): Flow<Ticker> = channelFlow {
        webSocketClient.subscribeTicker(pairSymbol)
        val job = launch {
            webSocketClient.tickerFor(pairSymbol).collect { send(it) }
        }
        awaitClose {
            job.cancel()
            webSocketClient.unsubscribeTicker(pairSymbol)
        }
    }

    override fun observeConnectionState(): Flow<ConnectionState> = webSocketClient.connectionState

    private companion object {
        const val SECONDS_PER_MINUTE = 60L
    }
}
