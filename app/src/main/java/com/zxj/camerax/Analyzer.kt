package com.zxj.camerax

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy


class Analyzer(var context: Context, var bitmapAnalyzer: BitmapAnalyzer):ImageAnalysis.Analyzer {
    interface BitmapAnalyzer{
        fun analyze(bitmap: Bitmap)
    }
    override fun analyze(image: ImageProxy) {
        image.image?.let {
            val start = System.currentTimeMillis()
            val bitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            YuvToRgbConverter(context).yuvToRgb(it, bitmap)
            bitmapAnalyzer.analyze(adjustPhotoRotation(bitmap,90f))
            Log.e("Zxj", "analyze: ${System.currentTimeMillis() - start}" )
        }
//        val bytesFromImageAsType = ImageUtils.getBytesFromImageAsType(image.image, 2)
//        val clone = bytesFromImageAsType.clone()
//
//        rotateYUV240SP(bytesFromImageAsType,clone,image.width,image.height)
//        val rgb: IntArray = ImageUtils.decodeYUV420SP(clone, image.width, image.height)
//        val bitmap2 = Bitmap.createBitmap(rgb, 0, image.width,
//                image.width, image.height,
//                Bitmap.Config.ARGB_8888)
//        bitmapAnalyzer.analyze(bitmap2)
        image.close()
    }
    fun adjustPhotoRotation(bm: Bitmap, orientationDegree: Float): Bitmap {
        val m = Matrix()
        m.setRotate(orientationDegree, bm.width.toFloat() / 2, bm.height.toFloat() / 2)
        val targetX: Float
        val targetY: Float
        if (orientationDegree == 90f) {
            targetX = bm.height.toFloat()
            targetY = 0f
        } else {
            targetX = bm.height.toFloat()
            targetY = bm.width.toFloat()
        }
        val values = FloatArray(9)
        m.getValues(values)
        val x1 = values[Matrix.MTRANS_X]
        val y1 = values[Matrix.MTRANS_Y]
        m.postTranslate(targetX - x1, targetY - y1)
        val bm1 = Bitmap.createBitmap(bm.height, bm.width, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        val canvas = Canvas(bm1)
        canvas.drawBitmap(bm, m, paint)
        return bm1
    }
    fun rotateYUV240SP(src: ByteArray, des: ByteArray, width: Int, height: Int) {
        val wh = width * height
        //旋转Y
        var k = 0
        for (i in 0 until width) {
            for (j in 0 until height) {
                des[k] = src[width * j + i]
                k++
            }
        }
        for (i in 0 until width / 2) {
            for (j in 0 until height / 2) {
                des[k] = src[wh + width / 2 * j + i]
                des[k + width * height / 4] = src[wh * 5 / 4 + width / 2 * j + i]
                k++
            }
        }
    }
}