package com.adroid.cryptosignal.domain.strategy

import com.adroid.cryptosignal.domain.indicator.IndicatorSnapshot

/**
 * A pluggable rule set that turns an [IndicatorSnapshot] into a [StrategyEvaluation]. Additional
 * strategies can be added later by implementing this interface and swapping the Hilt binding;
 * [DefaultSignalStrategy] is the one currently wired in.
 */
interface SignalStrategy {
    val name: String
    fun evaluate(snapshot: IndicatorSnapshot): StrategyEvaluation
}
