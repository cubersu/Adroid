package com.adroid.cryptosignal.domain.indicator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RsiTest {

    @Test
    fun `matches hand-computed Wilder RSI for a short series`() {
        // changes: +1.0, -0.5, +1.0, -0.5
        val closes = listOf(1.0, 2.0, 1.5, 2.5, 2.0)
        val result = rsi(closes, period = 2)

        assertTrue(result[0].isNaN())
        assertTrue(result[1].isNaN())
        assertEquals(66.6667, result[2], DELTA)
        assertEquals(85.7143, result[3], DELTA)
        assertEquals(54.5455, result[4], DELTA)
    }

    @Test
    fun `all gains yields RSI of 100`() {
        val closes = listOf(1.0, 2.0, 3.0, 4.0)
        val result = rsi(closes, period = 2)
        assertEquals(100.0, result[2], DELTA)
        assertEquals(100.0, result[3], DELTA)
    }

    @Test
    fun `too few closes returns an all-NaN list`() {
        val result = rsi(listOf(1.0, 2.0), period = 14)
        assertTrue(result.all { it.isNaN() })
    }

    private companion object {
        const val DELTA = 0.001
    }
}
