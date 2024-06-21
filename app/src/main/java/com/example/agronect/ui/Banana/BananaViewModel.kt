package com.example.agronect.ui.Banana

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.agronect.data.UserRepository
import com.example.agronect.data.pref.UserModel

class BananaViewModel(private val repository: UserRepository) : ViewModel()  {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}