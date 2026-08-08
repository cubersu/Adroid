package com.adroid.cryptosignal.domain.model

/**
 * User-configurable settings. [confidenceThresholdPercent] is the minimum confidence ratio
 * (0-100) a signal must reach before it is persisted/notified.
 */
data class AppSettings(
    val confidenceThresholdPercent: Int = 60,
    val notificationsEnabled: Boolean = true,
    val enabledIndicators: Set<IndicatorType> = IndicatorType.entries.toSet(),
    val indicatorConfig: IndicatorConfig = IndicatorConfig()
)
