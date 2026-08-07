package com.adroid.cryptosignal.domain.model

/**
 * A single OHLCV candle. [openTimeSeconds] is the candle's start time, Unix epoch seconds,
 * aligned to the candle resolution (1 minute for this app).
 */
data class Candle(
    val openTimeSeconds: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)
