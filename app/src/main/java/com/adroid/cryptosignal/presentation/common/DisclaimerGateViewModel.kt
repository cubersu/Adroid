package com.adroid.cryptosignal.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DisclaimerGateViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /** Null until DataStore's first value arrives — avoids a one-frame flash of the dialog. */
    val hasSeenDisclaimer: StateFlow<Boolean?> = settingsRepository.observeHasSeenDisclaimer()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

    fun onDisclaimerAcknowledged() {
        viewModelScope.launch { settingsRepository.setHasSeenDisclaimer() }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
