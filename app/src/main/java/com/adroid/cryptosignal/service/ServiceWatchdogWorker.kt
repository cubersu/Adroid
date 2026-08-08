package com.adroid.cryptosignal.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Periodic watchdog that restarts [SignalMonitorService] if the OS has killed it. */
@HiltWorker
class ServiceWatchdogWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        SignalMonitorService.start(applicationContext)
        return Result.success()
    }

    companion object {
        const val UNIQUE_WORK_NAME = "signal_monitor_watchdog"
    }
}
