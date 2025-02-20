package com.w174rd.syncuinotifactivity.view

import android.Manifest
import android.content.Intent
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
import com.w174rd.syncuinotifactivity.R
import com.w174rd.syncuinotifactivity.databinding.ActivityMainBinding
import com.w174rd.syncuinotifactivity.services.ForegroundService

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        onClick()
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
            }

            btnPlus.setOnClickListener {

            }

            btnPlus.setOnClickListener {

            }
        }
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