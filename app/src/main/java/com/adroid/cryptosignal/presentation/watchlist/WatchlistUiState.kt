package com.adroid.cryptosignal.presentation.watchlist

import com.adroid.cryptosignal.domain.model.ConnectionState
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.domain.model.TradingPair

data class WatchlistItem(
    val pair: TradingPair,
    val lastPrice: Double?,
    val dailyPercent: Double?,
    val latestSignal: TradeSignal?
)

data class WatchlistUiState(
    val isLoading: Boolean = true,
    val items: List<WatchlistItem> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED
)
