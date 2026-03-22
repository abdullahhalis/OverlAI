package com.abdullahhalis.overlai.presentation.main

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abdullahhalis.overlai.presentation.navigation.NavGraph
import com.abdullahhalis.overlai.presentation.ui.theme.OverlAITheme
import com.abdullahhalis.overlai.service.OverlayService
import com.abdullahhalis.overlai.service.OverlayServiceState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var overlayServiceState: OverlayServiceState

    private val splashViewModel: SplashViewModel by viewModels()

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkAndStartOverlay()
    }

    private val capturePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            startOverlayService(result.resultCode, result.data!!)
        } else {
            Toast.makeText(this, "Screen capture permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !splashViewModel.isReady.value }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isOverlayRunning by overlayServiceState.isRunning.collectAsStateWithLifecycle()
            val isOnboardingCompleted by splashViewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

            OverlAITheme {
                NavGraph(
                    isOnboardingComplete = isOnboardingCompleted,
                    isOverlayRunning = isOverlayRunning,
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