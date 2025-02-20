package com.w174rd.syncuinotifactivity.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.w174rd.syncuinotifactivity.utils.Attributes
import com.w174rd.syncuinotifactivity.utils.Attributes.pref.key.progressKeyPref
import com.w174rd.syncuinotifactivity.utils.Attributes.pref.progressPrefs
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.ACTION_MINUS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.ACTION_PLUS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.EXTRA_PROGRESS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.UPDATE_PROGRESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class ForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var notificationManager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var sharedPreferences: SharedPreferences

    private val activityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val progressChange = intent?.getIntExtra(EXTRA_PROGRESS, 0) ?: 0
            if (progressChange != 0) {
                updateNotification(progressChange)
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        LocalBroadcastManager.getInstance(this).registerReceiver(activityReceiver, IntentFilter(UPDATE_PROGRESS))

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        sharedPreferences = getSharedPreferences(progressPrefs, Context.MODE_PRIVATE)
        val progress = sharedPreferences.getInt(progressKeyPref, 0)
        startForeground(1, createNotification(progress))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityReceiver)
        Log.d("ForegroundService", "Service dihentikan!")

        sharedPreferences.edit().remove(progressKeyPref).apply()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Tidak perlu bind ke activity
    }

    private fun createNotification(progress: Int): Notification {
        // Intent untuk tombol "Tambah"
        val plusIntent = Intent(this, ProgressReceiver::class.java).apply {
            action = ACTION_PLUS
        }
        val plusPendingIntent = PendingIntent.getBroadcast(this, 0, plusIntent, PendingIntent.FLAG_MUTABLE)

        // Intent untuk tombol "Kurang"
        val minusIntent = Intent(this, ProgressReceiver::class.java).apply {
            action = ACTION_MINUS
        }
        val minusPendingIntent = PendingIntent.getBroadcast(this, 1, minusIntent, PendingIntent.FLAG_MUTABLE)

        builder = NotificationCompat.Builder(this, Attributes.pushNotif.channelForegroundService)
            .setContentTitle("Uploading...")
            .setContentText("$progress% completed")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.btn_minus, "Minus", minusPendingIntent) // Tombol kurang
            .addAction(android.R.drawable.btn_plus, "Plus", plusPendingIntent) // Tombol tambah

        return builder.build()
    }

    private fun updateNotification(progress: Int) {
        builder.setProgress(100, progress, false)
            .setContentText("$progress% completed")

        if (progress >= 100) {
            builder.setContentText("Download Complete")
                .setProgress(0, 0, false)
                .setOngoing(false)

            stopSelf() // Hentikan service setelah selesai
        }

        notificationManager.notify(1, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Attributes.pushNotif.channelForegroundService,
                "Progress Notifications",
                NotificationManager.IMPORTANCE_LOW // Tidak mengganggu user
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}