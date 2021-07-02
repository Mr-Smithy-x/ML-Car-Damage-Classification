package com.charlton.imageclassification.classification.loader

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

/**
 * TensorFlow Lite Model Loader
 * loads tensorflow files from the asset folder path
 * @param filename assets = root path "mydog/model" or "b0_model" without .tflite extension
 * @param assets Android AssetManager
 * @param inputImageHeight Input height of your model, typically 224x224 is the sweet spot for most models
 * @param inputImageWidth Input width of your model, typically 224x224 is the sweet spot for most models
 * @param channelSize Typically its 3 ie. RGB, sometimes 4 ARGB
 */
abstract class TFModelLoader(
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

    /**
     * Load our model
     */
    protected fun loadModelFile(): MappedByteBuffer? {
        val fileDescriptor: AssetFileDescriptor = assets.openFd("${filename}.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    // get bitmap from assets
    fun getLocalBitmapAsset(fileName: String): Bitmap? {
        return try {
            with(assets.open(fileName)) {
                BitmapFactory.decodeStream(this)
            }
        } catch (e: IOException) {
            null
        }
    }


    /**
     * We should take our bitmap, x, y and color channel values
     * normalize the rgb values so that our machine learning model can
     * predict on our images better.
     */
    protected fun convertBitmapToByteBufferAndNormalized(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        // Specify the size of the byteBuffer
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())
        // Calculate the number of pixels in the image
        val pixels = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        // Loop through all the pixels and save them into the buffer
        val input = Array(1) {
            Array(bitmap.width) {
                Array(bitmap.height) {
                    FloatArray(3)
                }
            }
        }
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)               // Get Pixel (X, Y) values, then store rgb
                input[0][x][y][0] = Color.red(pixel) / 255.0f   // Red
                input[0][x][y][1] = Color.green(pixel) / 255.0f // Green
                input[0][x][y][2] = Color.blue(pixel) / 255.0f  // Blue
            }
        }
        bitmap.recycle()
        return input
    }

}