package com.example.agronect.ui.Corn

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agronect.R
import com.example.agronect.databinding.ActivityAllClassificationResultBinding

class AllClassificationResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllClassificationResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllClassificationResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        val imageUri = intent.getStringExtra("imageUri")
        val confidence = intent.getStringExtra("confidence")
        val predictions = intent.getStringExtra("predictions")
        val description = intent.getStringExtra("description")
        val solution = intent.getStringExtra("solution")
        val plantName = intent.getStringExtra("plantName")

        imageUri?.let {
            binding.ivImg.setImageURI(Uri.parse(it))
        }
        binding.tvResultconfidance.text = "$confidence %"
        binding.tvResultprediction.text = predictions
        binding.tvResultdesc.text = description
        binding.tvResultsolution.text = solution
        binding.tvResultPlantName.text = plantName
    }
}

