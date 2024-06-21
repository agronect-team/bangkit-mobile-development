package com.example.agronect.ui.editmypost

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.agronect.R
import com.example.agronect.data.response.UpdateSharingResponse
import com.example.agronect.data.retrofit.ApiConfig
import com.example.agronect.data.util.getImageUri
import com.example.agronect.data.util.reduceFileImage
import com.example.agronect.data.util.uriToFile
import com.example.agronect.databinding.ActivityEditPostBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.mypost.MyPostViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditMyPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPostBinding

    private val viewModel by viewModels<MyPostViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null
    private var originalImageUri: Uri? = null
    private var originalImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val content = intent.getStringExtra("description")
        val imgUrl = intent.getStringExtra("imgUrl")?.let { Uri.parse(it) }
        val sharingId = intent.getStringExtra("sharingId")

        binding.etDescription.setText(content)
        imgUrl?.let {
            originalImageUri = it
            lifecycleScope.launch {
                originalImageFile = downloadImage(it.toString())
                loadImage(Uri.fromFile(originalImageFile))
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        viewModel.getSession().observe(this) { user ->
            if (user != null && user.isLogin) {
                val token = user.token

                binding.saveButton.setOnClickListener {
                    val newContent = binding.etDescription.text.toString()
                    lifecycleScope.launch {
                        updatePost(token, sharingId!!, newContent, currentImageUri)
                    }
                }

                binding.ivCamera.setOnClickListener { startCamera() }
                binding.ivGalery.setOnClickListener { startGallery() }
            }
        }
    }

    private fun loadImage(imgUrl: Uri) {
        Glide.with(this)
            .load(imgUrl)
            .into(binding.previewImageView)
    }

    private suspend fun downloadImage(url: String): File = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val inputStream = response.body?.byteStream()
        val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        file
    }

    private suspend fun updatePost(token: String, sharingId: String, content: String, imgUrl: Uri?) {
        try {
            val apiService = ApiConfig.getApiService()

            val contentBody = content.toRequestBody("text/plain".toMediaType())

            val imgUrlPart: MultipartBody.Part? = if (imgUrl != null) {
                val imageFile = if (imgUrl == originalImageUri) {
                    originalImageFile!!
                } else {
                    uriToFile(imgUrl, this@EditMyPostActivity).reduceFileImage()
                }

                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                Log.d("EditMyPostActivity", "Image file name: ${imageFile.name}")
                MultipartBody.Part.createFormData("imgUrl", imageFile.name, requestImageFile)
            } else {
                null
            }

            Log.d("EditMyPostActivity", "Content: $content")
            Log.d("EditMyPostActivity", "Sharing ID: $sharingId")

            val response = apiService.updateSharing(
                "Bearer $token",
                sharingId,
                contentBody,
                imgUrlPart
            )

            if (response.isSuccessful) {
                val successResponse = response.body()?.message
                successResponse?.let { message -> showToast(message) }
                showToast(getString(R.string.success_post_update))
                finish()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, UpdateSharingResponse::class.java)
                errorResponse.message?.let { message -> showToast(message) }
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UpdateSharingResponse::class.java)
            errorResponse.message?.let { message -> showToast(message) }
        } catch (e: Exception) {
            showToast("Error: ${e.message}")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@EditMyPostActivity, message, Toast.LENGTH_SHORT).show()
    }
}