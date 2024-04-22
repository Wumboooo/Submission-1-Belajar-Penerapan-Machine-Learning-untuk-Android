package com.dicoding.asclepius.wrapper

import java.io.Serializable
import org.tensorflow.lite.support.label.Category

data class SerializableCategory(val label: String, val score: Float) : Serializable

fun convertToSerializable(categories: List<Category>): List<SerializableCategory> {
    return categories.map { SerializableCategory(it.label, it.score) }
}