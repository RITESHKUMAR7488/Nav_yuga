package com.example.mahayuga.di

import com.example.mahayuga.feature.profile.data.remote.ImageUploadApi
import com.example.mahayuga.feature.navyuga.data.remote.LisunsApi
import com.example.mahayuga.feature.navyuga.data.remote.LisunsWebSocketClient
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideLisunsApi(okHttpClient: OkHttpClient): LisunsApi {
        return Retrofit.Builder()
            .baseUrl("https://test.lisuns.com:4532/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LisunsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLisunsWebSocketClient(client: OkHttpClient, gson: Gson): LisunsWebSocketClient {
        return LisunsWebSocketClient(client, gson)
    }

    // --- EXISTING IMAGE UPLOAD PIPELINE ---
    @Provides
    @Singleton
    fun provideImageUploadApi(okHttpClient: OkHttpClient): ImageUploadApi {
        return Retrofit.Builder()
            .baseUrl("https://freeimage.host/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImageUploadApi::class.java)
    }
}