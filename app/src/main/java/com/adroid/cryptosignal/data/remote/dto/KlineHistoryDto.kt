package com.adroid.cryptosignal.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * TradingView UDF-compatible response from graph-api.btcturk.com/v1/klines/history.
 * Parallel arrays: t=open time (unix seconds), o/h/l/c=OHLC, v=volume. [status] is "ok" when
 * data is present, "no_data" when the range is empty.
 */
@Serializable
data class KlineHistoryResponseDto(
    val s: String = "no_data",
    val t: List<Long> = emptyList(),
    val o: List<Double> = emptyList(),
    val h: List<Double> = emptyList(),
    val l: List<Double> = emptyList(),
    val c: List<Double> = emptyList(),
    val v: List<Double> = emptyList()
)
