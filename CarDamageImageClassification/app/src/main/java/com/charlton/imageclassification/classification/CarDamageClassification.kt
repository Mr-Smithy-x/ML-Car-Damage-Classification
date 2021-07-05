package com.charlton.imageclassification.classification

import android.content.res.AssetManager
import com.charlton.imageclassification.classification.base.BinaryClassification

/**
 * CarDamageClassification
 * It determines whether the car is damaged or not
 *
 * @param asset Android AssetManager
 */
class CarDamageClassification(asset: AssetManager) : BinaryClassification(
    labels = arrayOf("Damaged", "Not Damaged"), // 0 = Damaged, 1 = Not Damamged
    model_file = "binary/b0_model", // b0 model
    asset = asset // Asset Manager
) {

    /**
     * Get Car Damage
     */
    fun isCarDamaged(prediction: Float): Boolean {
        return !isCarNew(prediction)
    }

    fun isCarNew(prediction: Float): Boolean {
        return getLabelIndex(prediction).first() == 1
    }

}