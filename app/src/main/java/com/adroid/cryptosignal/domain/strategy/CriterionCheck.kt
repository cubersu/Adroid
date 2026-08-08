package com.adroid.cryptosignal.domain.strategy

/** A single named condition and whether it currently holds, for UI transparency. */
data class CriterionCheck(val label: String, val isMet: Boolean)

data class SignalCriteriaBreakdown(
    val buyChecks: List<CriterionCheck>,
    val sellChecks: List<CriterionCheck>
)
