package com.adroid.cryptosignal.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adroid.cryptosignal.presentation.addpair.AddPairScreen
import com.adroid.cryptosignal.presentation.history.HistoryScreen
import com.adroid.cryptosignal.presentation.pairdetail.PairDetailScreen
import com.adroid.cryptosignal.presentation.settings.SettingsScreen
import com.adroid.cryptosignal.presentation.signaldetail.SignalDetailScreen
import com.adroid.cryptosignal.presentation.watchlist.WatchlistScreen

@Composable
fun AdroidNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val hideBottomBarRoutes = setOf(
                Destination.AddPair.route,
                Destination.PairDetail.route,
                Destination.SignalDetail.route
            )
            if (currentRoute !in hideBottomBarRoutes) {
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
                WatchlistScreen(
                    onAddPairClick = { navController.navigate(Destination.AddPair.route) },
                    onPairClick = { symbol -> navController.navigate(Destination.PairDetail.createRoute(symbol)) }
                )
            }
            composable(Destination.AddPair.route) {
                AddPairScreen(onBackClick = { navController.popBackStack() })
            }
            composable(
                route = Destination.PairDetail.route,
                arguments = listOf(navArgument(Destination.PairDetail.ARG_SYMBOL) { type = NavType.StringType })
            ) {
                PairDetailScreen(onBackClick = { navController.popBackStack() })
            }
            composable(Destination.History.route) {
                HistoryScreen(onSignalClick = { id -> navController.navigate(Destination.SignalDetail.createRoute(id)) })
            }
            composable(
                route = Destination.SignalDetail.route,
                arguments = listOf(navArgument(Destination.SignalDetail.ARG_SIGNAL_ID) { type = NavType.LongType })
            ) {
                SignalDetailScreen(onBackClick = { navController.popBackStack() })
            }
            composable(Destination.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
