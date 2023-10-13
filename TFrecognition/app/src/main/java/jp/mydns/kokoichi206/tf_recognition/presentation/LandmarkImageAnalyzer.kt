package jp.mydns.kokoichi206.tf_recognition.presentation

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import jp.mydns.kokoichi206.tf_recognition.domain.Classification
import jp.mydns.kokoichi206.tf_recognition.domain.LandmarkClassifier

class LandmarkImageAnalyzer(
    private val classifier: LandmarkClassifier,
    private val onResults: (List<Classification>) -> Unit,
) : ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter % 60 == 0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
                // From metadata:
                // https://tfhub.dev/google/lite-model/on_device_vision/classifier/landmarks_classifier_asia_V1/1
                .centerCrop(321, 321)

            val results = classifier.classify(bitmap, rotationDegrees)
            onResults(results)
        }
        frameSkipCounter++

        image.close()
    }
}