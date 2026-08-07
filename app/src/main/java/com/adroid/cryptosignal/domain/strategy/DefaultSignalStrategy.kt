package com.adroid.cryptosignal.domain.strategy

import com.adroid.cryptosignal.domain.indicator.IndicatorSnapshot
import com.adroid.cryptosignal.domain.model.SignalType
import javax.inject.Inject

/**
 * The default rule set. Four criteria are checked independently per direction, so partial
 * matches are possible — [StrategyEvaluation.confidenceScore] is how many held:
 *
 * BUY: EMA9 crosses EMA21 upward, RSI turns up inside 30-50, price is above VWAP, volume is
 * above the last 5 candles' average.
 *
 * SELL: the mirrored conditions (EMA9 crosses EMA21 downward, RSI turns down inside 50-70,
 * price below VWAP); the volume confirmation is the same check in both directions since above-
 * average volume confirms conviction on either a breakout or a breakdown.
 *
 * BUY and SELL are scored independently and the side with strictly more matches wins; a tie
 * (including 0-0) is NEUTRAL.
 */
class DefaultSignalStrategy @Inject constructor() : SignalStrategy {

    override val name: String = "default_ema_rsi_vwap_volume"

    override fun evaluate(snapshot: IndicatorSnapshot): StrategyEvaluation {
        val buyCriteria = buyCriteria(snapshot)
        val sellCriteria = sellCriteria(snapshot)

        return when {
            buyCriteria.size > sellCriteria.size -> StrategyEvaluation(SignalType.BUY, buyCriteria, TOTAL_CRITERIA)
            sellCriteria.size > buyCriteria.size -> StrategyEvaluation(SignalType.SELL, sellCriteria, TOTAL_CRITERIA)
            else -> StrategyEvaluation(SignalType.NEUTRAL, emptyList(), TOTAL_CRITERIA)
        }
    }

    private fun buyCriteria(s: IndicatorSnapshot): List<String> = buildList {
        if (s.emaShortPrevious <= s.emaMidPrevious && s.emaShort > s.emaMid) add(CRITERION_EMA_CROSS_UP)
        if (s.rsi in RSI_BUY_BAND && s.rsi > s.rsiPrevious) add(CRITERION_RSI_TURN_UP)
        if (s.price > s.vwap) add(CRITERION_PRICE_ABOVE_VWAP)
        if (s.volume > s.averageVolume) add(CRITERION_VOLUME_ABOVE_AVERAGE)
    }

    private fun sellCriteria(s: IndicatorSnapshot): List<String> = buildList {
        if (s.emaShortPrevious >= s.emaMidPrevious && s.emaShort < s.emaMid) add(CRITERION_EMA_CROSS_DOWN)
        if (s.rsi in RSI_SELL_BAND && s.rsi < s.rsiPrevious) add(CRITERION_RSI_TURN_DOWN)
        if (s.price < s.vwap) add(CRITERION_PRICE_BELOW_VWAP)
        if (s.volume > s.averageVolume) add(CRITERION_VOLUME_ABOVE_AVERAGE)
    }

    companion object {
        private const val TOTAL_CRITERIA = 4
        private val RSI_BUY_BAND = 30.0..50.0
        private val RSI_SELL_BAND = 50.0..70.0

        const val CRITERION_EMA_CROSS_UP = "EMA9, EMA21'i yukarı kesti"
        const val CRITERION_EMA_CROSS_DOWN = "EMA9, EMA21'i aşağı kesti"
        const val CRITERION_RSI_TURN_UP = "RSI 30-50 bandında yukarı dönüyor"
        const val CRITERION_RSI_TURN_DOWN = "RSI 50-70 bandında aşağı dönüyor"
        const val CRITERION_PRICE_ABOVE_VWAP = "Fiyat VWAP üzerinde"
        const val CRITERION_PRICE_BELOW_VWAP = "Fiyat VWAP altında"
        const val CRITERION_VOLUME_ABOVE_AVERAGE = "Hacim son 5 mum ortalamasının üzerinde"
    }
}
