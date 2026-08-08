package com.adroid.cryptosignal.presentation.pairdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adroid.cryptosignal.domain.indicator.IndicatorSnapshot
import com.adroid.cryptosignal.domain.strategy.CriterionCheck
import com.adroid.cryptosignal.presentation.common.SignalBadge
import com.adroid.cryptosignal.presentation.theme.SignalBuyGreen
import com.adroid.cryptosignal.presentation.theme.SignalSellRed
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PairDetailScreen(
    onBackClick: () -> Unit,
    viewModel: PairDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.pairSymbol) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading || uiState.candles.isEmpty()) {
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
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            Card(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Şu anki durum", style = MaterialTheme.typography.titleSmall)
                    SignalBadge(type = uiState.liveSignalType)
                }
            }

            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    CandlestickChart(
                        candles = uiState.candles,
                        emaShortSeries = uiState.emaShortSeries,
                        emaMidSeries = uiState.emaMidSeries,
                        vwapSeries = uiState.vwapSeries,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.4f)
                    )
                    ChartLegend()
                }
            }

            uiState.snapshot?.let { snapshot ->
                IndicatorValuesCard(snapshot)
            }

            uiState.criteriaBreakdown?.let { breakdown ->
                CriteriaCard(
                    title = "AL Kriterleri",
                    accentColor = SignalBuyGreen,
                    checks = breakdown.buyChecks
                )
                CriteriaCard(
                    title = "SAT Kriterleri",
                    accentColor = SignalSellRed,
                    checks = breakdown.sellChecks
                )
            }

            uiState.latestSignal?.let { signal ->
                Card(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Son üretilen sinyal", style = MaterialTheme.typography.titleSmall)
                        SignalBadge(type = signal.type)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ChartLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LegendItem(color = EmaShortColor, label = "EMA9")
        LegendItem(color = EmaMidColor, label = "EMA21")
        LegendItem(color = VwapColor, label = "VWAP")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun IndicatorValuesCard(snapshot: IndicatorSnapshot) {
    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("İndikatör Değerleri", style = MaterialTheme.typography.titleSmall)
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            IndicatorRow("Fiyat", format(snapshot.price))
            IndicatorRow("EMA9", format(snapshot.emaShort))
            IndicatorRow("EMA21", format(snapshot.emaMid))
            IndicatorRow("EMA50", format(snapshot.emaLong))
            IndicatorRow("VWAP", format(snapshot.vwap))
            IndicatorRow("RSI(14)", format(snapshot.rsi))
            IndicatorRow("MACD", format(snapshot.macdLine))
            IndicatorRow("MACD Sinyal", format(snapshot.macdSignal))
            IndicatorRow("MACD Histogram", format(snapshot.macdHistogram))
            IndicatorRow("Bollinger Üst", format(snapshot.bollingerUpper))
            IndicatorRow("Bollinger Orta", format(snapshot.bollingerMiddle))
            IndicatorRow("Bollinger Alt", format(snapshot.bollingerLower))
            IndicatorRow("ATR(14)", format(snapshot.atr))
            IndicatorRow("Hacim", format(snapshot.volume))
            IndicatorRow("Ort. Hacim (5)", format(snapshot.averageVolume))
        }
    }
}

@Composable
private fun IndicatorRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CriteriaCard(title: String, accentColor: Color, checks: List<CriterionCheck>) {
    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = accentColor)
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            checks.forEach { check ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (check.isMet) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                        contentDescription = null,
                        tint = if (check.isMet) SignalBuyGreen else MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(check.label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

private fun format(value: Double): String =
    if (value.isNaN()) "—" else String.format(Locale.getDefault(), "%,.4f", value)
