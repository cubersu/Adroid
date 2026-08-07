package com.adroid.cryptosignal.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adroid.cryptosignal.domain.model.AppSettings
import com.adroid.cryptosignal.domain.model.IndicatorType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "adroid_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val CONFIDENCE_THRESHOLD = intPreferencesKey("confidence_threshold_percent")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val ENABLED_INDICATORS = stringSetPreferencesKey("enabled_indicators")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val enabledNames = prefs[Keys.ENABLED_INDICATORS]
        val enabledIndicators = enabledNames
            ?.mapNotNull { name -> runCatching { IndicatorType.valueOf(name) }.getOrNull() }
            ?.toSet()
            ?: IndicatorType.entries.toSet()

        AppSettings(
            confidenceThresholdPercent = prefs[Keys.CONFIDENCE_THRESHOLD] ?: 60,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            enabledIndicators = enabledIndicators
        )
    }

    suspend fun setConfidenceThreshold(percent: Int) {
        context.dataStore.edit { it[Keys.CONFIDENCE_THRESHOLD] = percent.coerceIn(0, 100) }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setIndicatorEnabled(indicator: IndicatorType, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.ENABLED_INDICATORS] ?: IndicatorType.entries.map { it.name }.toSet()
            prefs[Keys.ENABLED_INDICATORS] = if (enabled) current + indicator.name else current - indicator.name
        }
    }
}
