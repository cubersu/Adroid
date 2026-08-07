package com.adroid.cryptosignal.domain.indicator

data class MacdResult(
    val macdLine: List<Double>,
    val signalLine: List<Double>,
    val histogram: List<Double>
)

/**
 * MACD (Moving Average Convergence Divergence). Standard settings: 12/26 EMA difference as the
 * MACD line, 9-period EMA of the MACD line as the signal line. All three output lists are the
 * same size as [closes], NaN-padded until enough data is available.
 */
fun macd(
    closes: List<Double>,
    fastPeriod: Int = 12,
    slowPeriod: Int = 26,
    signalPeriod: Int = 9
): MacdResult {
    require(fastPeriod < slowPeriod) { "fastPeriod must be smaller than slowPeriod" }

    val emaFast = ema(closes, fastPeriod)
    val emaSlow = ema(closes, slowPeriod)
    val macdLine = closes.indices.map { i ->
        val fast = emaFast[i]
        val slow = emaSlow[i]
        if (fast.isNaN() || slow.isNaN()) Double.NaN else fast - slow
    }

    val validStart = slowPeriod - 1
    val signalLine = if (validStart >= macdLine.size) {
        List(macdLine.size) { Double.NaN }
    } else {
        val validMacd = macdLine.subList(validStart, macdLine.size)
        val emaOfValid = ema(validMacd, signalPeriod)
        MutableList(macdLine.size) { Double.NaN }.also { padded ->
            for (i in emaOfValid.indices) padded[validStart + i] = emaOfValid[i]
        }
    }

    val histogram = macdLine.indices.map { i ->
        val m = macdLine[i]
        val s = signalLine[i]
        if (m.isNaN() || s.isNaN()) Double.NaN else m - s
    }

    return MacdResult(macdLine, signalLine, histogram)
}
