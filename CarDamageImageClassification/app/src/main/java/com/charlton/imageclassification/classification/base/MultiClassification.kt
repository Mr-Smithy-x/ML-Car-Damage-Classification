package com.charlton.imageclassification.classification.base

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.charlton.imageclassification.classification.loader.TFModelLoader
import org.tensorflow.lite.Interpreter

/**
 * Model Wrapper for well.... Multi Class Classification Models
 *
 * @see TFModelLoader for base functions
 * @param labels Model Binary Labels - For binary its size 2 or more
 * @param model_file Model File
 * @param asset Android AssetManager
 */
abstract class MultiClassification(
    val labels: Array<String> = Array(3) {""}, // Labels to translated index to labels names
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
        return getOneHotLabels(*predictions).map {
            return@map it == 1
        }.toBooleanArray()
    }

    /**
     * Get True Labels 1 (T) & 0 (F) as an array
     * depending on predictions so sending in two images will return
     * 2... [0, 1], 3... [0, 1, 0] etc
     * @param float in an array of floats or a single float
     * @return An array of true/false labels
     */
    fun getOneHotLabels(vararg predictions: Float): IntArray {
        return predictions.map {
            if(it == predictions.maxOrNull()){
                return@map 1
            }else {
                return@map 0
            }
        }.toIntArray()
    }

    /**
     * Get label name on predictions based on index of prediction confidence
     * @param predictions in an array of floats or a single float
     * @return An array of strings or labels
     */
    fun getPredictionLabel(vararg predictions: Float): String {
        val maxIndex = getMaxIndex(*predictions)
        return labels[maxIndex]
    }

    fun getMaxValue(vararg predictions: Float): Float {
        return predictions.maxOrNull()?: throw NullPointerException("Could not find maximum value")
    }

    fun getMaxIndex(vararg predictions: Float): Int {
        return predictions.indexOf(getMaxValue(*predictions))
    }

    /**
     * Make a prediction
     * @param bitmap you can pass an array of bitmaps or a single bitmap
     * @return an array of floats, depending on how much bitmaps you send in
     */
    fun predict(vararg bitmap: Bitmap): Array<FloatArray> {
        try {
            return Interpreter(model, Interpreter.Options()).use { interpreter ->
                // Resize the bitmap so that it's 224x224
                val images = bitmap.scale(inputImageWidth, inputImageHeight, false)
                // Convert the bitmap to a ByteBuffer
                val modelInput = images.convertBitmapToByteBufferAndNormalized()
                // Output you get from your model
                val outputArray = Array(modelInput.size) { FloatArray(labels.size) }
                //Get output
                interpreter.run(modelInput, outputArray)
                //Flatten Data
                Log.e("OK", "${outputArray[0].toList()}")
                return@use outputArray.clone()
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
