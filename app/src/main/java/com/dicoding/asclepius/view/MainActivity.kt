package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import org.tensorflow.lite.support.label.Category
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.wrapper.convertToSerializable
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageClassifierHelper = ImageClassifierHelper(this)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            try {
                val imageStream = contentResolver.openInputStream(uri)
                val imageBitmap = BitmapFactory.decodeStream(imageStream)
                val results = imageClassifierHelper.classifyStaticImage(imageBitmap)

                moveToResult(results, uri)
            } catch (e: FileNotFoundException) {
                showToast("Error loading image")
            }
        } ?: showToast("Please select an image first.")
    }

    private fun moveToResult(results: List<Category>, imageUri: Uri) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_RESULTS, convertToSerializable(results).toTypedArray())
            putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri.toString())
        }
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                currentImageUri = uri
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageClassifierHelper.closeModel()
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}