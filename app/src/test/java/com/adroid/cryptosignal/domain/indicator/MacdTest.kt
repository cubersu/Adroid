package com.adroid.cryptosignal.domain.indicator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MacdTest {

    @Test
    fun `matches hand-computed values for a short linear series`() {
        val closes = listOf(10.0, 11.0, 12.0, 13.0, 14.0)
        val result = macd(closes, fastPeriod = 2, slowPeriod = 3, signalPeriod = 2)

        assertTrue(result.macdLine[0].isNaN())
        assertTrue(result.macdLine[1].isNaN())
        assertEquals(0.5, result.macdLine[2], DELTA)
        assertEquals(0.5, result.macdLine[3], DELTA)
        assertEquals(0.5, result.macdLine[4], DELTA)

        assertTrue(result.signalLine[2].isNaN())
        assertEquals(0.5, result.signalLine[3], DELTA)
        assertEquals(0.5, result.signalLine[4], DELTA)

        assertTrue(result.histogram[2].isNaN())
        assertEquals(0.0, result.histogram[4], DELTA)
    }

    @Test
    fun `histogram is always macd line minus signal line where both are defined`() {
        val closes = listOf(5.0, 6.0, 5.5, 7.0, 8.0, 7.5, 9.0, 10.0)
        val result = macd(closes, fastPeriod = 3, slowPeriod = 5, signalPeriod = 2)
        for (i in closes.indices) {
            val m = result.macdLine[i]
            val s = result.signalLine[i]
            val h = result.histogram[i]
            if (!m.isNaN() && !s.isNaN()) {
                assertEquals(m - s, h, DELTA)
            } else {
                assertTrue(h.isNaN())
            }
        }
    }

    private companion object {
        const val DELTA = 0.0001
    }
}
