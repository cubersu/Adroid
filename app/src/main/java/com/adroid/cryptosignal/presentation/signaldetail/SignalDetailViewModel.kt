package com.adroid.cryptosignal.presentation.signaldetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.repository.SignalRepository
import com.adroid.cryptosignal.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SignalDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    signalRepository: SignalRepository
) : ViewModel() {

    private val signalId: Long = checkNotNull(savedStateHandle[Destination.SignalDetail.ARG_SIGNAL_ID])

    val uiState: StateFlow<SignalDetailUiState> = signalRepository.observeSignalById(signalId)
        .map { signal -> SignalDetailUiState(isLoading = false, signal = signal) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), SignalDetailUiState())

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
