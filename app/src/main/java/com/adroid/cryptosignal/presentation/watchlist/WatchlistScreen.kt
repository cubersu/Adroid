package com.adroid.cryptosignal.presentation.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adroid.cryptosignal.R
import com.adroid.cryptosignal.presentation.common.DisclaimerBanner
import com.adroid.cryptosignal.presentation.common.PriceText
import com.adroid.cryptosignal.presentation.common.SignalAccentStripe
import com.adroid.cryptosignal.presentation.common.SignalBadge
import com.adroid.cryptosignal.presentation.common.Sparkline
import com.adroid.cryptosignal.presentation.common.rememberPriceFlashColor
import com.adroid.cryptosignal.presentation.theme.BuyGreen
import com.adroid.cryptosignal.presentation.theme.SellRed
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    onAddPairClick: () -> Unit,
    onPairClick: (String) -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onAddPairClick) {
                        Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.watchlist_add_pair))
                    }
                }
            )
        },
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
    val flashColor = rememberPriceFlashColor(item.lastPrice)

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
                .height(IntrinsicSize.Min)
                .background(flashColor)
        ) {
            SignalAccentStripe(
                type = item.latestSignal?.type,
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.pair.displayName, style = MaterialTheme.typography.titleMedium)
                        PriceText(
                            text = item.lastPrice?.let { formatPrice(it) } ?: "—",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        item.dailyPercent?.let { percent ->
                            PriceText(
                                text = String.format(Locale.getDefault(), "%+.2f%%", percent),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (percent >= 0) BuyGreen else SellRed
                            )
                        }
                    }
                    SignalBadge(type = item.currentStatus)
                    IconButton(onClick = { onRemove(item.pair.symbol) }) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                    }
                }
                if (item.sparklinePrices.size >= 2) {
                    Sparkline(
                        values = item.sparklinePrices,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun formatPrice(value: Double): String = String.format(Locale.getDefault(), "%,.2f", value)
