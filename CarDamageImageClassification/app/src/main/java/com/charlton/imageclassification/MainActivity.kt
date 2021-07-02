package com.charlton.imageclassification

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.charlton.imageclassification.classification.CarDamageClassification


class MainActivity : AppCompatActivity() {

    lateinit var mDamagedDetection: CarDamageClassification
    lateinit var firstPredTxtOutput: TextView
    lateinit var secondPredTxtOutput2: TextView
    lateinit var firstPredBtn: Button
    lateinit var secondPredBtn: Button
    lateinit var mFirstPredictionView: ImageView
    lateinit var mSecondPredictionView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirstPredictionView = findViewById(R.id.image_first_prediction)
        mSecondPredictionView = findViewById(R.id.image_second_prediction)
        firstPredTxtOutput = findViewById(R.id.firstPredTxtOutput)
        secondPredTxtOutput2 = findViewById(R.id.secondPredTxtOutput2)
        firstPredBtn = findViewById(R.id.btnFirstPredTest)
        secondPredBtn = findViewById(R.id.btnSecondPredTest)
        mDamagedDetection = CarDamageClassification(assets)

        // assets folder image file name with extension
        val notDamagedFile = "whole.jpeg"
        val damageFile = "damage.jpeg"

        // get bitmap from assets folder
        val notDamagedBitmap: Bitmap? =
            mDamagedDetection.getLocalBitmapAsset(notDamagedFile)?.apply {
                mFirstPredictionView.setImageBitmap(this)
            }
        val damagedBitmap: Bitmap? = mDamagedDetection.getLocalBitmapAsset(damageFile)?.apply {
            mSecondPredictionView.setImageBitmap(this)
        }

        firstPredBtn.setOnClickListener {
            val prediction = mDamagedDetection.predict(notDamagedBitmap!!)
            val label = mDamagedDetection.getLabel(*prediction).first()
            if (mDamagedDetection.isCarDamaged(prediction.first())) {
                firstPredTxtOutput.text =
                    "This car is damaged: (Confidence: ${100 - prediction.first()}%)\nLabel: ${label}"
            } else {
                firstPredTxtOutput.text =
                    "This car is not damaged: (Confidence: ${prediction.first()}%)\nLabel: ${label}"
            }
        }
        secondPredBtn.setOnClickListener {
            val prediction = mDamagedDetection.predict(damagedBitmap!!)
            val label = mDamagedDetection.getLabel(*prediction).first()
            if (mDamagedDetection.isCarDamaged(prediction.first())) {
                secondPredTxtOutput2.text =
                    "This car is damaged: (Confidence: ${100 - prediction.first()}%)\nLabel: ${label}"
            } else {
                secondPredTxtOutput2.text =
                    "This car is not damaged: (Confidence: ${prediction.first()}%)\nLabel: ${label}"
            }
        }
    }

}
