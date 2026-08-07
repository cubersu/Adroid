package com.adroid.cryptosignal.domain.usecase

import com.adroid.cryptosignal.domain.indicator.computeIndicatorSnapshot
import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.IndicatorConfig
import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.domain.repository.SettingsRepository
import com.adroid.cryptosignal.domain.repository.SignalRepository
import com.adroid.cryptosignal.domain.strategy.SignalStrategy
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Evaluates the latest candle for [pairSymbol] against the active [SignalStrategy]. Persists
 * and returns a [TradeSignal] only when the strategy picked a direction (not NEUTRAL) and its
 * confidence meets the user's configured threshold; otherwise returns null and nothing is
 * saved or notified.
 */
class GenerateSignalUseCase @Inject constructor(
    private val strategy: SignalStrategy,
    private val signalRepository: SignalRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(
        pairSymbol: String,
        candles: List<Candle>,
        indicatorConfig: IndicatorConfig
    ): TradeSignal? {
        val snapshot = computeIndicatorSnapshot(candles, indicatorConfig) ?: return null
        val evaluation = strategy.evaluate(snapshot)
        if (evaluation.type == SignalType.NEUTRAL) return null

        val settings = settingsRepository.observeSettings().first()
        if (evaluation.confidencePercent < settings.confidenceThresholdPercent) return null

        val signal = TradeSignal(
            pairSymbol = pairSymbol,
            type = evaluation.type,
            price = snapshot.price,
            confidenceScore = evaluation.confidenceScore,
            totalCriteria = evaluation.totalCriteria,
            matchedCriteria = evaluation.matchedCriteria,
            strategyName = strategy.name,
            timestampMillis = System.currentTimeMillis()
        )
        signalRepository.saveSignal(signal)
        return signal
    }
}
