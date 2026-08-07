package com.adroid.cryptosignal.presentation.addpair

import com.adroid.cryptosignal.domain.model.TradingPair

data class AddPairUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val pairs: List<TradingPair> = emptyList(),
    val watchedSymbols: Set<String> = emptySet(),
    val errorMessage: String? = null
) {
    val filteredPairs: List<TradingPair>
        get() = if (query.isBlank()) {
            pairs
        } else {
            pairs.filter { it.symbol.contains(query, ignoreCase = true) || it.displayName.contains(query, ignoreCase = true) }
        }
}
