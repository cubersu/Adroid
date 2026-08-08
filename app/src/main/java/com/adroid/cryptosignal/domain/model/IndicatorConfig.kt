package com.adroid.cryptosignal.domain.model

enum class IndicatorType {
    EMA,
    VWAP,
    RSI,
    MACD,
    BOLLINGER_BANDS,
    ATR
}

/**
 * Periods/parameters used by the indicator engine. Defaults match the spec: EMA 9/21/50,
 * RSI 14 (7 optional/secondary), MACD 12/26/9, Bollinger 20/2, ATR 14.
 */
data class IndicatorConfig(
    val emaShortPeriod: Int = 9,
    val emaMidPeriod: Int = 21,
    val emaLongPeriod: Int = 50,
    val rsiPeriod: Int = 14,
    val rsiSecondaryPeriod: Int = 7,
    val macdFastPeriod: Int = 12,
    val macdSlowPeriod: Int = 26,
    val macdSignalPeriod: Int = 9,
    val bollingerPeriod: Int = 20,
    val bollingerStdDev: Double = 2.0,
    val atrPeriod: Int = 14,
    val volumeAveragePeriod: Int = 5
)
