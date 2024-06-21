package com.example.agronect.ui.home

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.agronect.databinding.FragmentHomeBinding
import com.example.agronect.ui.Apple.AddAppleActivity
import com.example.agronect.ui.Banana.AddBananaActivity
import com.example.agronect.ui.Cassava.CassavaActivity
import com.example.agronect.ui.ViewModelFactory
import com.example.agronect.ui.Corn.CornActivity
import com.example.agronect.ui.Orange.OrangeActivity
import com.example.agronect.ui.Potato.AddPotatoActivity
import com.example.agronect.ui.Rice.AddRiceActivity
import com.example.agronect.ui.Tomato.AddTomatoActivity
import com.example.agronect.ui.main.MainViewModel
import com.example.agronect.ui.welcome.WelcomeActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var token = "token"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvPlant1.setOnClickListener{
            val intent = Intent(requireContext(), CornActivity::class.java)
            startActivity(intent)
        }

        binding.cvPlant2.setOnClickListener{
            val intent = Intent(requireContext(), AddPotatoActivity::class.java)
            startActivity(intent)
        }

        binding.cvPlant3.setOnClickListener{
            val intent = Intent(requireContext(), CassavaActivity::class.java)
            startActivity(intent)
        }

        binding.cvPlant4.setOnClickListener{
            val intent = Intent(requireContext(), AddBananaActivity::class.java)
            startActivity(intent)
        }

        binding.cvPlant5.setOnClickListener{
            val intent = Intent(requireContext(), OrangeActivity::class.java)
            startActivity(intent)
        }

        binding.cvPlant6.setOnClickListener{
            val intent = Intent(requireContext(), AddRiceActivity::class.java)
            startActivity(intent)
        }

        binding.cvPlant7.setOnClickListener{
            val intent = Intent(requireContext(), AddTomatoActivity::class.java)
            startActivity(intent)
        }

        binding.cvPlant8.setOnClickListener{
            val intent = Intent(requireContext(), AddAppleActivity::class.java)
            startActivity(intent)
        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity?.finish()
            } else {
                val mainViewModel = obtainViewModel()

                token = user.token
                Log.d(ContentValues.TAG, "token: $token")
                binding.tvName.text = user.name
                user.imgUrl.let { url ->
                    Glide.with(this)
                        .load(url)
                        .into(binding.ivLogoprofile)
                }

                // Uncomment and implement these lines if you need story data
                // mainViewModel.getStory(token)
                // mainViewModel.story.observe(viewLifecycleOwner) { storyList ->
                //     Log.d(ContentValues.TAG, "Story: $storyList")
                //     setStoryData(storyList)
                // }
            }
        }
    }

    private fun obtainViewModel(): MainViewModel {
        val factory = ViewModelFactory.getInstance(requireActivity().application)
        return ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}