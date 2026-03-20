package com.abdullahhalis.overlai.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.abdullahhalis.overlai.data.local.entity.TranslationHistoryEntity
import com.abdullahhalis.overlai.data.repository.AppRepository
import com.abdullahhalis.overlai.presentation.main.MainActivity
import com.abdullahhalis.overlai.presentation.overlay.FloatingBubble
import com.abdullahhalis.overlai.presentation.overlay.OverlayLifecycleOwner
import com.abdullahhalis.overlai.presentation.overlay.OverlayUIState
import com.abdullahhalis.overlai.presentation.overlay.TranslationOverlay
import com.abdullahhalis.overlai.presentation.ui.theme.OverlAITheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class OverlayService : Service() {

    @Inject
    lateinit var captureManager: CaptureManager

    @Inject
    lateinit var ocrManager: OcrManager

    @Inject
    lateinit var translationManager: TranslationManager

    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var overlayServiceState: OverlayServiceState

    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: ComposeView
    private lateinit var overlayView: ComposeView
    private val overlayParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
    }
    private lateinit var lifecycleOwner: OverlayLifecycleOwner

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val overlayState = mutableStateOf<OverlayUIState>(OverlayUIState.Idle)
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private val touchSlop by lazy {
        ViewConfiguration.get(this).scaledTouchSlop
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
            ?: Activity.RESULT_CANCELED
        val resultData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_RESULT_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_RESULT_DATA)
        }

        Log.d("OverlayService", "onStartCommand - resultCode: $resultCode, resultData: $resultData")

        if (resultCode == Activity.RESULT_OK && resultData != null) {
            captureManager.initialize(resultCode, resultData)
            Log.d("OverlayService", "CaptureManager initialized!")
        } else {
            Log.d("OverlayService", "Initialize skipped — resultCode or resultData null!")
        }

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        overlayServiceState.setRunning(true)

        startForeground(NOTIFICATION_ID, buildNotification())
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupLifecycleOwner()
        setupBubble()
        setupOverlayView()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayServiceState.setRunning(false)

        serviceScope.cancel()
        captureManager.release()
        with(lifecycleOwner) {
            onPause()
            onStop()
            onDestroy()
        }
        if (::bubbleView.isInitialized) windowManager.removeView(bubbleView)
        if (::overlayView.isInitialized) windowManager.removeView(overlayView)
    }

    private fun setupLifecycleOwner() {
        lifecycleOwner = OverlayLifecycleOwner()
        with(lifecycleOwner) {
            onCreate()
            onStart()
            onResume()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupBubble() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        bubbleView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                OverlAITheme {
                    FloatingBubble()
                }
            }
        }

        bubbleView.setOnClickListener { onBubbleTapped() }

        bubbleView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = abs(event.rawX - initialTouchX)
                    val dy = abs(event.rawY - initialTouchY)

                    if (dx > touchSlop || dy > touchSlop) {
                        isDragging = true
                    }

                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()

                    windowManager.updateViewLayout(bubbleView, params)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        bubbleView.performClick()
                    } else {
                        val screenWidth = resources.displayMetrics.widthPixels

                        params.x = if (params.x + bubbleView.width / 2 >= screenWidth / 2) {
                            screenWidth - bubbleView.width
                        } else {
                            0
                        }

                        windowManager.updateViewLayout(bubbleView, params)
                    }
                    true
                }

                else -> false
            }
        }

        windowManager.addView(bubbleView, params)
    }

    private fun setupOverlayView() {
        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                OverlAITheme {
                    TranslationOverlay(
                        state = overlayState.value,
                        onDismiss = {
                            overlayState.value = OverlayUIState.Idle
                            overlayParams.flags =
                                overlayParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            windowManager.updateViewLayout(overlayView, overlayParams)
                        },
                        onRetry = {
                            overlayState.value = OverlayUIState.Idle
                            onBubbleTapped()
                        }
                    )
                }
            }
        }
        windowManager.addView(overlayView, overlayParams)
    }

    private fun onBubbleTapped() {
        Log.d(OverlayService::class.java.simpleName, "float button clicked")
        serviceScope.launch {
            if (overlayServiceState.isCapturing.value || overlayServiceState.isTranslating.value) return@launch

            overlayServiceState.setCapturing(true)

            try {
                bubbleView.alpha = 0f
                delay(100)
                overlayState.value = OverlayUIState.Loading

                val bitmap = captureManager.captureScreen()

                if (bitmap == null) {
                    overlayState.value = OverlayUIState.Error("Capture failed")
                    return@launch
                }

//                bitmap.saveToGallery(this@OverlayService)
                Log.d(
                    OverlayService::class.java.simpleName,
                    "Capture Success: ${bitmap.width}x${bitmap.height}"
                )

                val sourceLanguage = appRepository.sourceLanguage.first()
                val targetLanguage = appRepository.targetLanguage.first()

                val ocrResult = ocrManager.recognize(bitmap, sourceLanguage)
                ocrResult.forEach { result ->
                    Log.d(
                        OverlayService::class.java.simpleName,
                        "OCR: ${result.text} at ${result.boundingBox}"
                    )
                }

                if (ocrResult.isEmpty()) {
                    overlayState.value = OverlayUIState.Error("No Text detected")
                    return@launch
                }

                overlayServiceState.setTranslating(true)

                val translationResults =
                    translationManager.translate(ocrResult, sourceLanguage, targetLanguage)
                translationResults.forEach { result ->
                    Log.d(
                        OverlayService::class.java.simpleName,
                        "Translated: ${result.originalText} -> ${result.translatedText}"
                    )
                }

                if (translationResults.isEmpty()) {
                    overlayState.value = OverlayUIState.Error("Translation failed")
                    return@launch
                }

                translationResults.forEach { result ->
                    appRepository.insertHistory(
                        TranslationHistoryEntity(
                            originalText = result.originalText,
                            translatedText = result.translatedText,
                            sourceLanguage = sourceLanguage.name,
                            targetLanguage = targetLanguage.name
                        )
                    )
                }

                overlayState.value = OverlayUIState.Success(translationResults)

            } catch (e: Exception) {
                Log.e(OverlayService::class.java.simpleName, "Error: ${e.message}")
                overlayState.value = OverlayUIState.Error(e.message ?: "Unknown Error")
            } finally {
                overlayServiceState.setTranslating(false)
                overlayServiceState.setCapturing(false)
                overlayParams.flags =
                    overlayParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
                windowManager.updateViewLayout(overlayView, overlayParams)
                bubbleView.alpha = 1f
            }
        }
    }

    private fun buildNotification(): Notification {
        val channelId = "overlay_channel"
        val channel = NotificationChannel(
            channelId,
            "OverlAI overlay",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)

        if (manager.getNotificationChannel(channelId) == null) {
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, channelId)
            .setContentTitle("OverlAI is Active")
            .setContentText("Tap bubble to capture")
            .setContentIntent(pendingIntent)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_RESULT_DATA = "extra_result_data"
    }
}