package com.charlton.imageclassification.utils

import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import kotlin.math.round

open class BinaryClassification(
    val labels: Array<String> = Array(2) {""},
    model_file: String,
    asset: AssetManager
) : TFModelLoader(model_file, asset) {


    // Output you get from your model
    protected val outputArray = Array(1) { FloatArray(1) }


    fun getLabelIndex(float: Float): Int {
        return round(float).toInt()
    }

    fun getLabel(float: Float): String {
        return labels[getLabelIndex(float)]
    }

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