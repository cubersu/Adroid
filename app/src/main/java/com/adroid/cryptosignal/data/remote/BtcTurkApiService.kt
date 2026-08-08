package com.adroid.cryptosignal.data.remote

import com.adroid.cryptosignal.data.remote.dto.ExchangeInfoResponseDto
import com.adroid.cryptosignal.data.remote.dto.TickerResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** https://api.btcturk.com */
interface BtcTurkApiService {

    @GET("api/v2/ticker")
    suspend fun getTicker(@Query("pairSymbol") pairSymbol: String? = null): TickerResponseDto

    @GET("api/v2/server/exchangeinfo")
    suspend fun getExchangeInfo(): ExchangeInfoResponseDto
}
