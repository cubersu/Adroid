package com.adroid.cryptosignal.domain.repository

import com.adroid.cryptosignal.domain.model.WatchedPair
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun observeWatchedPairs(): Flow<List<WatchedPair>>
    suspend fun addPair(symbol: String)
    suspend fun removePair(symbol: String)
    suspend fun setActive(symbol: String, isActive: Boolean)
}
