package com.adroid.cryptosignal.domain.indicator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmaTest {

    @Test
    fun `first period-1 values are NaN`() {
        val values = listOf(1.0, 2.0, 3.0, 4.0)
        val result = ema(values, period = 3)
        assertTrue(result[0].isNaN())
        assertTrue(result[1].isNaN())
    }

    @Test
    fun `seed value is the simple average of the first period values`() {
        val values = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val result = ema(values, period = 3)
        assertEquals(2.0, result[2], DELTA)
    }

    @Test
    fun `recursive values follow the standard ema formula`() {
        val values = (1..10).map { it.toDouble() }
        val result = ema(values, period = 3)
        // Linear ramp of slope 1: EMA settles to value - 1 for this multiplier (2/(3+1)=0.5).
        assertEquals(3.0, result[3], DELTA)
        assertEquals(9.0, result[9], DELTA)
    }

    @Test
    fun `too few values returns an all-NaN list of the same size`() {
        val values = listOf(1.0, 2.0)
        val result = ema(values, period = 5)
        assertEquals(2, result.size)
        assertTrue(result.all { it.isNaN() })
    }

    private companion object {
        const val DELTA = 0.0001
    }
}
