package com.charlton.imageclassification.utils

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

open class TFModelLoader(
    val filename: String,
    private val assets: AssetManager,
    protected val inputImageWidth: Int = 224, // Width of the image that our model expects
    protected val inputImageHeight: Int = 224, // Height of the image that our model expects
    private val channelSize: Int = 3 // Our model expects a RGB image, hence the channel size is 3
) {

    protected var model: MappedByteBuffer

    // Size of the input buffer size (if your model expects a float input, multiply this with 4)
    private val modelInputSize: Int
        get() = inputImageWidth * inputImageHeight * channelSize * 4

    init {
        model = loadModelFile()!!
    }

    protected fun loadModelFile(): MappedByteBuffer? {
        val fileDescriptor: AssetFileDescriptor = assets.openFd("${filename}.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    // extension function to get bitmap from assets
    fun getLocalBitmapAsset(fileName: String): Bitmap? {
        return try {
            with(assets.open(fileName)) {
                BitmapFactory.decodeStream(this)
            }
        } catch (e: IOException) {
            null
        }
    }


    protected fun convertBitmapToByteBufferAndNormalized(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        // Specify the size of the byteBuffer
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())
        // Calculate the number of pixels in the image
        val pixels = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        // Loop through all the pixels and save them into the buffer
        val input = Array(1) {
            Array(224) {
                Array(224) {
                    FloatArray(3)
                }
            }
        }
        for (x in 0..223) {
            for (y in 0..223) {
                val pixel = bitmap.getPixel(x, y)
                input[0][x][y][0] = Color.red(pixel) / 255.0f
                input[0][x][y][1] = Color.green(pixel) / 255.0f
                input[0][x][y][2] = Color.blue(pixel) / 255.0f
            }
        }
        bitmap.recycle()
        return input
    }

}