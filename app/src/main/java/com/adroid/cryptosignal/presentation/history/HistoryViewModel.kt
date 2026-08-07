package com.adroid.cryptosignal.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.domain.repository.SignalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HistoryViewModel @Inject constructor(
    signalRepository: SignalRepository
) : ViewModel() {

    val history: StateFlow<List<TradeSignal>> = signalRepository.observeSignalHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
