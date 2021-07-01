package com.charlton.imageclassification.utils

import android.content.res.AssetManager

class CarDamageClassification(asset: AssetManager) : BinaryClassification(
    labels = arrayOf("Damaged", "Not Damaged"),
    model_file = "b0_model",
    asset = asset
) {

    fun isCarDamaged(float: Float): Boolean {
        return getLabelIndex(float) == 0
    }

}