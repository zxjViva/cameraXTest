package com.zxj.camerax

import android.graphics.Bitmap
import android.renderscript.*
import android.view.Surface
import org.netvirta.thecamerax.rs.ScriptC_rotator
import java.nio.ByteBuffer

class YuvToRgbBufferConversion(private val rs: RenderScript) {

    private lateinit var rotateAllocation: Allocation
    private lateinit var mOutputAllocation: Allocation
    private lateinit var mInputAllocation: Allocation

    private val rsYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
    private val rotator: ScriptC_rotator = ScriptC_rotator(rs)

    private fun ByteBuffer.initByteArray(): ByteArray {
        return ByteArray(capacity())
    }


    fun yuvToRgb(
        yBuffer: ByteBuffer,
        uBuffer: ByteBuffer,
        vBuffer: ByteBuffer,
        width: Int,
        height: Int,
        rotation: Int
    ): Bitmap {
        val bitmap = if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            Bitmap.createBitmap(height,width, Bitmap.Config.ARGB_8888)
        }else{
            Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
        }
        val currentTimestamp = System.currentTimeMillis()
        if (!::mInputAllocation.isInitialized) {
            val totalSize = yBuffer.capacity() + uBuffer.capacity() + vBuffer.capacity()
            val yuvType = Type.Builder(rs, Element.U8(rs)).apply {
                setX(totalSize)
            }
            mInputAllocation = Allocation.createTyped(rs,yuvType.create(), Allocation.USAGE_SCRIPT)
        }
        if (!::mOutputAllocation.isInitialized) {
            val rgbType = Type.createXY(rs, Element.RGBA_8888(rs), width, height)
            mOutputAllocation = Allocation.createTyped(rs, rgbType, Allocation.USAGE_SCRIPT)
        }

        if (!::rotateAllocation.isInitialized) {
            rotateAllocation = Allocation.createFromBitmap(rs,bitmap)
        }
        val yuvArray = YUV_420_888toNV21(yBuffer,uBuffer,vBuffer)
        mInputAllocation.copyFrom(yuvArray)
        rsYuvToRgb.setInput(mInputAllocation)
        rsYuvToRgb.forEach(mOutputAllocation)
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            rotator._inWidth = width
            rotator._inHeight = height
            rotator._inImage = mOutputAllocation
            rotator.forEach_rotate_90_clockwise(rotateAllocation, rotateAllocation)
            rotateAllocation.copyTo(bitmap)

        } else {
            mOutputAllocation.copyTo(bitmap)
        }
        return bitmap
    }
    private fun YUV_420_888toNV21(yBuffer: ByteBuffer,
                                  uBuffer: ByteBuffer,
                                  vBuffer: ByteBuffer): ByteArray {
        val nv21: ByteArray
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        nv21 = ByteArray(ySize + uSize + vSize)
        // U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        return nv21
    }
}