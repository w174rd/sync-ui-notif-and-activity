package com.w174rd.syncuinotifactivity.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.w174rd.syncuinotifactivity.utils.Attributes.pref.key.progressKeyPref
import com.w174rd.syncuinotifactivity.utils.Attributes.pref.progressPrefs
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.ACTION_MINUS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.ACTION_PLUS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.EXTRA_PROGRESS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.UPDATE_PROGRESS

class ProgressReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val sharedPreferences = context.getSharedPreferences(progressPrefs, Context.MODE_PRIVATE)
        var progress = sharedPreferences.getInt(progressKeyPref, 0)

        when (intent.action) {
            ACTION_PLUS -> {
                if (progress < 100) {
                    progress += 5
                    Log.e("ProgressReceiver", "PLUS")
                }
            }

            ACTION_MINUS -> {
                if (progress > 0) {
                    progress -= 5
                    Log.e("ProgressReceiver", "MINUS")
                }
            }
        }

        // ✅ Simpan nilai progress yang baru
        sharedPreferences.edit().putInt(progressKeyPref, progress).apply()

        val intentBroadcast = Intent(UPDATE_PROGRESS)
        intentBroadcast.putExtra(EXTRA_PROGRESS, progress)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentBroadcast)
    }
}