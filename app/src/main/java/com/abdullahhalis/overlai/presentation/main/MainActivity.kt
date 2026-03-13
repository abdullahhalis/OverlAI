package com.abdullahhalis.overlai.presentation.main

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.abdullahhalis.overlai.presentation.ui.theme.OverlAITheme
import com.abdullahhalis.overlai.service.OverlayService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkAndStartOverlay()
    }

    private val capturePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MainActivity", "resultCode: ${result.resultCode}, data: ${result.data}")
        if (result.resultCode == RESULT_OK && result.data != null) {
            startOverlayService(result.resultCode, result.data!!)
        } else {
            Toast.makeText(this, "Screen capture permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OverlAITheme {
                MainScreen(
                    onStartOverlay = { checkAndStartOverlay() },
                    onStopOverlay = { stopOverlayService() }
                )
            }
        }
    }

    private fun checkAndStartOverlay() {
        if (Settings.canDrawOverlays(this)) {
            requestMediaProjectionPermission()
        } else {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:$packageName".toUri()
            )
            overlayPermissionLauncher.launch(intent)
        }
    }

    private fun requestMediaProjectionPermission() {
        val mediaProjectionManager = getSystemService(
            MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager
        capturePermissionLauncher.launch(
            mediaProjectionManager.createScreenCaptureIntent()
        )
    }

    private fun startOverlayService(resultCode: Int, data: Intent) {
        Log.d("MainActivity", "startOverlayService - resultCode: $resultCode")
        val intent = Intent(this, OverlayService::class.java).apply {
            putExtra(OverlayService.EXTRA_RESULT_CODE, resultCode)
            putExtra(OverlayService.EXTRA_RESULT_DATA, data)
        }
        startForegroundService(intent)
    }

    private fun stopOverlayService() {
        val intent = Intent(this, OverlayService::class.java)
        stopService(intent)
    }
}