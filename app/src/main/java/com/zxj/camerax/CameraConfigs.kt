package com.zxj.camerax

import android.content.Context
import android.view.Surface.ROTATION_0
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import java.util.concurrent.Executors

class CameraConfigs(var context: Context,var analyzer: Analyzer) {
    val imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetRotation(ROTATION_0)
            .build()
    }
    val imageAnalysis: ImageAnalysis by lazy {
        ImageAnalysis.Builder().build().apply {
            setAnalyzer(Executors.newCachedThreadPool(),analyzer)
        }
    }

}