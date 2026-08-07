package com.adroid.cryptosignal.domain.indicator

import kotlin.math.sqrt

data class BollingerBandsResult(
    val upper: List<Double>,
    val middle: List<Double>,
    val lower: List<Double>
)

/**
 * Bollinger Bands: an [period]-period simple moving average with upper/lower bands at
 * [stdDevMultiplier] population standard deviations. NaN-padded for indices `< period - 1`.
 */
fun bollingerBands(closes: List<Double>, period: Int = 20, stdDevMultiplier: Double = 2.0): BollingerBandsResult {
    require(period > 0) { "period must be positive" }
    if (closes.size < period) {
        val nan = List(closes.size) { Double.NaN }
        return BollingerBandsResult(nan, nan, nan)
    }

    val upper = MutableList(closes.size) { Double.NaN }
    val middle = MutableList(closes.size) { Double.NaN }
    val lower = MutableList(closes.size) { Double.NaN }

    for (i in period - 1 until closes.size) {
        val window = closes.subList(i - period + 1, i + 1)
        val mean = window.average()
        val variance = window.sumOf { (it - mean) * (it - mean) } / period
        val stdDev = sqrt(variance)

        middle[i] = mean
        upper[i] = mean + stdDevMultiplier * stdDev
        lower[i] = mean - stdDevMultiplier * stdDev
    }
    return BollingerBandsResult(upper, middle, lower)
}
