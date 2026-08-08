package com.adroid.cryptosignal.presentation.history

import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.domain.model.TradeSignal

enum class HistoryFilter {
    ALL,
    BUY_ONLY,
    SELL_ONLY
}

data class HistoryUiState(
    val allSignals: List<TradeSignal> = emptyList(),
    val filter: HistoryFilter = HistoryFilter.ALL
) {
    val filteredSignals: List<TradeSignal>
        get() = when (filter) {
            HistoryFilter.ALL -> allSignals
            HistoryFilter.BUY_ONLY -> allSignals.filter { it.type == SignalType.BUY }
            HistoryFilter.SELL_ONLY -> allSignals.filter { it.type == SignalType.SELL }
        }
}
