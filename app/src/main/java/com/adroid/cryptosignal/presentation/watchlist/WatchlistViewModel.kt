package com.adroid.cryptosignal.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.indicator.computeIndicatorSnapshot
import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.domain.model.Ticker
import com.adroid.cryptosignal.domain.model.WatchedPair
import com.adroid.cryptosignal.domain.repository.MarketDataRepository
import com.adroid.cryptosignal.domain.repository.SettingsRepository
import com.adroid.cryptosignal.domain.repository.SignalRepository
import com.adroid.cryptosignal.domain.repository.WatchlistRepository
import com.adroid.cryptosignal.domain.strategy.SignalStrategy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    private val marketDataRepository: MarketDataRepository,
    private val signalRepository: SignalRepository,
    private val settingsRepository: SettingsRepository,
    private val signalStrategy: SignalStrategy
) : ViewModel() {

    val uiState: StateFlow<WatchlistUiState> = combine(
        watchlistItemsFlow(),
        marketDataRepository.observeConnectionState()
    ) { items, connectionState ->
        WatchlistUiState(isLoading = false, items = items, connectionState = connectionState)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), WatchlistUiState())

    private fun watchlistItemsFlow(): Flow<List<WatchlistItem>> =
        watchlistRepository.observeWatchedPairs().flatMapLatest { pairs ->
            if (pairs.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(pairs.map { watchlistItemFlow(it) }) { items -> items.toList() }
            }
        }

    /**
     * [WatchlistItem.currentStatus] reflects what the strategy says *right now* (including
     * NEUTRAL), computed live from the candle buffer — not just the last BUY/SELL that was
     * strong enough to be persisted/notified. Without this, the badge would keep showing a
     * stale AL/SAT long after the market moved back to neutral, since NEUTRAL evaluations are
     * never saved to history by design.
     */
    private fun watchlistItemFlow(watched: WatchedPair): Flow<WatchlistItem> {
        val symbol = watched.pair.symbol
        val tickerFlow: Flow<Ticker?> = marketDataRepository.observeTicker(symbol)
            .map<Ticker, Ticker?> { it }
            .onStart { emit(null) }

        return combine(
            tickerFlow,
            signalRepository.observeLatestSignal(symbol),
            marketDataRepository.observeCandleBuffer(symbol),
            settingsRepository.observeSettings()
        ) { ticker, latestSignal, candles, settings ->
            val snapshot = computeIndicatorSnapshot(candles, settings.indicatorConfig)
            val currentStatus = snapshot?.let { signalStrategy.evaluate(it).type } ?: SignalType.NEUTRAL
            WatchlistItem(
                pair = watched.pair,
                lastPrice = ticker?.last,
                dailyPercent = ticker?.dailyPercent,
                currentStatus = currentStatus,
                latestSignal = latestSignal,
                sparklinePrices = candles.takeLast(SPARKLINE_CANDLE_COUNT).map { it.close }
            )
        }
    }

    fun removePair(symbol: String) {
        viewModelScope.launch { watchlistRepository.removePair(symbol) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
        const val SPARKLINE_CANDLE_COUNT = 30
    }
}
