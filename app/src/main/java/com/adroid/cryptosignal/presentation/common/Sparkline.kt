package com.adroid.cryptosignal.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * A minimal axis-free line chart for a short recent price window (e.g. a watchlist card's
 * last ~20-30 minutes). No labels, no grid — just the shape of the move.
 */
@Composable
fun Sparkline(
    values: List<Double>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    strokeWidthPx: Float = 3f
) {
    if (values.size < 2) return

    val minValue = values.min()
    val maxValue = values.max()
    val range = (maxValue - minValue).takeIf { it > 0.0 } ?: 1.0
    val lineColor = if (color == Color.Unspecified) {
        if (values.last() >= values.first()) BuyGreenLocal else SellRedLocal
    } else {
        color
    }

    Canvas(modifier = modifier) {
        val stepX = size.width / (values.size - 1)
        val points = values.mapIndexed { index, value ->
            val x = stepX * index
            val y = size.height - ((value - minValue) / range * size.height).toFloat()
            Offset(x, y)
        }
        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = strokeWidthPx,
                cap = Stroke.DefaultCap
            )
        }
    }
}

private val BuyGreenLocal = Color(0xFF00C853)
private val SellRedLocal = Color(0xFFFF3B30)
