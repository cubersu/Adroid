package com.adroid.cryptosignal.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adroid.cryptosignal.presentation.addpair.AddPairScreen
import com.adroid.cryptosignal.presentation.history.HistoryScreen
import com.adroid.cryptosignal.presentation.settings.SettingsScreen
import com.adroid.cryptosignal.presentation.watchlist.WatchlistScreen

@Composable
fun AdroidNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Destination.AddPair.route) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.destination.route,
                            onClick = {
                                navController.navigate(tab.destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Destination.Watchlist.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Destination.Watchlist.route) {
                WatchlistScreen(onAddPairClick = { navController.navigate(Destination.AddPair.route) })
            }
            composable(Destination.AddPair.route) {
                AddPairScreen(onBackClick = { navController.popBackStack() })
            }
            composable(Destination.History.route) {
                HistoryScreen()
            }
            composable(Destination.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
