package com.example.agronect.ui.Orange

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.agronect.data.UserRepository
import com.example.agronect.data.pref.UserModel

class OrangeViewModel(private val repository: UserRepository) : ViewModel()  {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}