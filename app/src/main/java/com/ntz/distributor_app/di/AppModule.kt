package com.ntz.distributor_app.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ntz.distributor_app.data.local.UserPreferencesDataStore
import com.ntz.distributor_app.data.remote.GeminiApiService
import com.ntz.distributor_app.data.repository.AuthRepository
import com.ntz.distributor_app.data.repository.DistributorRepository
import com.ntz.distributor_app.data.repository.RecommendationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // context aplikasi
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    // Datastore
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): UserPreferencesDataStore {
        return UserPreferencesDataStore(context)
    }

    // instance firebaseAuth
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    // geminiApiService
    @Provides
    @Singleton
    fun provideGeminiApiService(): GeminiApiService {
        return GeminiApiService()
    }

    // Menyediakan Repository (dependency lain akan diinject otomatis oleh Hilt jika disediakan di atas)
    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context): AuthRepository {
        return AuthRepository(context)
    }

    @Provides
    @Singleton
    fun provideDistributorRepository(): DistributorRepository {
        return DistributorRepository()
    }

    @Provides
    @Singleton
    fun provideRecommendationRepository(geminiApiService: GeminiApiService): RecommendationRepository {
        return RecommendationRepository(geminiApiService)
    }

}