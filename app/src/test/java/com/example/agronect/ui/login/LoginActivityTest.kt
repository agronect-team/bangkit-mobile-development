//package com.example.agronect.ui.login
//
//import org.junit.Assert.*
//
//import android.content.Intent
//import android.view.View
//import android.widget.Toast
//import androidx.lifecycle.lifecycleScope
//import androidx.test.core.app.ActivityScenario
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.yourapp.R
//import com.example.agronect.data.UserModel
//import com.example.agronect.data.retrofit.ApiService
//import com.example.yourapp.network.ApiService
//import com.example.yourapp.ui.login.LoginActivity
//import com.example.yourapp.ui.login.LoginViewModel
//import com.example.yourapp.ui.login.ViewModelFactory
//import com.google.common.truth.Truth.assertThat
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.runTest
//import okhttp3.mockwebserver.MockResponse
//import okhttp3.mockwebserver.MockWebServer
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mockito.*
//
//@ExperimentalCoroutinesApi
//@RunWith(AndroidJUnit4::class)
//class LoginActivityTest {
//
//    private lateinit var mockWebServer: MockWebServer
//    private lateinit var apiService: ApiService
//
//    @Before
//    fun setUp() {
//        mockWebServer = MockWebServer()
//        mockWebServer.start()
//
//        // Use MockWebServer url as the base url for ApiService
//        apiService = ApiService.create(mockWebServer.url("/").toString())
//    }
//
//    @After
//    fun tearDown() {
//        mockWebServer.shutdown()
//    }
//
//    @Test
//    fun login_success() = runTest {
//        val response = MockResponse()
//            .setResponseCode(200)
//            .setBody("{\"message\":\"Login successful\",\"loginResult\":{\"token\":\"sample_token\"}}")
//        mockWebServer.enqueue(response)
//
//        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
//            scenario.onActivity { activity ->
//                // Setup mocks and inputs
//                activity.binding.emailEditText.setText("test@example.com")
//                activity.binding.passwordEditText.setText("password")
//
//                activity.binding.loginButton.performClick()
//
//                // Wait for coroutine to finish
//                advanceUntilIdle()
//
//                // Assertions
//                assertThat(activity.viewModel.getUserSession().value?.token).isEqualTo("sample_token")
//                assertThat(activity.binding.progressBar.visibility).isEqualTo(View.GONE)
//                verify(activity).showToast(activity.getString(R.string.success_login))
//            }
//        }
//    }
//
//    @Test
//    fun login_failure() = runTest {
//        val response = MockResponse()
//            .setResponseCode(401)
//            .setBody("{\"message\":\"Invalid credentials\"}")
//        mockWebServer.enqueue(response)
//
//        ActivityScenario.launch(LoginActivity::class.java).use { scenario ->
//            scenario.onActivity { activity ->
//                // Setup mocks and inputs
//                activity.binding.emailEditText.setText("test@example.com")
//                activity.binding.passwordEditText.setText("wrong_password")
//
//                activity.binding.loginButton.performClick()
//
//                // Wait for coroutine to finish
//                advanceUntilIdle()
//
//                // Assertions
//                assertThat(activity.binding.progressBar.visibility).isEqualTo(View.GONE)
//                verify(activity).showToast("Invalid credentials")
//            }
//        }
//    }
//}
