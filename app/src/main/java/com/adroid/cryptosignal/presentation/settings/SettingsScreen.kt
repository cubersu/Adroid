package com.adroid.cryptosignal.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adroid.cryptosignal.R
import com.adroid.cryptosignal.domain.model.IndicatorType

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.settings_confidence_threshold), style = MaterialTheme.typography.titleMedium)
        Text("%${settings.confidenceThresholdPercent}", style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = settings.confidenceThresholdPercent.toFloat(),
            onValueChange = { viewModel.setConfidenceThreshold(it.toInt()) },
            valueRange = 0f..100f,
            steps = 19
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_notifications_enabled),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = settings.notificationsEnabled,
                onCheckedChange = { viewModel.setNotificationsEnabled(it) }
            )
        }

        Text(
            text = stringResource(R.string.settings_indicators_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        IndicatorType.entries.forEach { indicator ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(indicator.name, modifier = Modifier.weight(1f))
                Switch(
                    checked = indicator in settings.enabledIndicators,
                    onCheckedChange = { checked -> viewModel.setIndicatorEnabled(indicator, checked) }
                )
            }
        }
    }
}
