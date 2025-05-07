package com.ntz.distributor_app.data.repository

import com.ntz.distributor_app.data.local.UserPreferencesDataStore
import com.ntz.distributor_app.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) {
    fun getUserPreferences(): Flow<UserPreferences> = dataStore.userPreferencesFlow
    suspend fun savePreferences(preferences: UserPreferences) = dataStore.updatePreferences(preferences)
}