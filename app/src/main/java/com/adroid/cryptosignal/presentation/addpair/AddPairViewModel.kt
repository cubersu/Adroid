package com.adroid.cryptosignal.presentation.addpair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.repository.MarketDataRepository
import com.adroid.cryptosignal.domain.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddPairViewModel @Inject constructor(
    private val marketDataRepository: MarketDataRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPairUiState())
    val uiState: StateFlow<AddPairUiState> = _uiState.asStateFlow()

    init {
        loadPairs()
        watchlistRepository.observeWatchedPairs()
            .onEach { watched -> _uiState.update { it.copy(watchedSymbols = watched.map { p -> p.pair.symbol }.toSet()) } }
            .launchIn(viewModelScope)
    }

    fun loadPairs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            marketDataRepository.fetchExchangeInfo()
                .onSuccess { pairs -> _uiState.update { it.copy(isLoading = false, pairs = pairs) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun toggleWatched(symbol: String) {
        viewModelScope.launch {
            if (symbol in _uiState.value.watchedSymbols) {
                watchlistRepository.removePair(symbol)
            } else {
                watchlistRepository.addPair(symbol)
            }
        }
    }
}
