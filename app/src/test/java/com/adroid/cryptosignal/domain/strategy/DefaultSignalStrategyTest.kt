package com.adroid.cryptosignal.domain.strategy

import com.adroid.cryptosignal.domain.indicator.IndicatorSnapshot
import com.adroid.cryptosignal.domain.model.SignalType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultSignalStrategyTest {

    private val strategy = DefaultSignalStrategy()

    @Test
    fun `all four buy criteria met yields BUY with full confidence`() {
        val snapshot = baseSnapshot(
            emaShortPrevious = 100.0, emaMidPrevious = 101.0, // was below
            emaShort = 102.0, emaMid = 101.5, // now above -> cross up
            rsi = 40.0, rsiPrevious = 35.0, // inside 30-50, turning up
            price = 105.0, vwap = 100.0, // above vwap
            volume = 20.0, averageVolume = 10.0 // above average
        )
        val result = strategy.evaluate(snapshot)
        assertEquals(SignalType.BUY, result.type)
        assertEquals(4, result.confidenceScore)
        assertEquals(100, result.confidencePercent)
    }

    @Test
    fun `all four sell criteria met yields SELL with full confidence`() {
        val snapshot = baseSnapshot(
            emaShortPrevious = 101.0, emaMidPrevious = 100.0, // was above
            emaShort = 99.0, emaMid = 100.5, // now below -> cross down
            rsi = 60.0, rsiPrevious = 65.0, // inside 50-70, turning down
            price = 95.0, vwap = 100.0, // below vwap
            volume = 20.0, averageVolume = 10.0
        )
        val result = strategy.evaluate(snapshot)
        assertEquals(SignalType.SELL, result.type)
        assertEquals(4, result.confidenceScore)
    }

    @Test
    fun `no criteria met on either side yields NEUTRAL`() {
        val snapshot = baseSnapshot(
            emaShortPrevious = 100.0, emaMidPrevious = 100.0,
            emaShort = 100.0, emaMid = 100.0, // no crossover either way
            rsi = 50.0, rsiPrevious = 50.0, // exactly on the boundary, no movement
            price = 100.0, vwap = 100.0, // exactly at vwap
            volume = 5.0, averageVolume = 10.0 // below average
        )
        val result = strategy.evaluate(snapshot)
        assertEquals(SignalType.NEUTRAL, result.type)
        assertEquals(0, result.confidenceScore)
    }

    @Test
    fun `partial buy match reports the exact matched criteria`() {
        val snapshot = baseSnapshot(
            emaShortPrevious = 100.0, emaMidPrevious = 101.0,
            emaShort = 102.0, emaMid = 101.5, // cross up
            rsi = 40.0, rsiPrevious = 35.0, // turning up in band
            price = 95.0, vwap = 100.0, // below vwap: does NOT match
            volume = 5.0, averageVolume = 10.0 // below average: does NOT match
        )
        val result = strategy.evaluate(snapshot)
        assertEquals(SignalType.BUY, result.type)
        assertEquals(2, result.confidenceScore)
        assertTrue(result.matchedCriteria.contains(DefaultSignalStrategy.CRITERION_EMA_CROSS_UP))
        assertTrue(result.matchedCriteria.contains(DefaultSignalStrategy.CRITERION_RSI_TURN_UP))
        assertEquals(50, result.confidencePercent)
    }

    private fun baseSnapshot(
        emaShortPrevious: Double,
        emaMidPrevious: Double,
        emaShort: Double,
        emaMid: Double,
        rsi: Double,
        rsiPrevious: Double,
        price: Double,
        vwap: Double,
        volume: Double,
        averageVolume: Double
    ) = IndicatorSnapshot(
        price = price,
        emaShort = emaShort,
        emaShortPrevious = emaShortPrevious,
        emaMid = emaMid,
        emaMidPrevious = emaMidPrevious,
        emaLong = emaMid,
        rsi = rsi,
        rsiPrevious = rsiPrevious,
        macdLine = 0.0,
        macdLinePrevious = 0.0,
        macdSignal = 0.0,
        macdSignalPrevious = 0.0,
        macdHistogram = 0.0,
        bollingerUpper = price + 10,
        bollingerMiddle = price,
        bollingerLower = price - 10,
        atr = 1.0,
        vwap = vwap,
        volume = volume,
        averageVolume = averageVolume
    )
}
