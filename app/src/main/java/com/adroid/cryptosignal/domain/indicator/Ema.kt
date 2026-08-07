package com.adroid.cryptosignal.domain.indicator

/**
 * Exponential moving average. Pure Kotlin, no Android dependency.
 *
 * Returns a list the same size as [values]. Indices `< period - 1` are [Double.NaN]
 * (insufficient data). The seed at index `period - 1` is the simple average of the first
 * [period] values; every value after that follows the standard recursive EMA formula.
 */
fun ema(values: List<Double>, period: Int): List<Double> {
    require(period > 0) { "period must be positive" }
    if (values.size < period) return List(values.size) { Double.NaN }

    val result = MutableList(values.size) { Double.NaN }
    val multiplier = 2.0 / (period + 1)

    val seed = values.subList(0, period).average()
    result[period - 1] = seed

    for (i in period until values.size) {
        val previous = result[i - 1]
        result[i] = (values[i] - previous) * multiplier + previous
    }
    return result
}
