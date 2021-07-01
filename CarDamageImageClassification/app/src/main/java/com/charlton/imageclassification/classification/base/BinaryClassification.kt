package com.charlton.imageclassification.classification.base

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.charlton.imageclassification.classification.loader.TFModelLoader
import org.tensorflow.lite.Interpreter
import kotlin.math.round
import kotlin.math.roundToInt

abstract class BinaryClassification(
    val labels: Array<String> = Array(2) {""}, // Labels to translated index to labels names
    model_file: String, // model of the file
    asset: AssetManager // our asset manager
) : TFModelLoader(model_file, asset) {


    // Output you get from your model
    private val outputArray = Array(1) { FloatArray(1) }

    /**
     * Get label prediction index
     */
    fun getLabelIndex(vararg float: Float): IntArray {
        return float.map{it.roundToInt()}.toIntArray()
    }

    /**
     * Get label name on predictions based on index of prediction confidence
     */
    fun getLabel(vararg float: Float): Array<String> {
        return getLabelIndex(*float).map {
            labels[it]
        }.toTypedArray()
    }

    /**
     * Make a prediction
     */
    fun predict(vararg bitmap: Bitmap): FloatArray {
        try {
            return Interpreter(model, Interpreter.Options()).use { interpreter ->
                // Resize the bitmap so that it's 224x224
                val images = bitmap.scale(inputImageWidth, inputImageHeight, false)
                // Convert the bitmap to a ByteBuffer
                val modelInput = images.convertBitmapToByteBufferAndNormalized()
                // Perform inference on the model
                val list: ArrayList<Float> = ArrayList()
                modelInput.forEach {
                    interpreter.run(it, outputArray)
                    list.add(outputArray[0][0])
                    Log.e("OK", "${outputArray[0][0]}")
                }
                return@use list.toFloatArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw Exception("Unable to detect")
    }

    private fun Array<Bitmap>.convertBitmapToByteBufferAndNormalized(): Array<Array<Array<Array<FloatArray>>>> {
        val mapped = this.map {
            return@map convertBitmapToByteBufferAndNormalized(it)
        }
        return mapped.toTypedArray()
    }

    private fun Array<out Bitmap>.scale(inputImageWidth: Int, inputImageHeight: Int, filter: Boolean): Array<Bitmap> {
        val mapped = this.map {
            return@map Bitmap.createScaledBitmap(it, inputImageWidth, inputImageHeight, filter)
        }
        return mapped.toTypedArray()
    }

}
