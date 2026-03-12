package com.abdullahhalis.overlai.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.abdullahhalis.overlai.R
import com.abdullahhalis.overlai.presentation.overlay.FloatingBubble
import com.abdullahhalis.overlai.presentation.overlay.OverlayLifecycleOwner
import com.abdullahhalis.overlai.presentation.ui.theme.OverlAITheme
import kotlin.math.abs

class OverlayService: Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: ComposeView
    private lateinit var lifecycleOwner: OverlayLifecycleOwner

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false
    private val touchSlop by lazy {
        ViewConfiguration.get(this).scaledTouchSlop
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupLifecycleOwner()
        setupBubble()
    }

    override fun onDestroy() {
        super.onDestroy()
        with(lifecycleOwner) {
            onPause()
            onStop()
            onDestroy()
        }
        if (::bubbleView.isInitialized) {
            windowManager.removeView(bubbleView)
        }
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

    private fun onBubbleTapped() {
        Log.d("button click", "float button clicked")
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

        return Notification.Builder(this, channelId)
            .setContentTitle("OverlAI is Active")
            .setContentText("Tap bubble to capture")
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}