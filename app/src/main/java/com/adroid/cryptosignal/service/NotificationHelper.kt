package com.adroid.cryptosignal.service

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.adroid.cryptosignal.R
import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.domain.model.TradeSignal
import com.adroid.cryptosignal.util.NotificationConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun buildForegroundNotification(activePairCount: Int): Notification =
        NotificationCompat.Builder(context, NotificationConstants.SIGNAL_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("$activePairCount parite izleniyor")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    fun showSignalNotification(signal: TradeSignal) {
        val directionLabel = when (signal.type) {
            SignalType.BUY -> context.getString(R.string.signal_buy)
            SignalType.SELL -> context.getString(R.string.signal_sell)
            SignalType.NEUTRAL -> context.getString(R.string.signal_none)
        }
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(signal.timestampMillis))
        val confidencePercent = (signal.confidenceRatio * 100).toInt()

        val notification = NotificationCompat.Builder(context, NotificationConstants.SIGNAL_CHANNEL_ID)
            .setContentTitle("${signal.pairSymbol} · $directionLabel")
            .setContentText("Fiyat: ${signal.price} · Güven: %$confidencePercent · $time")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (hasNotificationPermission()) {
            val notificationId = NotificationConstants.SIGNAL_NOTIFICATION_ID_BASE + signal.pairSymbol.hashCode()
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }
}
