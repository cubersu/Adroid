package com.adroid.cryptosignal.domain.indicator

import com.adroid.cryptosignal.domain.model.Candle
import kotlin.math.abs
import kotlin.math.max

/**
 * Average True Range using Wilder's smoothing. Used to size a stop-loss distance from the
 * current price. Returns a list the same size as [candles]; index 0 is NaN (true range needs a
 * previous close) and indices `1 until period` are NaN until the seed average is available.
 */
fun atr(candles: List<Candle>, period: Int = 14): List<Double> {
    require(period > 0) { "period must be positive" }
    if (candles.size <= period) return List(candles.size) { Double.NaN }

    val trueRanges = DoubleArray(candles.size)
    trueRanges[0] = candles[0].high - candles[0].low
    for (i in 1 until candles.size) {
        val previousClose = candles[i - 1].close
        trueRanges[i] = max(
            candles[i].high - candles[i].low,
            max(abs(candles[i].high - previousClose), abs(candles[i].low - previousClose))
        )
    }

    val result = MutableList(candles.size) { Double.NaN }
    var atrValue = (1..period).sumOf { trueRanges[it] } / period
    result[period] = atrValue

    for (i in period + 1 until candles.size) {
        atrValue = (atrValue * (period - 1) + trueRanges[i]) / period
        result[i] = atrValue
    }
    return result
}
