package com.zxj.camerax

import android.graphics.Bitmap
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.view.Surface.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zxj.camerax.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 123)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123 && grantResults.size == 1){
            initCamera()
        }
    }

    private fun initCamera() {
        val provider = ProcessCameraProvider.getInstance(this)
        provider.addListener(Runnable {
            val get = provider.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            val analyzer = ImageAnalysis.Builder().build().apply {
                setAnalyzer(Executors.newCachedThreadPool(), Analyzer(this@MainActivity, object : Analyzer.BitmapAnalyzer {
                    override fun analyze(bitmap: Bitmap) {
                        runOnUiThread {
                            binding.pInP.setImageBitmap(bitmap)
                        }
                    }
                }))
            }
            get.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, analyzer, initImageCapture(), preview)
        }, ContextCompat.getMainExecutor(this))
    }
    private fun initImageCapture():ImageCapture{
        return ImageCapture.Builder()
            .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }
}