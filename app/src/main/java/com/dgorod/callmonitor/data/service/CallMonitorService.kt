package com.dgorod.callmonitor.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_NONE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import com.dgorod.callmonitor.R
import com.dgorod.callmonitor.data.repository.ServerRepository
import org.koin.android.ext.android.inject

/**
 * Android foreground service handling call monitoring server through [ServerRepository].
 */
class CallMonitorService: Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "CallMonitorServiceChannel"
    }

    private val serverRepository: ServerRepository by inject()
    private val manager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, setupNotification())
        serverRepository.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serverRepository.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Make notification attached to foreground service.
     */
    private fun setupNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.general)
            val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_NONE)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(PRIORITY_LOW)
            .setContentTitle(CHANNEL_ID)
            .build()
    }
}