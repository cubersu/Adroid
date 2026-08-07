package com.adroid.cryptosignal.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.model.Ticker
import com.adroid.cryptosignal.domain.model.WatchedPair
import com.adroid.cryptosignal.domain.repository.MarketDataRepository
import com.adroid.cryptosignal.domain.repository.SignalRepository
import com.adroid.cryptosignal.domain.repository.WatchlistRepository
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
    private val signalRepository: SignalRepository
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

    private fun watchlistItemFlow(watched: WatchedPair): Flow<WatchlistItem> {
        val tickerFlow: Flow<Ticker?> = marketDataRepository.observeTicker(watched.pair.symbol)
            .map<Ticker, Ticker?> { it }
            .onStart { emit(null) }

        return combine(tickerFlow, signalRepository.observeLatestSignal(watched.pair.symbol)) { ticker, latestSignal ->
            WatchlistItem(
                pair = watched.pair,
                lastPrice = ticker?.last,
                dailyPercent = ticker?.dailyPercent,
                latestSignal = latestSignal
            )
        }
    }

    fun removePair(symbol: String) {
        viewModelScope.launch { watchlistRepository.removePair(symbol) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
