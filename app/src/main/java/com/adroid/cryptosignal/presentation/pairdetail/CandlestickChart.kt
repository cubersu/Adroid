package com.adroid.cryptosignal.presentation.pairdetail

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.adroid.cryptosignal.domain.model.Candle

private val BuyColor = Color(0xFF1DB954)
private val SellColor = Color(0xFFE53935)
val EmaShortColor = Color(0xFF2962FF)
val EmaMidColor = Color(0xFFFFA000)
val VwapColor = Color(0xFF9C27B0)

/**
 * Lightweight candlestick chart drawn directly on a [Canvas] — no charting library dependency.
 * Shows the last [maxCandles] candles plus the EMA-short/EMA-mid/VWAP series overlaid (same
 * index alignment as [candles], as produced by [com.adroid.cryptosignal.domain.indicator.ema]
 * and [com.adroid.cryptosignal.domain.indicator.vwap]).
 */
@Composable
fun CandlestickChart(
    candles: List<Candle>,
    emaShortSeries: List<Double>,
    emaMidSeries: List<Double>,
    vwapSeries: List<Double>,
    modifier: Modifier = Modifier,
    maxCandles: Int = 60
) {
    if (candles.isEmpty()) return

    val startIndex = (candles.size - maxCandles).coerceAtLeast(0)
    val visibleCandles = candles.subList(startIndex, candles.size)
    val visibleEmaShort = emaShortSeries.drop(startIndex)
    val visibleEmaMid = emaMidSeries.drop(startIndex)
    val visibleVwap = vwapSeries.drop(startIndex)

    val priceExtremes = buildList {
        visibleCandles.forEach { add(it.high); add(it.low) }
        visibleEmaShort.forEach { if (!it.isNaN()) add(it) }
        visibleEmaMid.forEach { if (!it.isNaN()) add(it) }
        visibleVwap.forEach { if (!it.isNaN()) add(it) }
    }
    if (priceExtremes.isEmpty()) return

    val maxPrice = priceExtremes.max()
    val minPrice = priceExtremes.min()
    val priceRange = (maxPrice - minPrice).takeIf { it > 0.0 } ?: 1.0
    val candleCount = visibleCandles.size

    Canvas(modifier = modifier) {
        val slotWidth = size.width / candleCount
        val candleWidth = slotWidth * 0.6f

        fun yFor(price: Double): Float =
            (size.height - ((price - minPrice) / priceRange) * size.height).toFloat()

        fun xFor(index: Int): Float = slotWidth * index + slotWidth / 2f

        visibleCandles.forEachIndexed { index, candle ->
            val color = if (candle.close >= candle.open) BuyColor else SellColor
            val x = xFor(index)
            drawLine(
                color = color,
                start = Offset(x, yFor(candle.high)),
                end = Offset(x, yFor(candle.low)),
                strokeWidth = 2f
            )
            val bodyTop = yFor(maxOf(candle.open, candle.close))
            val bodyBottom = yFor(minOf(candle.open, candle.close))
            drawRect(
                color = color,
                topLeft = Offset(x - candleWidth / 2f, bodyTop),
                size = Size(candleWidth, (bodyBottom - bodyTop).coerceAtLeast(1f))
            )
        }

        fun drawSeries(series: List<Double>, color: Color) {
            var previousPoint: Offset? = null
            series.forEachIndexed { index, value ->
                previousPoint = if (value.isNaN()) {
                    null
                } else {
                    val point = Offset(xFor(index), yFor(value))
                    previousPoint?.let { drawLine(color = color, start = it, end = point, strokeWidth = 3f) }
                    point
                }
            }
        }

        drawSeries(visibleEmaShort, EmaShortColor)
        drawSeries(visibleEmaMid, EmaMidColor)
        drawSeries(visibleVwap, VwapColor)
    }
}
