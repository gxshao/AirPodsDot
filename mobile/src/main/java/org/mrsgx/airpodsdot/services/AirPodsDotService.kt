package org.mrsgx.airpodsdot.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.os.HandlerThread


class AirPodsDotService : Service() {

    private val mChannelId: String = "AirPodsDotChannel"
    private val mHandlerThread:HandlerThread = HandlerThread("AirPodsDotMainThread")
    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()
        mHandlerThread.start()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel =
            NotificationChannel(mChannelId, packageName, NotificationManager.IMPORTANCE_NONE)
        channel.vibrationPattern = null
        channel.setShowBadge(false)

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

        startForeground(100, Notification.Builder(this, mChannelId)
            .setSmallIcon(android.R.drawable.star_on)
            .build())
        return START_STICKY
    }
}
