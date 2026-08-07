package com.adroid.cryptosignal.domain.model

/**
 * A generated BUY/SELL signal. [confidenceScore] is the number of criteria that matched
 * (out of [totalCriteria]) for the strategy that produced it; [matchedCriteria] describes
 * which ones, for display in the history screen.
 */
data class TradeSignal(
    val id: Long = 0,
    val pairSymbol: String,
    val type: SignalType,
    val price: Double,
    val confidenceScore: Int,
    val totalCriteria: Int,
    val matchedCriteria: List<String>,
    val strategyName: String,
    val timestampMillis: Long
) {
    val confidenceRatio: Float
        get() = if (totalCriteria == 0) 0f else confidenceScore.toFloat() / totalCriteria
}
