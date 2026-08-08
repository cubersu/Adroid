package com.adroid.cryptosignal.presentation.pairdetail

import com.adroid.cryptosignal.domain.indicator.IndicatorSnapshot
import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.domain.strategy.SignalCriteriaBreakdown

data class PairDetailUiState(
    val isLoading: Boolean = true,
    val pairSymbol: String,
    val candles: List<Candle> = emptyList(),
    val emaShortSeries: List<Double> = emptyList(),
    val emaMidSeries: List<Double> = emptyList(),
    val vwapSeries: List<Double> = emptyList(),
    val snapshot: IndicatorSnapshot? = null,
    val criteriaBreakdown: SignalCriteriaBreakdown? = null,
    val latestSignal: TradeSignal? = null
)
