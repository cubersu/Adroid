package com.adroid.cryptosignal.data.remote

import com.adroid.cryptosignal.data.remote.dto.KlineHistoryResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** https://graph-api.btcturk.com */
interface BtcTurkGraphApiService {

    @GET("v1/klines/history")
    suspend fun getKlineHistory(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String = "1",
        @Query("from") fromUnixSeconds: Long,
        @Query("to") toUnixSeconds: Long
    ): KlineHistoryResponseDto
}
