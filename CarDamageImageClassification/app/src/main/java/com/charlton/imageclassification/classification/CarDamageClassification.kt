package com.charlton.imageclassification.classification

import android.content.res.AssetManager
import com.charlton.imageclassification.classification.base.BinaryClassification

class CarDamageClassification(asset: AssetManager) : BinaryClassification(
    labels = arrayOf("Damaged", "Not Damaged"),
    model_file = "b0_model",
    asset = asset
) {

    /**
     * Get Car Damage
     */
    fun isCarDamaged(prediction: Float): Boolean {
        return getLabelIndex(prediction)[0] == 0
    }

}