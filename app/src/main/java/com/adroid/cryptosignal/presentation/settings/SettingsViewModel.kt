package com.adroid.cryptosignal.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adroid.cryptosignal.domain.model.AppSettings
import com.adroid.cryptosignal.domain.model.IndicatorType
import com.adroid.cryptosignal.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setConfidenceThreshold(percent: Int) {
        viewModelScope.launch { settingsRepository.setConfidenceThreshold(percent) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotificationsEnabled(enabled) }
    }

    fun setIndicatorEnabled(indicator: IndicatorType, enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setIndicatorEnabled(indicator, enabled) }
    }
}
