package com.adroid.cryptosignal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Restarts the monitoring service after a reboot so watched pairs keep generating signals
 * without the user having to reopen the app first.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            SignalMonitorService.start(context)
        }
    }
}
