package com.example.navyuga.feature.profile.data.remote

import com.example.navyuga.feature.profile.data.model.ImageUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ImageUploadApi {
    @Multipart
    @POST("api/1/upload")
    suspend fun uploadImage(
        @Part("key") key: RequestBody,
        @Part source: MultipartBody.Part,
        @Part("format") format: RequestBody
    ): ImageUploadResponse
}