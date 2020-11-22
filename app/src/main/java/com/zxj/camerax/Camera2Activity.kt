package com.zxj.camerax

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.extensions.BokehImageCaptureExtender
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_camera2.*
import java.util.concurrent.Executors

class Camera2Activity : AppCompatActivity() {
    val executor = Executors.newCachedThreadPool()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CAMERA),123)
        p_in_p.setOnClickListener {
            p_in_p.layoutParams = p_in_p.layoutParams.apply {
                if (height == ViewGroup.LayoutParams.WRAP_CONTENT){
                    height = 100
                    width = 70
                }else{
                    height == ViewGroup.LayoutParams.WRAP_CONTENT
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val processCameraProvider = ProcessCameraProvider.getInstance(this).get()
            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
            analyzer.setAnalyzer(executor, ImageAnalysis.Analyzer {
                Log.e("zxj", "imageInfo: ${it.imageInfo}")
                Log.e("zxj", "format: ${it.format}")
            })
            val bokehImageCapture = BokehImageCaptureExtender.create(ImageCapture.Builder())
            // Query if extension is available (optional).
            if (bokehImageCapture.isExtensionAvailable(CameraSelector.DEFAULT_BACK_CAMERA)) {
                // Enable the extension if available.
                bokehImageCapture.enableExtension(CameraSelector.DEFAULT_BACK_CAMERA)
            }

            processCameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA,preview,analyzer)
            take.setOnClickListener {
                previewView.bitmap?.let {
                    p_in_p.setImageBitmap(it)
                }
            }
        }
    }
}