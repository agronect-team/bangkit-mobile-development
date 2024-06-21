package com.example.agronect.ui.sharingpage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.agronect.data.response.DataGetAllItem
import com.example.agronect.databinding.FragmentSharingpageBinding
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.adapter.LoadingStateAdapter
import com.example.agronect.ui.adapter.StoriesAdapter
import com.example.agronect.ui.addList.AddListActivity
import com.example.agronect.ui.detail.DetailActivity
import com.example.agronect.ui.login.LoginActivity
import com.example.agronect.ui.mypost.MyPostActivity
import com.google.gson.Gson
import java.util.Base64

class SharingPageFragment : Fragment() {
    private val viewModel by viewModels<SharingPageViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentSharingpageBinding? = null
    private val binding get() = _binding!!
    private var token = "token"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharingpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading2(true)
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            token = user.token
            if (isTokenExpired(token)) {
                // Jika token kadaluarsa, arahkan ke LoginActivity
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
                showToast("Harap Login Kembali")
            } else {
                viewModel.getStory(token).observe(viewLifecycleOwner) { storyList ->
                    val adapter = StoriesAdapter()
                    adapter.submitData(lifecycle, storyList)
                    binding.rvStories.adapter = adapter.withLoadStateFooter(
                        footer = LoadingStateAdapter {
                            adapter.retry()
                        }
                    )

                    adapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: DataGetAllItem) {
                            Intent(requireContext(), DetailActivity::class.java).also {
                                it.putExtra(DetailActivity.ID, data.sharingId)
                                it.putExtra(DetailActivity.DATE, data.createdAt)
                                startActivity(it)
                            }
                        }
                    })
                }
            }
        }
            showLoading2(false)
        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        setupView()

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddListActivity::class.java))
        }
        binding.mySharing.setOnClickListener {
            startActivity(Intent(requireContext(), MyPostActivity::class.java))
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
    }

    private fun showLoading(state: Boolean) {
        binding.progressbar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showLoading2(state: Boolean) {
        binding.progressbarRV.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        if (message != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Message is null", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

