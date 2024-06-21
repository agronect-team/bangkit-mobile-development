package com.example.agronect.ui.detail

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.agronect.R
import com.example.agronect.databinding.ActivityDetailHistoryBinding
import com.example.agronect.databinding.ActivityDetailPostBinding
import com.example.agronect.ui.ViewModelFactory

class DetailHistoryActivity : AppCompatActivity() {
//    private val viewModel by viewModels<DetailHistoryViewModel> {
//        ViewModelFactory.getInstance(this)
//    }
    private lateinit var binding: ActivityDetailHistoryBinding
    private var token = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Mengambil data dari intent
        val prediction = intent.getStringExtra("prediction")
        val description = intent.getStringExtra("description")
        val confidence = intent.getStringExtra("confidence")
        val solution = intent.getStringExtra("solution")
        val image = intent.getByteArrayExtra("img")
        val plantName = intent.getStringExtra("name")

        image?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.ivImg.setImageBitmap(bitmap)
        }

        // Menampilkan data di view
        binding.apply {
            tvResultprediction.text = prediction
            tvResultconfidance.text = confidence
            tvResultdesc.text = description
            tvResultsolution.text = solution
            tvResultName.text = plantName
        }

        // Contoh untuk log data yang diterima
        Log.d(ContentValues.TAG, "Received prediction: $prediction")
        Log.d(ContentValues.TAG, "Received confidence: $confidence")
        Log.d(ContentValues.TAG, "Received description: $description")
        Log.d(ContentValues.TAG, "Received solution: $solution")
    }
}
