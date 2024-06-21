package com.example.agronect.ui.mypost

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.agronect.data.UserRepository
import com.example.agronect.data.pref.UserModel
import com.example.agronect.data.response.DataItem
import com.example.agronect.data.response.SharingByUserIDResponse
import com.example.agronect.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostViewModel(private val repository: UserRepository) : ViewModel() {
    private val _story = MutableLiveData<List<DataItem>?>()
    val story: LiveData<List<DataItem>?> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun myPostSharing(token: String, userId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().myPostSharing("Bearer $token", userId)
        client.enqueue(object : Callback<SharingByUserIDResponse> {
            override fun onResponse(
                call: Call<SharingByUserIDResponse>,
                response: Response<SharingByUserIDResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _story.value = response.body()?.data as List<DataItem>?
                } else {
                    Log.e("MyPostViewModel", "onFailure1: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SharingByUserIDResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("MyPostViewModel", "onFailure2: ${t.message.toString()}")
            }
        })
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}
