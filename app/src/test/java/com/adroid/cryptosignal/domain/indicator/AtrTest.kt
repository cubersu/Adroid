package com.adroid.cryptosignal.domain.indicator

import com.adroid.cryptosignal.domain.model.Candle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AtrTest {

    @Test
    fun `matches hand-computed Wilder ATR including a gap candle`() {
        val candles = listOf(
            candle(high = 10.0, low = 8.0, close = 9.0),
            candle(high = 11.0, low = 9.0, close = 10.0),
            candle(high = 9.0, low = 7.0, close = 8.0), // gap down from prev close 10
            candle(high = 10.0, low = 8.0, close = 9.0)
        )
        val result = atr(candles, period = 2)

        assertTrue(result[0].isNaN())
        assertTrue(result[1].isNaN())
        assertEquals(2.5, result[2], DELTA) // avg(TR1=2, TR2=3)
        assertEquals(2.25, result[3], DELTA) // (2.5*1 + 2) / 2
    }

    @Test
    fun `too few candles returns an all-NaN list`() {
        val candles = listOf(candle(10.0, 8.0, 9.0), candle(11.0, 9.0, 10.0))
        val result = atr(candles, period = 14)
        assertTrue(result.all { it.isNaN() })
    }

    private fun candle(high: Double, low: Double, close: Double, openTimeSeconds: Long = 0L) = Candle(
        openTimeSeconds = openTimeSeconds,
        open = close,
        high = high,
        low = low,
        close = close,
        volume = 1.0
    )

    private companion object {
        const val DELTA = 0.001
    }
}
