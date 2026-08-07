package com.adroid.cryptosignal.data.repository

import com.adroid.cryptosignal.data.local.dao.WatchedPairDao
import com.adroid.cryptosignal.data.local.entity.WatchedPairEntity
import com.adroid.cryptosignal.data.remote.BtcTurkApiService
import com.adroid.cryptosignal.domain.model.TradingPair
import com.adroid.cryptosignal.domain.model.WatchedPair
import com.adroid.cryptosignal.domain.repository.WatchlistRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class WatchlistRepositoryImpl @Inject constructor(
    private val dao: WatchedPairDao,
    private val apiService: BtcTurkApiService
) : WatchlistRepository {

    override fun observeWatchedPairs(): Flow<List<WatchedPair>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addPair(symbol: String) {
        val response = apiService.getExchangeInfo()
        val symbolDto = response.data?.symbols?.firstOrNull { it.name.equals(symbol, ignoreCase = true) }
        val numerator = symbolDto?.numerator ?: symbol.removeSuffix("TRY").removeSuffix("USDT")
        val denominator = symbolDto?.denominator ?: symbol.removePrefix(numerator)
        dao.insert(
            WatchedPairEntity(
                symbol = symbol,
                numerator = numerator,
                denominator = denominator,
                displayName = "$numerator/$denominator",
                isActive = true,
                addedAtMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removePair(symbol: String) {
        dao.deleteBySymbol(symbol)
    }

    override suspend fun setActive(symbol: String, isActive: Boolean) {
        dao.setActive(symbol, isActive)
    }

    private fun WatchedPairEntity.toDomain(): WatchedPair = WatchedPair(
        pair = TradingPair(symbol = symbol, numerator = numerator, denominator = denominator, displayName = displayName),
        isActive = isActive,
        addedAtMillis = addedAtMillis
    )
}
