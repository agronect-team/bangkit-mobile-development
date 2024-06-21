package com.example.agronect.data

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.agronect.data.pref.UserModel
import com.example.agronect.data.pref.UserPreference
import com.example.agronect.data.response.DataGetAllItem
import com.example.agronect.ui.paging.PagingSource
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference
) {

    fun getAllSharing(token: String): LiveData<PagingData<DataGetAllItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                Log.d(ContentValues.TAG, "tokenrepository: $token")
                PagingSource(token)
            }
        ).liveData
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}