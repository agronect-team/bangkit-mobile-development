package com.example.agronect.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.agronect.data.UserRepository
import com.example.agronect.data.pref.UserModel
import com.example.agronect.data.response.DataHistoryItem
import com.example.agronect.data.response.HistoryResponse
import com.example.agronect.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel(private val repository: UserRepository) : ViewModel() {
    private val _story = MutableLiveData<List<DataHistoryItem>?>()
    val story: LiveData<List<DataHistoryItem>?> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getHistory(token: String, userId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getHistory("Bearer $token", userId)
        client.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _story.value = response.body()?.dataHistory as List<DataHistoryItem>?
                } else {
                    Log.e("MyHistoryViewModel", "onFailure1: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("MyHistoryViewModel", "onFailure2: ${t.message.toString()}")
            }
        })
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}