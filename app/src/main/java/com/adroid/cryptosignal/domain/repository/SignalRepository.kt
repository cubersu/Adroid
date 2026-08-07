package com.adroid.cryptosignal.domain.repository

import com.adroid.cryptosignal.domain.model.TradeSignal
import kotlinx.coroutines.flow.Flow

interface SignalRepository {
    fun observeSignalHistory(pairSymbol: String? = null): Flow<List<TradeSignal>>
    suspend fun saveSignal(signal: TradeSignal)
    fun observeLatestSignal(pairSymbol: String): Flow<TradeSignal?>
}
