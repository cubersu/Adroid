package com.adroid.cryptosignal.presentation.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adroid.cryptosignal.presentation.theme.TextSecondary

/**
 * A filled/empty dot readout of a strategy's confidence score — "●●●●○ 4/5 kriter" — the
 * instrument-panel feel traders expect instead of a bare percentage.
 */
@Composable
fun ConfidenceIndicator(
    matched: Int,
    total: Int,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        val dots = buildString {
            repeat(matched.coerceAtLeast(0)) { append('●') }
            repeat((total - matched).coerceAtLeast(0)) { append('○') }
        }
        Text(
            text = dots,
            color = LocalContentColor.current,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label ?: "$matched/$total kriter",
            color = TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
