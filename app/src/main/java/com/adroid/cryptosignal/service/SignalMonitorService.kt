package com.adroid.cryptosignal.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.adroid.cryptosignal.domain.repository.MarketDataRepository
import com.adroid.cryptosignal.domain.repository.SettingsRepository
import com.adroid.cryptosignal.domain.repository.WatchlistRepository
import com.adroid.cryptosignal.domain.usecase.GenerateSignalUseCase
import com.adroid.cryptosignal.util.NotificationConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Keeps the shared BtcTurk websocket alive and, for every actively watched pair, folds new
 * candles into a rolling buffer and runs [GenerateSignalUseCase] on each update. Runs as a
 * foreground service so the connection survives while the app is backgrounded.
 */
@AndroidEntryPoint
class SignalMonitorService : Service() {

    @Inject lateinit var watchlistRepository: WatchlistRepository
    @Inject lateinit var marketDataRepository: MarketDataRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var generateSignalUseCase: GenerateSignalUseCase
    @Inject lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(SupervisorJob())
    private val pairJobs = mutableMapOf<String, Job>()

    override fun onCreate() {
        super.onCreate()
        startAsForeground(activePairCount = 0)
        observeWatchlist()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startAsForeground(activePairCount: Int) {
        val notification = notificationHelper.buildForegroundNotification(activePairCount)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NotificationConstants.FOREGROUND_SERVICE_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NotificationConstants.FOREGROUND_SERVICE_NOTIFICATION_ID, notification)
        }
    }

    private fun updateForegroundNotification(activePairCount: Int) {
        val notification = notificationHelper.buildForegroundNotification(activePairCount)
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NotificationConstants.FOREGROUND_SERVICE_NOTIFICATION_ID, notification)
    }

    private fun observeWatchlist() {
        serviceScope.launch {
            watchlistRepository.observeWatchedPairs().collect { watched ->
                val activeSymbols = watched.filter { it.isActive }.map { it.pair.symbol }.toSet()

                val symbolsToStop = pairJobs.keys - activeSymbols
                symbolsToStop.forEach { symbol -> pairJobs.remove(symbol)?.cancel() }

                val symbolsToStart = activeSymbols - pairJobs.keys
                symbolsToStart.forEach { symbol -> pairJobs[symbol] = launchPairMonitor(symbol) }

                updateForegroundNotification(activeSymbols.size)
            }
        }
    }

    private fun launchPairMonitor(symbol: String): Job = serviceScope.launch {
        marketDataRepository.observeCandleBuffer(symbol).collect { candles ->
            val settings = settingsRepository.observeSettings().first()
            val signal = generateSignalUseCase(symbol, candles, settings.indicatorConfig)
            if (signal != null && settings.notificationsEnabled) {
                notificationHelper.showSignalNotification(signal)
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SignalMonitorService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
