package com.adroid.cryptosignal.domain.repository

import com.adroid.cryptosignal.domain.model.AppSettings
import com.adroid.cryptosignal.domain.model.IndicatorType
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setConfidenceThreshold(percent: Int)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setIndicatorEnabled(indicator: IndicatorType, enabled: Boolean)
    fun observeHasSeenDisclaimer(): Flow<Boolean>
    suspend fun setHasSeenDisclaimer()
}
