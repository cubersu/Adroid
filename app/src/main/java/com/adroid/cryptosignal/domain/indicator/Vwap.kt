package com.adroid.cryptosignal.domain.indicator

import com.adroid.cryptosignal.domain.model.Candle
import java.util.concurrent.TimeUnit

/**
 * Volume-weighted average price, resetting at each UTC calendar day boundary. Returns a list
 * the same size as [candles]; VWAP is always defined once the first candle of a day has arrived
 * (a single-candle window is its own VWAP).
 */
fun vwap(candles: List<Candle>): List<Double> {
    if (candles.isEmpty()) return emptyList()

    val result = MutableList(candles.size) { 0.0 }
    var cumulativePriceVolume = 0.0
    var cumulativeVolume = 0.0
    var currentDay = -1L

    for (i in candles.indices) {
        val candle = candles[i]
        val day = TimeUnit.SECONDS.toDays(candle.openTimeSeconds)
        if (day != currentDay) {
            currentDay = day
            cumulativePriceVolume = 0.0
            cumulativeVolume = 0.0
        }
        val typicalPrice = (candle.high + candle.low + candle.close) / 3.0
        cumulativePriceVolume += typicalPrice * candle.volume
        cumulativeVolume += candle.volume

        result[i] = if (cumulativeVolume == 0.0) typicalPrice else cumulativePriceVolume / cumulativeVolume
    }
    return result
}
