package com.example.agronect.data.retrofit

import com.example.agronect.data.response.AddSharingResponse
import com.example.agronect.data.response.AppleResponse
import com.example.agronect.data.response.BananaResponse
import com.example.agronect.data.response.CassavaResponse
import com.example.agronect.data.response.CornResponse
import com.example.agronect.data.response.DetailSharingResponse
import com.example.agronect.data.response.GetAllSharingPagingResponse
import com.example.agronect.data.response.HistoryResponse
import com.example.agronect.data.response.LoginResponse
import com.example.agronect.data.response.OrangeResponse
import com.example.agronect.data.response.PotatoResponse
import com.example.agronect.data.response.RegisterResponse
import com.example.agronect.data.response.RiceResponse
import com.example.agronect.data.response.SharingByUserIDResponse
import com.example.agronect.data.response.TomatoResponse
import com.example.agronect.data.response.UpdatePasswordResponse
import com.example.agronect.data.response.UpdateProfilePhotoResponse
import com.example.agronect.data.response.UpdateProfileResponse
import com.example.agronect.data.response.UpdateSharingResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("signup")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("signin")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("sharing")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part("content") content: RequestBody,
        @Part imgUrl: MultipartBody.Part?
    ): Call<AddSharingResponse>

    @GET("sharing")
    fun getAllSharing(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Call<GetAllSharingPagingResponse>

    @GET("sharing/{sharing_id}")
    fun getDetailSharing(
        @Header("Authorization") token: String,
        @Path("sharing_id") sharingid: String
    ): Call<DetailSharingResponse>

    @Multipart
    @PUT("users/update-users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @PartMap userInfo: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imgUrl: MultipartBody.Part? = null
    ): Response<UpdateProfileResponse>

    @PUT("users/change-password/{user_id}")
    fun changePassword(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
        @Body passwordChangeRequest: PasswordChangeRequest
    ): Call<UpdatePasswordResponse>

    @Multipart
    @POST("users/{user_id}/upload-photoprofile")
    fun uploadProfilePhoto(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
        @Part imgUrl: MultipartBody.Part?
        ): Call<UpdateProfilePhotoResponse>

    @GET("sharing/users/{user_id}")
    fun myPostSharing(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String,
    ): Call<SharingByUserIDResponse>

    @Multipart
    @PUT("sharing/{sharing_id}")
    suspend fun updateSharing(
        @Header("Authorization") token: String,
        @Path("sharing_id") sharingId: String,
        @Part("content") content: RequestBody,
        @Part imgUrl: MultipartBody.Part? = null
    ): Response<UpdateSharingResponse>

    @GET("history/{user_id}")
    fun getHistory(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): Call<HistoryResponse>

    @Multipart
    @POST("predict/corn")
    fun uploadCorn(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<CornResponse>

    @Multipart
    @POST("predict/potato")
    fun uploadPotato(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<PotatoResponse>

    @Multipart
    @POST("predict/apple")
    fun uploadApple(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<AppleResponse>

    @Multipart
    @POST("predict/banana")
    fun uploadBanana(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<BananaResponse>

    @Multipart
    @POST("predict/tomato")
    fun uploadTomato(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<TomatoResponse>

    @Multipart
    @POST("predict/rice")
    fun uploadRice(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<RiceResponse>

    @Multipart
    @POST("predict/cassava")
    fun uploadCassava(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<CassavaResponse>

    @Multipart
    @POST("predict/orange")
    fun uploadOrange(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<OrangeResponse>

}

data class PasswordChangeRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)