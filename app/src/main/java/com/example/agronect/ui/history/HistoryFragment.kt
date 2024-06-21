package com.example.agronect.ui.history

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import com.example.agronect.data.response.DataHistoryItem
import com.example.agronect.data.response.Image
import com.example.agronect.databinding.FragmentHistoryBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.adapter.ListHistoryAdapter
import com.example.agronect.ui.detail.DetailHistoryActivity
import com.example.agronect.ui.login.LoginActivity
import com.example.agronect.ui.main.MainViewModel
import com.example.agronect.ui.sharingpage.SharingPageViewModel
import com.example.agronect.ui.welcome.WelcomeActivity
import java.io.ByteArrayOutputStream
import java.util.Date

class HistoryFragment : Fragment() {
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var token: String
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvHistory.addItemDecoration(itemDecoration)

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                requireActivity().finish()
            } else {
                token = user.token
                userId = user.uid
                if (isTokenExpired(token)) {
                    navigateToLogin()
                } else {
                    Log.d("HistoryFragment", "token: $token")

                    viewModel.getHistory(token, userId)
                    viewModel.story.observe(viewLifecycleOwner) { storyList ->
                        Log.d("HistoryFragment", "Story: $storyList")
                        setStoryData(storyList)
                    }
                    val mainViewModel = obtainViewModel(this@HistoryFragment.requireActivity() as AppCompatActivity)
                    mainViewModel.isLoading.observe(viewLifecycleOwner) {
                        showLoading(it)
                    }
                }
            }
        }
    }

    private fun setStoryData(listHistory: List<DataHistoryItem>?) {
        Log.d("HistoryFragment", "setStoryData: $listHistory")
        val adapter = ListHistoryAdapter()
        adapter.submitList(listHistory)
        binding.rvHistory.adapter = adapter
        adapter.setOnItemClickCallback(object : ListHistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataHistoryItem) {
                val bitmap = data.image?.let { convertToBitmap(it) }
                val byteArray = bitmap?.let { convertBitmapToByteArray(it) }

                Intent(requireContext(), DetailHistoryActivity::class.java).also {
                    it.putExtra("img", byteArray)
                    it.putExtra("name", data.plantName)
                    it.putExtra("prediction", data.prediction)
                    it.putExtra("description", data.description)
                    it.putExtra("confidence", data.confidence.toString())
                    it.putExtra("solution", data.solution)
                    startActivity(it)
                }
            }
        })
        // Hide the progress bar after setting the data
        showLoading(false)
    }

    private fun convertToBitmap(image: Image): Bitmap? {
        val data = image.data ?: return null
        val byteArray = data.mapNotNull { it?.toByte() }.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    private fun isTokenExpired(token: String?): Boolean {
        if (token == null) {
            return true
        }

        return try {
            val jwt = JWT(token)
            val exp = jwt.expiresAt
            exp == null || exp.before(Date())
        } catch (e: Exception) {
            true // Jika ada kesalahan dalam parsing token, anggap token kadaluarsa
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun showLoading(state: Boolean) {
        binding.progressbarRV.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dicoding Story"
    }

    private fun obtainViewModel(activity: AppCompatActivity): SharingPageViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[SharingPageViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
