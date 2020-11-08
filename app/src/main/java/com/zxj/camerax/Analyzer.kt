package com.zxj.camerax

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class Analyzer(var context: Context,var bitmapAnalyzer: BitmapAnalyzer):ImageAnalysis.Analyzer {
    interface BitmapAnalyzer{
        fun analyze(bitmap: Bitmap)
    }
    override fun analyze(image: ImageProxy) {
        image.image?.let {
            val bitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            YuvToRgbConverter(context).yuvToRgb(it, bitmap)
            bitmapAnalyzer.analyze(bitmap)
            it.close()
        }
        image.close()
    }

}