package com.zxj.camerax

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraView
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors


class CameraViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CAMERA),123)
        p_in_p.setOnClickListener {
            p_in_p.layoutParams.apply {
                if (height == ViewGroup.LayoutParams.MATCH_PARENT){
                    height = 100
                    width = 70
                }else{
                    height == ViewGroup.LayoutParams.MATCH_PARENT
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                }
                p_in_p.layoutParams = this
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraView.apply {
                bindToLifecycle(this@CameraViewActivity)
                captureMode =  CameraView.CaptureMode.IMAGE
            }
            take.setOnClickListener {
                val start = System.currentTimeMillis()
                cameraView.takePicture(Executors.newSingleThreadExecutor(), object :
                    ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        Log.e("zxj", "imageInfo: ${image.imageInfo}")
                        Log.e("zxj", "format: ${image.format}")
                        Log.e("zxj", "cast time : ${System.currentTimeMillis() - start}" )
                        super.onCaptureSuccess(image)
                    }
                })
            }
        }
    }
}