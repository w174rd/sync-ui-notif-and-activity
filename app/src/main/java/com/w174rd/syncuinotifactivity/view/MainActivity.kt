package com.w174rd.syncuinotifactivity.view

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.w174rd.syncuinotifactivity.R
import com.w174rd.syncuinotifactivity.databinding.ActivityMainBinding
import com.w174rd.syncuinotifactivity.services.ForegroundService
import com.w174rd.syncuinotifactivity.services.ProgressReceiver
import com.w174rd.syncuinotifactivity.utils.Attributes.pref.key.progressKeyPref
import com.w174rd.syncuinotifactivity.utils.Attributes.pref.progressPrefs
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.ACTION_MINUS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.ACTION_PLUS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.EXTRA_PROGRESS
import com.w174rd.syncuinotifactivity.utils.Attributes.progressReceiver.UPDATE_PROGRESS

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val progressChange = intent?.getIntExtra(EXTRA_PROGRESS, 0) ?: 0
            Log.e("MainActivity", "progressChangeeee: $progressChange")
            if (progressChange != 0) {
                binding.apply {
                    progressBar.progress = progressChange
                    txtProgress.text = "$progressChange%"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(progressReceiver, IntentFilter(UPDATE_PROGRESS))

        initialProgressData()
        onClick()
    }

    private fun initialProgressData() {
        sharedPreferences = getSharedPreferences(progressPrefs, Context.MODE_PRIVATE)
        val progress = sharedPreferences.getInt(progressKeyPref, 0)

        binding.apply {
            progressBar.progress = progress
            txtProgress.text = "$progress%"
        }
    }

    private fun onClick() {
        binding.apply {
            btnStartFgService.setOnClickListener {
                val serviceIntent = Intent(this@MainActivity, ForegroundService::class.java)
                startService(serviceIntent)
            }

            btnStopFgService.setOnClickListener {
                val serviceIntent = Intent(this@MainActivity, ForegroundService::class.java)
                stopService(serviceIntent)
                sharedPreferences.edit().remove(progressKeyPref).apply()
                initialProgressData()
            }

            btnPlus.setOnClickListener {
                val plusIntent = Intent(this@MainActivity, ProgressReceiver::class.java).apply {
                    action = ACTION_PLUS
                }
                sendBroadcast(plusIntent)
            }

            btnMinus.setOnClickListener {
                val minusIntent = Intent(this@MainActivity, ProgressReceiver::class.java).apply {
                    action = ACTION_MINUS
                }
                sendBroadcast(minusIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(progressReceiver)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Permission", "Izin notifikasi diberikan ✅")
        } else {
            Log.d("Permission", "Izin notifikasi ditolak ❌")
        }
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}