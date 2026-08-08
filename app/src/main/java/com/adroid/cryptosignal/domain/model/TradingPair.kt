package com.adroid.cryptosignal.domain.model

/**
 * A tradeable pair as listed by BtcTurk's exchange info endpoint, e.g. BTCUSDT.
 */
data class TradingPair(
    val symbol: String,
    val numerator: String,
    val denominator: String,
    val displayName: String
)

/**
 * A pair the user has chosen to watch, plus whether the monitoring service should be
 * actively streaming/evaluating it right now.
 */
data class WatchedPair(
    val pair: TradingPair,
    val isActive: Boolean = true,
    val addedAtMillis: Long = System.currentTimeMillis()
)
