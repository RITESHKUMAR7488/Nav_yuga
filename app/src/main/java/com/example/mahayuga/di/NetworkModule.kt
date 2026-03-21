package com.example.mahayuga.di

import com.example.mahayuga.feature.profile.data.remote.ImageUploadApi
import com.example.mahayuga.feature.navyuga.data.remote.YahooFinanceApi
import com.example.mahayuga.feature.navyuga.data.remote.AccelPixApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // --- EXISTING IMAGE UPLOAD PIPELINE ---

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://freeimage.host/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideImageUploadApi(retrofit: Retrofit): ImageUploadApi {
        return retrofit.create(ImageUploadApi::class.java)
    }

    // --- EXISTING YAHOO FINANCE PIPELINE ---

    @Provides
    @Singleton
    @Named("YahooInterceptor")
    fun provideRapidApiInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-rapidapi-key", "91b8c61787mshbd2d9597c9a9ba7p1ee43bjsndf6c9871521d")
                .addHeader("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    @Named("YahooHttpClient")
    fun provideYahooOkHttpClient(@Named("YahooInterceptor") interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideYahooFinanceApi(@Named("YahooHttpClient") okHttpClient: OkHttpClient): YahooFinanceApi {
        return Retrofit.Builder()
            .baseUrl("https://apidojo-yahoo-finance-v1.p.rapidapi.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YahooFinanceApi::class.java)
    }

    // --- NEW ACCELPIX PIPELINE ---

    @Provides
    @Singleton
    fun provideAccelPixOkHttpClient(): OkHttpClient {
        // Adding a standard HTTP Client with timeouts to prevent infinite hanging on bad network calls
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAccelPixApi(okHttpClient: OkHttpClient): AccelPixApi {
        return Retrofit.Builder()
            .baseUrl("https://apidata.accelpix.in/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AccelPixApi::class.java)
    }
}