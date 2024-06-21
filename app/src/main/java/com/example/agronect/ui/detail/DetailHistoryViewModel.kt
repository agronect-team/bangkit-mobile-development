//package com.example.agronect.ui.detail
//
//import android.content.ContentValues
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.asLiveData
//import com.example.agronect.data.UserRepository
//import com.example.agronect.data.pref.UserModel
//import com.example.agronect.data.response.DataById
//import com.example.agronect.data.response.DataHistoryItem
//import com.example.agronect.data.response.DetailSharingResponse
//import com.example.agronect.data.response.HistoryResponse
//import com.example.agronect.data.retrofit.ApiConfig
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class DetailHistoryViewModel (private val repository: UserRepository) : ViewModel() {
//    private val mdetail = MutableLiveData<List<DataHistoryItem>?>()
//    val detail: MutableLiveData<List<DataHistoryItem>?> = mdetail
//
//    private val misLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = misLoading
//
//    fun getDetailHistory(token: String, id: String) {
//        misLoading.value = true
//        val client = ApiConfig.getApiService().getDetailHistory("Bearer $token", id)
//        client.enqueue(object : Callback<HistoryResponse> {
//            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
//                if (response.isSuccessful) {
//                    misLoading.value = false
//                    val detailSharing = response.body()?.data
//                    if (detailSharing != null) {
//                        mdetail.postValue(detailSharing)
//                    } else {
//                        Log.e("DetailViewModel", "Error: dataGetById is null. Response body: ${response.body()}")
//                    }
//                } else {
//                    Log.e("DetailViewModel", "Error: Response not successful")
//                }
//            }
//
//
//            override fun onFailure(call: Call<DetailSharingResponse>, t: Throwable) {
//                misLoading.value = false
//                Log.e(ContentValues.TAG, "onFailure2: ${t.message.toString()}")
//            }
//        })
//    }
//
//    fun getSession(): LiveData<UserModel> {
//        return repository.getSession().asLiveData()
//    }
//}
//
