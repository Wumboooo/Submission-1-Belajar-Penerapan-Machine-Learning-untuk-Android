package com.dicoding.asclepius.helper

import android.graphics.Bitmap
import android.content.Context
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.image.TensorImage

class ImageClassifierHelper(private val context: Context) {

    private val model: CancerClassification = CancerClassification.newInstance(context)

    fun classifyStaticImage(image: Bitmap): List<Category> {
        val tensorImage = TensorImage.fromBitmap(image)
        val outputs = model.process(tensorImage)
        return outputs.probabilityAsCategoryList
    }

    fun closeModel() {
        model.close()
    }
}
