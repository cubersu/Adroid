package com.adroid.cryptosignal.presentation.pairdetail

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.SignalType
import kotlin.math.abs

private val BuyColor = Color(0xFF1DB954)
private val SellColor = Color(0xFFE53935)
val EmaShortColor = Color(0xFF2962FF)
val EmaMidColor = Color(0xFFFFA000)
val VwapColor = Color(0xFF9C27B0)

/** A past signal to mark on the chart, at the candle whose open time is closest to it. */
data class SignalMarker(val timestampSeconds: Long, val type: SignalType)

/**
 * Lightweight candlestick chart drawn directly on a [Canvas] — no charting library dependency.
 * Shows the last [maxCandles] candles plus the EMA-short/EMA-mid/VWAP series overlaid (same
 * index alignment as [candles], as produced by [com.adroid.cryptosignal.domain.indicator.ema]
 * and [com.adroid.cryptosignal.domain.indicator.vwap]), and arrow markers for past signals that
 * fall inside the visible window.
 */
@Composable
fun CandlestickChart(
    candles: List<Candle>,
    emaShortSeries: List<Double>,
    emaMidSeries: List<Double>,
    vwapSeries: List<Double>,
    signalMarkers: List<SignalMarker> = emptyList(),
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

    val visibleRangeStart = visibleCandles.first().openTimeSeconds
    val visibleRangeEnd = visibleCandles.last().openTimeSeconds
    val visibleMarkers = signalMarkers.filter { it.timestampSeconds in visibleRangeStart..visibleRangeEnd }

    Canvas(modifier = modifier) {
        val slotWidth = size.width / candleCount
        val candleWidth = slotWidth * 0.6f
        val markerSize = (slotWidth * 0.35f).coerceAtMost(12f)
        val markerGap = markerSize * 1.5f

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

        visibleMarkers.forEach { marker ->
            val index = visibleCandles.indices.minByOrNull { i ->
                abs(visibleCandles[i].openTimeSeconds - marker.timestampSeconds)
            } ?: return@forEach
            val candle = visibleCandles[index]
            val x = xFor(index)
            if (marker.type == SignalType.BUY) {
                drawTriangleUp(Offset(x, yFor(candle.low) + markerGap), markerSize, BuyColor)
            } else if (marker.type == SignalType.SELL) {
                drawTriangleDown(Offset(x, yFor(candle.high) - markerGap), markerSize, SellColor)
            }
        }
    }
}

private fun DrawScope.drawTriangleUp(center: Offset, radius: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x - radius, center.y + radius)
        lineTo(center.x + radius, center.y + radius)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawTriangleDown(center: Offset, radius: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y + radius)
        lineTo(center.x - radius, center.y - radius)
        lineTo(center.x + radius, center.y - radius)
        close()
    }
    drawPath(path, color)
}
