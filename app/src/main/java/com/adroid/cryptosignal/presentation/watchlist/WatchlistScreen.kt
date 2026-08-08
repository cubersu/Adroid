package com.adroid.cryptosignal.presentation.watchlist

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.adroid.cryptosignal.presentation.common.DisclaimerBanner
import com.adroid.cryptosignal.presentation.common.SignalBadge
import java.util.Locale

@Composable
fun WatchlistScreen(
    onAddPairClick: () -> Unit,
    onPairClick: (String) -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPairClick) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.watchlist_add_pair))
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            DisclaimerBanner()
            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                uiState.items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.watchlist_empty), style = MaterialTheme.typography.bodyMedium)
                }
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.items, key = { it.pair.symbol }) { item ->
                        WatchlistRow(
                            item = item,
                            onClick = { onPairClick(item.pair.symbol) },
                            onRemove = { viewModel.removePair(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistRow(item: WatchlistItem, onClick: () -> Unit, onRemove: (String) -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.pair.displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = item.lastPrice?.let { formatPrice(it) } ?: "—",
                    style = MaterialTheme.typography.bodyMedium
                )
                item.dailyPercent?.let { percent ->
                    Text(
                        text = String.format(Locale.getDefault(), "%+.2f%%", percent),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            SignalBadge(type = item.latestSignal?.type ?: SignalType.NEUTRAL)
            IconButton(onClick = { onRemove(item.pair.symbol) }) {
                Icon(Icons.Filled.Delete, contentDescription = null)
            }
        }
    }
}

private fun formatPrice(value: Double): String = String.format(Locale.getDefault(), "%,.2f", value)
