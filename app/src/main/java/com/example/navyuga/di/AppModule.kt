package com.example.navyuga.di

import android.content.Context
import com.example.navyuga.core.data.local.PreferenceManager
import com.example.navyuga.feature.admin.data.repository.AdminRepositoryImpl
import com.example.navyuga.feature.admin.domain.repository.AdminRepository
import com.example.navyuga.feature.arthyuga.data.repository.PropertyRepositoryImpl
import com.example.navyuga.feature.arthyuga.domain.repository.PropertyRepository
import com.example.navyuga.feature.auth.data.repository.AuthRepositoryImpl
import com.example.navyuga.feature.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    // ⚡ KEEP THIS (Delete the separate AdminModule file)
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
}