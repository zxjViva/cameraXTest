package com.zxj.camerax

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.renderscript.RenderScript
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_camera2.*
import java.util.concurrent.Executors

class Camera3Activity : AppCompatActivity() {
    val executor = Executors.newCachedThreadPool()
    lateinit var rs :RenderScript
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        rs = RenderScript.create(this)
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

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val processCameraProvider = ProcessCameraProvider.getInstance(this).get()
            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).setTargetRotation(preview.targetRotation).setTargetResolution(
                    Size(1080,1920)
                ).build()
            analyzer.setAnalyzer(executor, ImageAnalysis.Analyzer {
                Log.e("zxj", "onRequestPermissionsResult: ${it.imageInfo.rotationDegrees}")
                it.image?.let { image ->
                    val start = System.currentTimeMillis()
                    val surfaceRotation = Surface.ROTATION_90
                    val bitmap = YuvToRgbBufferConversion(rs).yuvToRgb(
                            image.planes[0].buffer,
                            image.planes[1].buffer,
                            image.planes[2].buffer,
                            image.width,
                            image.height,
                            surfaceRotation
                    )
                    Log.e("zxj", "cost: ${System.currentTimeMillis() - start}" )
                    p_in_p.post {
                        p_in_p.setImageBitmap(bitmap)
                    }
                }
                it.close()

            })
            val imageCapture = ImageCapture.Builder().setTargetRotation(previewView.display.rotation).build()
            processCameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA,imageCapture,preview,analyzer)
            take.setOnClickListener {
                previewView.bitmap?.let {
                    p_in_p.setImageBitmap(it)
                }
            }
        }
    }
}