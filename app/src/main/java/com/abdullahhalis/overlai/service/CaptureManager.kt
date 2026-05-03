package com.abdullahhalis.overlai.service

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import com.abdullahhalis.overlai.utils.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaptureManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private var width = 0
    private var height = 0
    private var statusBarHeight = 0

    fun initialize(resultCode: Int, resultData: Intent) {
        val mediaProjectionManager =
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData)

        mediaProjection?.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                release()
            }
        }, Handler(Looper.getMainLooper()))

        statusBarHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insets = context.getSystemService(Context.WINDOW_SERVICE)
                .let { it as WindowManager }
                .currentWindowMetrics
                .windowInsets
            maxOf(
                insets.getInsets(WindowInsets.Type.statusBars()).top,
                insets.getInsets(WindowInsets.Type.displayCutout()).top
            )
        } else {
            (24 * context.resources.displayMetrics.density).toInt()
        }

        setupVirtualDisplay()
    }

    private fun setupVirtualDisplay() {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = context.resources.displayMetrics.densityDpi

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            width = windowMetrics.bounds.width()
            height = windowMetrics.bounds.height()
        } else {
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(metrics)
            width = metrics.widthPixels
            height = metrics.heightPixels
        }

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "OverlAI Capture",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR or
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY,
            imageReader?.surface,
            null, null
        )
    }

    fun captureScreen(): Bitmap? {
        if (mediaProjection == null || virtualDisplay == null) return null

        val image = imageReader?.acquireLatestImage() ?: return null
        val bitmap = image.toBitmap(width, height)
        image.close()

        return Bitmap.createBitmap(
            bitmap,
            0,
            statusBarHeight,
            width,
            height - statusBarHeight
        ).also { bitmap.recycle() }
    }

    fun release() {
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        virtualDisplay = null
        imageReader = null
        mediaProjection = null
    }
}