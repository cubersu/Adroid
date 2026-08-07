package com.adroid.cryptosignal.domain.model

data class Ticker(
    val pairSymbol: String,
    val last: Double,
    val bid: Double,
    val ask: Double,
    val dailyPercent: Double,
    val volume: Double,
    val timestampMillis: Long
)
