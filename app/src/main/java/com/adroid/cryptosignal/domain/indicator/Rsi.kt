package com.adroid.cryptosignal.domain.indicator

/**
 * Relative Strength Index using Wilder's smoothing. Returns a list the same size as [closes];
 * indices `<= period` are [Double.NaN] (the first RSI value needs `period` prior changes, i.e.
 * `period + 1` closes).
 */
fun rsi(closes: List<Double>, period: Int = 14): List<Double> {
    require(period > 0) { "period must be positive" }
    if (closes.size <= period) return List(closes.size) { Double.NaN }

    val result = MutableList(closes.size) { Double.NaN }

    var avgGain = 0.0
    var avgLoss = 0.0
    for (i in 1..period) {
        val change = closes[i] - closes[i - 1]
        if (change > 0) avgGain += change else avgLoss -= change
    }
    avgGain /= period
    avgLoss /= period
    result[period] = rsiFromAverages(avgGain, avgLoss)

    for (i in period + 1 until closes.size) {
        val change = closes[i] - closes[i - 1]
        val gain = if (change > 0) change else 0.0
        val loss = if (change < 0) -change else 0.0
        avgGain = (avgGain * (period - 1) + gain) / period
        avgLoss = (avgLoss * (period - 1) + loss) / period
        result[i] = rsiFromAverages(avgGain, avgLoss)
    }
    return result
}

private fun rsiFromAverages(avgGain: Double, avgLoss: Double): Double {
    if (avgLoss == 0.0) return 100.0
    val rs = avgGain / avgLoss
    return 100.0 - (100.0 / (1.0 + rs))
}
