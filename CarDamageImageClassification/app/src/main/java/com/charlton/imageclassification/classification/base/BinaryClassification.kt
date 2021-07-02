package com.charlton.imageclassification.classification.base

import android.content.res.AssetManager
import android.graphics.Bitmap
import com.charlton.imageclassification.classification.loader.TFModelLoader
import org.tensorflow.lite.Interpreter
import kotlin.math.roundToInt

/**
 * Model Wrapper for well.... Binary Classification Models
 *
 * @see TFModelLoader for base functions
 * @param labels Model Binary Labels - For binary its size 2
 * @param model_file Model File
 * @param asset Android AssetManager
 */
abstract class BinaryClassification(
    val labels: Array<String> = Array(2) {""}, // Labels to translated index to labels names
    model_file: String, // model of the file
    asset: AssetManager // our asset manager
) : TFModelLoader(model_file, asset) {


    /**
     * Get True Labels 1 (T) & 0 (F) as an array
     * depending on predictions so sending in two images will return
     * 2... [0, 1], 3... [0, 1, 0] etc
     * @param float in an array of floats or a single float
     * @return An array of true/false labels
     */
    fun getTrueLabels(vararg predictions: Float): BooleanArray {
        return getLabelIndex(*predictions).map { it == 1 }.toBooleanArray()
    }


    /**
     * Get label prediction index
     * @param predictions in an array of floats or a single float
     * @return An array of index for your labels
     */
    fun getLabelIndex(vararg predictions: Float): IntArray {
        return predictions.map{it.roundToInt()}.toIntArray()
    }

    /**
     * Get label name on predictions based on index of prediction confidence
     * @param predictions in an array of floats or a single float
     * @return An array of strings or labels
     */
    fun getLabel(vararg predictions: Float): Array<String> {
        return getLabelIndex(*predictions).map {
            labels[it]
        }.toTypedArray()
    }

    /**
     * Make a prediction
     * @param bitmap you can pass an array of bitmaps or a single bitmap
     * @return an array of floats, depending on how much bitmaps you send in
     */
    fun predict(vararg bitmap: Bitmap): FloatArray {
        try {
            return Interpreter(model, Interpreter.Options()).use { interpreter ->
                // Resize the bitmap so that it's 224x224
                val images = bitmap.scale(inputImageWidth, inputImageHeight, false)
                // Convert the bitmap to a ByteBuffer
                val modelInput = images.convertBitmapToByteBufferAndNormalized()
                // Output you get from your model
                val outputArray = Array(modelInput.size) { FloatArray(1) }
                //Get output
                interpreter.run(modelInput, outputArray)
                //Flatten Data
                val flattened = outputArray.map { predictions ->
                    return@map predictions.map {
                        return@map it
                    }.first()
                }
                return@use flattened.toFloatArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw Exception("Unable to detect")
    }

    private fun Array<Bitmap>.convertBitmapToByteBufferAndNormalized(): Array<Array<Array<FloatArray>>> {
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
