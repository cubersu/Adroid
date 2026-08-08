package com.adroid.cryptosignal.presentation.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.presentation.common.SignalBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    if (history.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.history_empty), style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history, key = { it.id }) { signal -> HistoryRow(signal) }
        }
    }
}

@Composable
private fun HistoryRow(signal: TradeSignal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(signal.pairSymbol, style = MaterialTheme.typography.titleMedium)
                    Text(formatTimestamp(signal.timestampMillis), style = MaterialTheme.typography.bodySmall)
                    Text(
                        "Fiyat: ${signal.price} · Güven: %${(signal.confidenceRatio * 100).toInt()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                SignalBadge(type = signal.type)
            }
            if (signal.matchedCriteria.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(stringResource(R.string.history_reason_title), style = MaterialTheme.typography.labelMedium)
                    signal.matchedCriteria.forEach { criterion ->
                        Text("• $criterion", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(millis))
