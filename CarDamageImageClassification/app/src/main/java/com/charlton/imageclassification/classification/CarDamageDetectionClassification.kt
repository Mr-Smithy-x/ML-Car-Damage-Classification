package com.charlton.imageclassification.classification

import android.content.res.AssetManager
import com.charlton.imageclassification.classification.base.MultiClassification

class CarDamageDetectionClassification(asset: AssetManager) : MultiClassification(
    labels = arrayOf("bumper", "door", "glass_shatter", "lamp"),
    model_file = "multi/car_damage_multi_b0",
    asset = asset
) {


    /**
     * Get Car Damage
     */
    fun getLabel(vararg predictions: Float): String {
        return super.getPredictionLabel(*predictions)
    }

}