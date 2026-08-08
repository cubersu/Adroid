package com.adroid.cryptosignal.presentation.signaldetail

import com.adroid.cryptosignal.domain.model.TradeSignal

data class SignalDetailUiState(
    val isLoading: Boolean = true,
    val signal: TradeSignal? = null
)
