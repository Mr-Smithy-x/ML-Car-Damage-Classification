package com.charlton.imageclassification

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.charlton.imageclassification.utils.CarDamageClassification


class MainActivity : AppCompatActivity() {

    lateinit var damage: CarDamageClassification
    lateinit var txtOutput: TextView
    lateinit var txtOutput2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val img: ImageView = findViewById(R.id.imageToLabel)
        val img2: ImageView = findViewById(R.id.imageToLabel2)
        // assets folder image file name with extension
        val notDamagedFile = "whole.jpeg"
        val damageFile = "damage.jpeg"
        damage = CarDamageClassification(assets)

        // get bitmap from assets folder
        val bitmap: Bitmap? = damage.getLocalBitmapAsset(notDamagedFile)
        val bitmap2: Bitmap? = damage.getLocalBitmapAsset(damageFile)
        bitmap?.apply {
            img.setImageBitmap(this)
        }
        bitmap2?.apply {
            img2.setImageBitmap(this)
        }

        txtOutput = findViewById(R.id.txtOutput)
        txtOutput2 = findViewById(R.id.txtOutput2)
        val btn: Button = findViewById(R.id.btnTest)
        val btn2: Button = findViewById(R.id.btnTest2)
        btn.setOnClickListener {
            val prediction = damage.predict(bitmap!!)
            val label = damage.getLabel(prediction)
            if(damage.isCarDamaged(prediction)){
                txtOutput.text = "This car is damaged: (Confidence: ${100 - prediction}%)\nLabel: ${label}"
            }else{
                txtOutput.text = "This car is not damaged: (Confidence: ${prediction}%)\nLabel: ${label}"
            }
        }
        btn2.setOnClickListener {
            val prediction = damage.predict(bitmap2!!)
            val label = damage.getLabel(prediction)
            if(damage.isCarDamaged(prediction)){
                txtOutput2.text = "This car is damaged: (Confidence: ${100 - prediction}%)\nLabel: ${label}"
            }else{
                txtOutput2.text = "This car is not damaged: (Confidence: ${prediction}%)\nLabel: ${label}"
            }
        }
    }

}
