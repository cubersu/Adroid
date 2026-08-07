package com.adroid.cryptosignal.data.repository

import com.adroid.cryptosignal.data.local.datastore.SettingsDataStore
import com.adroid.cryptosignal.domain.model.AppSettings
import com.adroid.cryptosignal.domain.model.IndicatorType
import com.adroid.cryptosignal.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> = dataStore.settingsFlow

    override suspend fun setConfidenceThreshold(percent: Int) {
        dataStore.setConfidenceThreshold(percent)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.setNotificationsEnabled(enabled)
    }

    override suspend fun setIndicatorEnabled(indicator: IndicatorType, enabled: Boolean) {
        dataStore.setIndicatorEnabled(indicator, enabled)
    }
}
