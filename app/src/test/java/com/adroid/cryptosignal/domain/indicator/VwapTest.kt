package com.adroid.cryptosignal.domain.indicator

import com.adroid.cryptosignal.domain.model.Candle
import org.junit.Assert.assertEquals
import org.junit.Test

class VwapTest {

    @Test
    fun `accumulates typical price weighted by volume within the same day`() {
        val candles = listOf(
            Candle(openTimeSeconds = DAY_1_BASE, open = 9.0, high = 10.0, low = 8.0, close = 9.0, volume = 100.0),
            Candle(openTimeSeconds = DAY_1_BASE + 60, open = 11.0, high = 12.0, low = 10.0, close = 11.0, volume = 200.0)
        )
        val result = vwap(candles)

        assertEquals(9.0, result[0], DELTA) // typical = (10+8+9)/3
        assertEquals(10.3333, result[1], DELTA) // (900 + 2200) / 300
    }

    @Test
    fun `resets cumulative sums at a new UTC day`() {
        val candles = listOf(
            Candle(openTimeSeconds = DAY_1_BASE, open = 9.0, high = 10.0, low = 8.0, close = 9.0, volume = 100.0),
            Candle(openTimeSeconds = DAY_1_BASE + 90_000, open = 19.0, high = 20.0, low = 18.0, close = 19.0, volume = 50.0)
        )
        val result = vwap(candles)

        assertEquals(9.0, result[0], DELTA)
        assertEquals(19.0, result[1], DELTA) // new day: not blended with day 1's VWAP
    }

    private companion object {
        const val DAY_1_BASE = 1_700_000_000L
        const val DELTA = 0.001
    }
}
