package com.adroid.cryptosignal.domain.strategy

import com.adroid.cryptosignal.domain.model.SignalType

data class StrategyEvaluation(
    val type: SignalType,
    val matchedCriteria: List<String>,
    val totalCriteria: Int
) {
    val confidenceScore: Int get() = matchedCriteria.size

    val confidencePercent: Int
        get() = if (totalCriteria == 0) 0 else (matchedCriteria.size * 100) / totalCriteria
}
