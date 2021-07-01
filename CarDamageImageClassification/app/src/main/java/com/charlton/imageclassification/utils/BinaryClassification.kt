package com.charlton.imageclassification.utils

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import kotlin.math.round

open class BinaryClassification(
    val labels: Array<String> = Array(2) {""}, // Labels to translated index to labels names
    model_file: String, // model of the file
    asset: AssetManager // our asset manager
) : TFModelLoader(model_file, asset) {


    // Output you get from your model
    private val outputArray = Array(1) { FloatArray(1) }

    /**
     * Get label prediction index
     */
    fun getLabelIndex(float: Float): Int {
        return round(float).toInt()
    }

    /**
     * Get label name on predictions based on index of prediction confidence
     */
    fun getLabel(float: Float): String {
        return labels[getLabelIndex(float)]
    }

    /**
     * Make a prediction
     */
    fun predict(bitmap: Bitmap): Float {
        try {
            return Interpreter(model).use { interpreter ->
                // Resize the bitmap so that it's 224x224
                val resizedImage =
                    Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, false)
                // Convert the bitmap to a ByteBuffer
                val modelInput = convertBitmapToByteBufferAndNormalized(resizedImage)
                // Perform inference on the model
                interpreter.run(modelInput, outputArray)
                return@use outputArray[0][0]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw Exception("Unable to detect")
    }

}