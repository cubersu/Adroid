package com.adroid.cryptosignal.presentation.pairdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.indicator.computeIndicatorSnapshot
import com.adroid.cryptosignal.domain.indicator.ema
import com.adroid.cryptosignal.domain.indicator.vwap
import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.domain.repository.MarketDataRepository
import com.adroid.cryptosignal.domain.repository.SettingsRepository
import com.adroid.cryptosignal.domain.repository.SignalRepository
import com.adroid.cryptosignal.domain.strategy.SignalStrategy
import com.adroid.cryptosignal.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PairDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val marketDataRepository: MarketDataRepository,
    private val settingsRepository: SettingsRepository,
    private val signalRepository: SignalRepository,
    private val signalStrategy: SignalStrategy
) : ViewModel() {

    private val pairSymbol: String = checkNotNull(savedStateHandle[Destination.PairDetail.ARG_SYMBOL])

    val uiState: StateFlow<PairDetailUiState> = combine(
        marketDataRepository.observeCandleBuffer(pairSymbol),
        settingsRepository.observeSettings(),
        signalRepository.observeLatestSignal(pairSymbol),
        signalRepository.observeSignalHistory(pairSymbol)
    ) { candles, settings, latestSignal, history ->
        val closes = candles.map { it.close }
        val snapshot = computeIndicatorSnapshot(candles, settings.indicatorConfig)
        PairDetailUiState(
            isLoading = false,
            pairSymbol = pairSymbol,
            candles = candles,
            emaShortSeries = if (closes.isEmpty()) emptyList() else ema(closes, settings.indicatorConfig.emaShortPeriod),
            emaMidSeries = if (closes.isEmpty()) emptyList() else ema(closes, settings.indicatorConfig.emaMidPeriod),
            vwapSeries = if (candles.isEmpty()) emptyList() else vwap(candles),
            snapshot = snapshot,
            criteriaBreakdown = snapshot?.let { signalStrategy.describeCriteria(it) },
            liveSignalType = snapshot?.let { signalStrategy.evaluate(it).type } ?: SignalType.NEUTRAL,
            latestSignal = latestSignal,
            signalMarkers = history.map { SignalMarker(it.timestampMillis / 1000, it.type) }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        PairDetailUiState(isLoading = true, pairSymbol = pairSymbol)
    )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
