package com.adroid.cryptosignal.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.repository.SignalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HistoryViewModel @Inject constructor(
    signalRepository: SignalRepository
) : ViewModel() {

    private val filter = MutableStateFlow(HistoryFilter.ALL)

    val uiState: StateFlow<HistoryUiState> = combine(
        signalRepository.observeSignalHistory(),
        filter
    ) { signals, currentFilter ->
        HistoryUiState(allSignals = signals, filter = currentFilter)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HistoryUiState())

    fun setFilter(newFilter: HistoryFilter) {
        filter.value = newFilter
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
