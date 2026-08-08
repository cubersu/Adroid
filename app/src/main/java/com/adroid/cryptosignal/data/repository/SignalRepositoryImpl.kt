package com.adroid.cryptosignal.data.repository

import com.adroid.cryptosignal.data.local.dao.TradeSignalDao
import com.adroid.cryptosignal.data.local.entity.TradeSignalEntity
import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.domain.repository.SignalRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SignalRepositoryImpl @Inject constructor(
    private val dao: TradeSignalDao
) : SignalRepository {

    override fun observeSignalHistory(pairSymbol: String?): Flow<List<TradeSignal>> {
        val entitiesFlow = if (pairSymbol == null) dao.observeAll() else dao.observeForPair(pairSymbol)
        return entitiesFlow.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun saveSignal(signal: TradeSignal) {
        dao.insert(signal.toEntity())
    }

    override fun observeLatestSignal(pairSymbol: String): Flow<TradeSignal?> =
        dao.observeLatestForPair(pairSymbol).map { it?.toDomain() }

    override fun observeSignalById(id: Long): Flow<TradeSignal?> =
        dao.observeById(id).map { it?.toDomain() }

    private fun TradeSignalEntity.toDomain(): TradeSignal = TradeSignal(
        id = id,
        pairSymbol = pairSymbol,
        type = SignalType.valueOf(type),
        price = price,
        confidenceScore = confidenceScore,
        totalCriteria = totalCriteria,
        matchedCriteria = if (matchedCriteria.isBlank()) emptyList() else matchedCriteria.split(CRITERIA_DELIMITER),
        strategyName = strategyName,
        timestampMillis = timestampMillis
    )

    private fun TradeSignal.toEntity(): TradeSignalEntity = TradeSignalEntity(
        pairSymbol = pairSymbol,
        type = type.name,
        price = price,
        confidenceScore = confidenceScore,
        totalCriteria = totalCriteria,
        matchedCriteria = matchedCriteria.joinToString(CRITERIA_DELIMITER),
        strategyName = strategyName,
        timestampMillis = timestampMillis
    )

    private companion object {
        const val CRITERIA_DELIMITER = "|"
    }
}
