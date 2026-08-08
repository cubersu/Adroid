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
        val buyMatches = buyChecks(snapshot).filter { it.isMet }.map { it.label }
        val sellMatches = sellChecks(snapshot).filter { it.isMet }.map { it.label }

        return when {
            buyMatches.size > sellMatches.size -> StrategyEvaluation(SignalType.BUY, buyMatches, TOTAL_CRITERIA)
            sellMatches.size > buyMatches.size -> StrategyEvaluation(SignalType.SELL, sellMatches, TOTAL_CRITERIA)
            else -> StrategyEvaluation(SignalType.NEUTRAL, emptyList(), TOTAL_CRITERIA)
        }
    }

    override fun describeCriteria(snapshot: IndicatorSnapshot): SignalCriteriaBreakdown =
        SignalCriteriaBreakdown(buyChecks(snapshot), sellChecks(snapshot))

    private fun buyChecks(s: IndicatorSnapshot): List<CriterionCheck> = listOf(
        CriterionCheck(CRITERION_EMA_CROSS_UP, s.emaShortPrevious <= s.emaMidPrevious && s.emaShort > s.emaMid),
        CriterionCheck(CRITERION_RSI_TURN_UP, s.rsi in RSI_BUY_BAND && s.rsi > s.rsiPrevious),
        CriterionCheck(CRITERION_PRICE_ABOVE_VWAP, s.price > s.vwap),
        CriterionCheck(CRITERION_VOLUME_ABOVE_AVERAGE, s.volume > s.averageVolume)
    )

    private fun sellChecks(s: IndicatorSnapshot): List<CriterionCheck> = listOf(
        CriterionCheck(CRITERION_EMA_CROSS_DOWN, s.emaShortPrevious >= s.emaMidPrevious && s.emaShort < s.emaMid),
        CriterionCheck(CRITERION_RSI_TURN_DOWN, s.rsi in RSI_SELL_BAND && s.rsi < s.rsiPrevious),
        CriterionCheck(CRITERION_PRICE_BELOW_VWAP, s.price < s.vwap),
        CriterionCheck(CRITERION_VOLUME_ABOVE_AVERAGE, s.volume > s.averageVolume)
    )

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
