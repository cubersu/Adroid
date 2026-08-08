package com.adroid.cryptosignal.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.presentation.common.PriceText
import com.adroid.cryptosignal.presentation.theme.BuyGreen
import com.adroid.cryptosignal.presentation.theme.SellRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onSignalClick: (Long) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.filter == HistoryFilter.ALL,
                onClick = { viewModel.setFilter(HistoryFilter.ALL) },
                label = { Text("Tümü") }
            )
            FilterChip(
                selected = uiState.filter == HistoryFilter.BUY_ONLY,
                onClick = { viewModel.setFilter(HistoryFilter.BUY_ONLY) },
                label = { Text("Sadece AL") }
            )
            FilterChip(
                selected = uiState.filter == HistoryFilter.SELL_ONLY,
                onClick = { viewModel.setFilter(HistoryFilter.SELL_ONLY) },
                label = { Text("Sadece SAT") }
            )
        }

        val filteredSignals = uiState.filteredSignals
        if (filteredSignals.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.history_empty), style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredSignals, key = { it.id }) { signal ->
                    HistoryRow(signal = signal, onClick = { onSignalClick(signal.id) })
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(signal: TradeSignal, onClick: () -> Unit) {
    val (icon, tint) = if (signal.type == SignalType.SELL) {
        Icons.Filled.ArrowDownward to SellRed
    } else {
        Icons.Filled.ArrowUpward to BuyGreen
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PriceText(
                text = formatTime(signal.timestampMillis),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 12.dp)
            )
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(end = 8.dp))
            Text(
                signal.pairSymbol,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            PriceText(
                text = formatPrice(signal.price),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatPrice(value: Double): String = String.format(Locale.getDefault(), "%,.2f", value)

private fun formatTime(millis: Long): String =
    SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(Date(millis))
