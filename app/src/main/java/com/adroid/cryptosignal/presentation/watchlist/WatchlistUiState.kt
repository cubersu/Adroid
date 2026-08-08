package com.adroid.cryptosignal.presentation.watchlist

import com.adroid.cryptosignal.domain.model.ConnectionState
import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.domain.model.TradingPair

data class WatchlistItem(
    val pair: TradingPair,
    val lastPrice: Double?,
    val dailyPercent: Double?,
    /** What the strategy says right now (including NEUTRAL), not just the last saved signal. */
    val currentStatus: SignalType,
    val latestSignal: TradeSignal?
)

data class WatchlistUiState(
    val isLoading: Boolean = true,
    val items: List<WatchlistItem> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED
)
