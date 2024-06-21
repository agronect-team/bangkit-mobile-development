package com.example.agronect.ui.mypost

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agronect.R
import com.example.agronect.data.response.DataGetAllItem
import com.example.agronect.data.response.DataItem
import com.example.agronect.databinding.ActivityMainBinding
import com.example.agronect.databinding.ActivityMypostBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.adapter.MyPostAdapter
import com.example.agronect.ui.adapter.StoriesAdapter
import com.example.agronect.ui.detail.DetailActivity
import com.example.agronect.ui.detail.DetailMyPostActivity
import com.example.agronect.ui.editmypost.EditMyPostActivity
import com.example.agronect.ui.main.MainViewModel
import com.example.agronect.ui.welcome.WelcomeActivity
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MyPostActivity : AppCompatActivity() {
    private val viewModel by viewModels<MyPostViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMypostBinding
    private lateinit var token: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                token = user.token
                userId = user.uid
                Log.d("MyPostActivity", "token: $token")

                viewModel.myPostSharing(token, userId)
                viewModel.story.observe(this) { storyList ->
                    Log.d("MyPostActivity", "Story: $storyList")
                    setStoryData(storyList)
                }
                viewModel.isLoading.observe(this) {
                    showLoading(it)
                }
            }
        }
    }

    private fun setStoryData(listStory: List<DataItem>?) {
        Log.d("MyPostActivity", "setStoryData: $listStory")
        val adapter = MyPostAdapter()
        adapter.submitList(listStory)
        binding.rvStories.adapter = adapter
        adapter.setOnItemClickCallback(object : MyPostAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataItem) {
                Intent(this@MyPostActivity, EditMyPostActivity::class.java).also {
                    it.putExtra("sharingId", data.sharingId)
                    it.putExtra("description", data.content)
                    it.putExtra("imgUrl", data.imgUrl.toString())
                    startActivity(it)
                }
            }
        })
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        supportActionBar?.title = "Dicoding Story"
    }

    private fun showLoading(state: Boolean) {
        binding.progressbarRV.visibility = if (state) View.VISIBLE else View.GONE
    }
}
