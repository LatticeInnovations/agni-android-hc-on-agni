package com.heartcare.agni.service.workmanager.workers.trigger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.heartcare.agni.FhirApp
import com.heartcare.agni.R
import com.heartcare.agni.service.workmanager.workers.base.SyncWorker

abstract class TriggerWorkerPeriodic(context: Context, workerParameters: WorkerParameters) :
    SyncWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        (applicationContext as FhirApp).launchSyncingInternal()
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {

        val channelId = "sync_channel"

        val channel = NotificationChannel(
            channelId,
            "Background Sync",
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager =
            applicationContext.getSystemService(
                NotificationManager::class.java
            )

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setContentTitle("Syncing data")
            .setContentText("Please wait while data sync completes")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSilent(true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                1001,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                1001,
                notification
            )
        }
    }
}