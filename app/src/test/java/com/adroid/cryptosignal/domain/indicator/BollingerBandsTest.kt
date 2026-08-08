package com.adroid.cryptosignal.domain.indicator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BollingerBandsTest {

    @Test
    fun `matches hand-computed mean and population stddev`() {
        val closes = listOf(2.0, 4.0, 6.0, 8.0, 10.0)
        val result = bollingerBands(closes, period = 5, stdDevMultiplier = 2.0)

        assertTrue(result.middle[0].isNaN())
        assertTrue(result.middle[3].isNaN())

        assertEquals(6.0, result.middle[4], DELTA)
        assertEquals(11.6569, result.upper[4], DELTA)
        assertEquals(0.3431, result.lower[4], DELTA)
    }

    @Test
    fun `constant series has zero band width`() {
        val closes = List(5) { 100.0 }
        val result = bollingerBands(closes, period = 5, stdDevMultiplier = 2.0)
        assertEquals(100.0, result.middle[4], DELTA)
        assertEquals(100.0, result.upper[4], DELTA)
        assertEquals(100.0, result.lower[4], DELTA)
    }

    private companion object {
        const val DELTA = 0.001
    }
}
