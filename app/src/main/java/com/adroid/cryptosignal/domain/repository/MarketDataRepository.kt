package com.adroid.cryptosignal.domain.repository

import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.ConnectionState
import com.adroid.cryptosignal.domain.model.Ticker
import com.adroid.cryptosignal.domain.model.TradingPair
import kotlinx.coroutines.flow.Flow

/**
 * Market data access, backed by BtcTurk. WebSocket is the preferred live source; REST is used
 * for the initial candle history backfill and as a fallback when the socket is down.
 */
interface MarketDataRepository {

    /** Full pair list from the exchange info endpoint (REST, called once / on refresh). */
    suspend fun fetchExchangeInfo(): Result<List<TradingPair>>

    /** One-shot REST snapshot of recent 1-minute candles, used for backfill/reconnect sync. */
    suspend fun fetchRecentCandles(pairSymbol: String, count: Int = 200): Result<List<Candle>>

    /**
     * Starts (if needed) a shared WebSocket connection and subscribes to live 1-minute
     * kline updates for [pairSymbol] on the `tradeview` channel. Each new/updated candle is
     * emitted as it arrives. Cancelling the collector unsubscribes.
     */
    fun observeCandleUpdates(pairSymbol: String): Flow<Candle>

    /** Live ticker updates for [pairSymbol] via the `ticker` channel. */
    fun observeTicker(pairSymbol: String): Flow<Ticker>

    /** Connection status of the shared WebSocket session. */
    fun observeConnectionState(): Flow<ConnectionState>
}
