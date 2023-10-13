package jp.mydns.kokoichi206.tf_recognition.domain

import android.graphics.Bitmap

interface LandmarkClassifier {

    fun classify(bitmap: Bitmap, rotation: Int): List<Classification>
}
