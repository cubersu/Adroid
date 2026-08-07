package com.adroid.cryptosignal.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeInfoResponseDto(
    val data: ExchangeInfoDataDto? = null,
    val success: Boolean = false,
    val message: String? = null,
    val code: Int = 0
)

@Serializable
data class ExchangeInfoDataDto(
    val timeZone: String? = null,
    val serverTime: Long = 0L,
    val symbols: List<SymbolDto> = emptyList()
)

@Serializable
data class SymbolDto(
    val id: Int = 0,
    val name: String,
    val nameNormalized: String? = null,
    val status: String? = null,
    val numerator: String = "",
    val denominator: String = ""
)
