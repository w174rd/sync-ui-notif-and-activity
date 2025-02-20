package com.w174rd.syncuinotifactivity.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
}