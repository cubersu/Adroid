package com.adroid.cryptosignal.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.adroid.cryptosignal.R

sealed class Destination(val route: String) {
    data object Watchlist : Destination("watchlist")
    data object AddPair : Destination("add_pair")
    data object History : Destination("history")
    data object Settings : Destination("settings")
}

data class BottomTab(val destination: Destination, val icon: ImageVector, val labelRes: Int)

val bottomTabs = listOf(
    BottomTab(Destination.Watchlist, Icons.Filled.ShowChart, R.string.nav_watchlist),
    BottomTab(Destination.History, Icons.Filled.History, R.string.nav_history),
    BottomTab(Destination.Settings, Icons.Filled.Settings, R.string.nav_settings)
)
