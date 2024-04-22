package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.tensorflow.lite.support.label.Category
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.wrapper.SerializableCategory
import java.io.FileNotFoundException

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        const val EXTRA_RESULTS = "extra_results"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val results = getSerializableResults(intent)
        displayResults(results)

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        loadImage(imageUri)
    }

    private fun getSerializableResults(intent: Intent): List<Category> {
        val serializableResults = intent.getSerializableExtra(EXTRA_RESULTS) as Array<SerializableCategory>
        return serializableResults.map { Category(it.label, it.score) }
    }

    private fun displayResults(results: List<Category>) {
        val resultText = StringBuilder()
        for (result in results) {
            resultText.append("${result.label}: ${(result.score * 100).toInt()}%\n")
        }
        binding.resultText.text = resultText.toString()
    }

    private fun loadImage(imageUri: String?) {
        try {
            imageUri?.let {
                val uri = Uri.parse(imageUri)
                val imageStream = contentResolver.openInputStream(uri)
                val imageBitmap = BitmapFactory.decodeStream(imageStream)
                binding.resultImage.setImageBitmap(imageBitmap)
            }
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
        }
    }
}