package com.adroid.cryptosignal.domain.indicator

/**
 * The latest and previous-candle values for every indicator, all in one place so a
 * [com.adroid.cryptosignal.domain.strategy.SignalStrategy] can evaluate crossovers/turns
 * without recomputing series itself.
 */
data class IndicatorSnapshot(
    val price: Double,
    val emaShort: Double,
    val emaShortPrevious: Double,
    val emaMid: Double,
    val emaMidPrevious: Double,
    val emaLong: Double,
    val rsi: Double,
    val rsiPrevious: Double,
    val macdLine: Double,
    val macdLinePrevious: Double,
    val macdSignal: Double,
    val macdSignalPrevious: Double,
    val macdHistogram: Double,
    val bollingerUpper: Double,
    val bollingerMiddle: Double,
    val bollingerLower: Double,
    val atr: Double,
    val vwap: Double,
    val volume: Double,
    val averageVolume: Double
)
