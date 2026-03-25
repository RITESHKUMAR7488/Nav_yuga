package com.example.mahayuga.di

import android.content.Context
import android.content.SharedPreferences
import com.example.mahayuga.core.data.local.PreferenceManager
import com.example.mahayuga.feature.admin.data.repository.AdminRepositoryImpl
import com.example.mahayuga.feature.admin.domain.repository.AdminRepository
import com.example.mahayuga.feature.navyuga.domain.repository.PropertyRepository
import com.example.mahayuga.feature.auth.domain.repository.AuthRepositoryImpl
import com.example.mahayuga.feature.auth.domain.repository.AuthRepository
import com.example.mahayuga.feature.navyuga.data.remote.LisunsApi
import com.example.mahayuga.feature.navyuga.data.remote.LisunsWebSocketClient
import com.example.mahayuga.feature.navyuga.data.repository.PropertyRepositoryImpl
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepository
import com.example.mahayuga.feature.navyuga.domain.repository.MarketRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(context)
    }

    // ⚡ NEW: Provides Local Storage for our Market Cache
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("mahayuga_market_cache", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAdminRepository(firestore: FirebaseFirestore): AdminRepository {
        return AdminRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun providePropertyRepository(firestore: FirebaseFirestore): PropertyRepository {
        return PropertyRepositoryImpl(firestore)
    }

    // ⚡ UPDATED: Injecting SharedPreferences and Gson into the MarketRepository
    @Provides
    @Singleton
    fun provideMarketRepository(
        api: LisunsApi,
        wsClient: LisunsWebSocketClient,
        sharedPreferences: SharedPreferences,
        gson: Gson
    ): MarketRepository {
        return MarketRepositoryImpl(api, wsClient, sharedPreferences, gson)
    }
}