package com.example.agronect.ui.main

import android.content.ContentValues
import java.util.Date
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.auth0.android.jwt.JWT
import com.example.agronect.R
import com.example.agronect.databinding.ActivityMainBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.login.LoginActivity
import com.example.agronect.ui.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import java.util.Base64

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom) // Mengatur padding hanya pada bottom untuk menghindari overlapping dengan bottom navigation
            insets
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToLogin()
                showToast("Harap Login Kembali")
            } else {
                token = user.token
                if (isTokenExpired(token)) {
                    navigateToLogin()
                } else {
                    val mainViewModel = obtainViewModel(this@MainActivity)
                    mainViewModel.isLoading.observe(this) {
                        showLoading(it)
                    }
                }
            }
        }
    }

    private fun isTokenExpired(token: String?): Boolean {
        return try {
            val parts = token?.split(".")
            if (parts != null && parts.size == 3) {
                val payload = String(Base64.getDecoder().decode(parts[1]), Charsets.UTF_8)
                val json = Gson().fromJson(payload, Map::class.java)
                val exp = (json["exp"] as Double).toLong()
                val currentTime = System.currentTimeMillis() / 1000
                currentTime > exp
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
