package com.example.agronect.ui.Potato

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.agronect.data.response.CornResponse
import com.example.agronect.data.response.PotatoResponse
import com.example.agronect.data.retrofit.ApiConfig2
import com.example.agronect.data.util.getImageUri
import com.example.agronect.data.util.reduceFileImage
import com.example.agronect.data.util.uriToFile
import com.example.agronect.databinding.ActivityAddPotatoBinding
import com.example.agronect.ui.Corn.AllClassificationResultActivity
import com.example.agronect.ui.Potato.PotatoViewModel
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class AddPotatoActivity : AppCompatActivity(){
    private lateinit var binding: ActivityAddPotatoBinding

    private val viewModel by viewModels<PotatoViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var token : String = ""

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPotatoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token
                binding.galleryButton.setOnClickListener { startGallery() }
                binding.cameraButton.setOnClickListener { startCamera() }
                binding.uploadButton.setOnClickListener {
                    uploadPotato()
                }
            }
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

    private fun uploadPotato() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)

            // Validate the file extension
            if (!isValidImageFormat(imageFile.name)) {
                showToast("Only JPG, JPEG, and PNG formats are allowed")
                return
            }

            val bitmap = BitmapFactory.decodeFile(imageFile.path)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

            // Compress bitmap to JPEG format and reduce file size to be under 50KB
            val outputStream = ByteArrayOutputStream()
            var quality = 100
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            while (outputStream.size() > 50 * 1024 && quality > 0) {
                outputStream.reset()
                quality -= 5
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            val compressedByteArray = outputStream.toByteArray()

            // Create a temp file to hold the compressed image
            val tempFile = createTempFile("compressed_", ".jpg", cacheDir)
            tempFile.writeBytes(compressedByteArray)

            showLoading(true)

            val requestImageFile = tempFile.asRequestBody("image/jpeg".toMediaType())
            val imagePart = MultipartBody.Part.createFormData("file", tempFile.name, requestImageFile)

            lifecycleScope.launch {
                try {
                    val apiService = ApiConfig2.getApiService2()
                    val call = apiService.uploadPotato("Bearer $token", imagePart)

                    call.enqueue(object : Callback<PotatoResponse> {
                        override fun onResponse(call: Call<PotatoResponse>, response: Response<PotatoResponse>) {
                            showLoading(false)
                            if (response.isSuccessful) {
                                response.body()?.let { PotatoResponse ->
                                    Log.e(ContentValues.TAG, "onSuccess: ${response.message()}")
                                    showToast("Upload successful")
                                    navigateToResultActivity(PotatoResponse)
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e(ContentValues.TAG, "onFailure1: $errorBody")
                                showToast("Upload failed: $errorBody")
                            }
                        }

                        override fun onFailure(call: Call<PotatoResponse>, t: Throwable) {
                            showLoading(false)
                            Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
                            showToast("Upload failed: ${t.message.toString()}")
                        }
                    })
                } catch (e: HttpException) {
                    showLoading(false)
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(ContentValues.TAG, "onFailure3: $errorBody")
                    showToast("Upload failed: $errorBody")
                }
            }
        } ?: showToast("Please select an image first")
    }

    private fun createTempFile(prefix: String, suffix: String, directory: File): File {
        return File.createTempFile(prefix, suffix, directory)
    }

    private fun navigateToResultActivity(potatoResponse: PotatoResponse) {
        val intent = Intent(this, AllClassificationResultActivity::class.java).apply {
            putExtra("imageUri", currentImageUri.toString())
            putExtra("confidence", potatoResponse.confidence.toString())
            putExtra("predictions", potatoResponse.prediction)
            putExtra("description", potatoResponse.description)
            putExtra("solution", potatoResponse.solution)
            putExtra("plantName", potatoResponse.plantName)
        }
        startActivity(intent)
        finish()  // Finish current activity if you don't want to return back to it
    }

    private fun isValidImageFormat(fileName: String): Boolean {
        val validExtensions = listOf("jpg", "jpeg", "png")
        val fileExtension = fileName.substringAfterLast('.', "").lowercase()
        return validExtensions.contains(fileExtension)
    }

    private fun backToMainActivity() {
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}