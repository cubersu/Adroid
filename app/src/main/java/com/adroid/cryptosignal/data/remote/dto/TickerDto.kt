package com.adroid.cryptosignal.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TickerResponseDto(
    val data: List<TickerDto> = emptyList(),
    val success: Boolean = false,
    val message: String? = null,
    val code: Int = 0
)

@Serializable
data class TickerDto(
    val pair: String,
    val pairNormalized: String? = null,
    val timestamp: Long = 0L,
    val last: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val bid: Double = 0.0,
    val ask: Double = 0.0,
    val open: Double = 0.0,
    val volume: Double = 0.0,
    val average: Double = 0.0,
    val daily: Double = 0.0,
    val dailyPercent: Double = 0.0,
    val denominatorSymbol: String? = null,
    val numeratorSymbol: String? = null
)
