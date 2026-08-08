package com.adroid.cryptosignal.domain.indicator

import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.IndicatorConfig

/**
 * Computes every configured indicator series over [candles] and reduces them to the current
 * and previous candle's values. Returns null when there isn't enough history yet for the
 * slowest indicator (EMA long / MACD slow) to produce two consecutive data points.
 */
fun computeIndicatorSnapshot(candles: List<Candle>, config: IndicatorConfig): IndicatorSnapshot? {
    if (candles.size < 2) return null

    val closes = candles.map { it.close }
    val lastIndex = candles.lastIndex
    val previousIndex = lastIndex - 1

    val emaShortSeries = ema(closes, config.emaShortPeriod)
    val emaMidSeries = ema(closes, config.emaMidPeriod)
    val emaLongSeries = ema(closes, config.emaLongPeriod)
    val rsiSeries = rsi(closes, config.rsiPeriod)
    val macdResult = macd(closes, config.macdFastPeriod, config.macdSlowPeriod, config.macdSignalPeriod)
    val bollinger = bollingerBands(closes, config.bollingerPeriod, config.bollingerStdDev)
    val atrSeries = atr(candles, config.atrPeriod)
    val vwapSeries = vwap(candles)

    val requiredNonNan = listOf(
        emaShortSeries[lastIndex], emaShortSeries[previousIndex],
        emaMidSeries[lastIndex], emaMidSeries[previousIndex],
        emaLongSeries[lastIndex],
        rsiSeries[lastIndex], rsiSeries[previousIndex],
        macdResult.macdLine[lastIndex], macdResult.macdLine[previousIndex],
        macdResult.signalLine[lastIndex], macdResult.signalLine[previousIndex],
        bollinger.upper[lastIndex], bollinger.middle[lastIndex], bollinger.lower[lastIndex],
        atrSeries[lastIndex]
    )
    if (requiredNonNan.any { it.isNaN() }) return null

    val volumeWindowSize = config.volumeAveragePeriod
    val precedingCandles = candles.subList(0, lastIndex).takeLast(volumeWindowSize)
    val averageVolume = if (precedingCandles.isEmpty()) candles[lastIndex].volume else precedingCandles.map { it.volume }.average()

    return IndicatorSnapshot(
        price = candles[lastIndex].close,
        emaShort = emaShortSeries[lastIndex],
        emaShortPrevious = emaShortSeries[previousIndex],
        emaMid = emaMidSeries[lastIndex],
        emaMidPrevious = emaMidSeries[previousIndex],
        emaLong = emaLongSeries[lastIndex],
        rsi = rsiSeries[lastIndex],
        rsiPrevious = rsiSeries[previousIndex],
        macdLine = macdResult.macdLine[lastIndex],
        macdLinePrevious = macdResult.macdLine[previousIndex],
        macdSignal = macdResult.signalLine[lastIndex],
        macdSignalPrevious = macdResult.signalLine[previousIndex],
        macdHistogram = macdResult.histogram[lastIndex],
        bollingerUpper = bollinger.upper[lastIndex],
        bollingerMiddle = bollinger.middle[lastIndex],
        bollingerLower = bollinger.lower[lastIndex],
        atr = atrSeries[lastIndex],
        vwap = vwapSeries[lastIndex],
        volume = candles[lastIndex].volume,
        averageVolume = averageVolume
    )
}
