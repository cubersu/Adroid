package com.adroid.cryptosignal.presentation.signaldetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adroid.cryptosignal.presentation.common.ConfidenceIndicator
import com.adroid.cryptosignal.presentation.common.PriceText
import com.adroid.cryptosignal.presentation.common.SignalBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SignalDetailScreen(
    onBackClick: () -> Unit,
    viewModel: SignalDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sinyal Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        val signal = uiState.signal
        if (uiState.isLoading || signal == null) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SignalBadge(type = signal.type, large = true)

                    Text(
                        text = signal.pairSymbol,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    PriceText(
                        text = formatPrice(signal.price),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    PriceText(
                        text = formatTimestamp(signal.timestampMillis),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    ConfidenceIndicator(
                        matched = signal.confidenceScore,
                        total = signal.totalCriteria,
                        modifier = Modifier.padding(top = 20.dp)
                    )

                    if (signal.matchedCriteria.isNotEmpty()) {
                        Text(
                            text = "Tetiklenen indikatörler",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                        )
                        FlowRow(modifier = Modifier.fillMaxWidth()) {
                            signal.matchedCriteria.forEach { criterion ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(criterion) },
                                    modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatPrice(value: Double): String = String.format(Locale.getDefault(), "%,.2f", value)

private fun formatTimestamp(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(millis))
