package com.ntz.distributor_app.data.repository

import com.ntz.distributor_app.data.local.UserPreferencesDataStore
import com.ntz.distributor_app.data.model.User
import com.ntz.distributor_app.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) {
    fun getUserPreferences(): Flow<User> = dataStore.userPreferencesFlow
    fun getUserTypePreference() : Flow<String> = dataStore.getUserTypePreferenceFlow
    suspend fun saveUser(user: User) = dataStore.saveUser(user)
    suspend fun updateUserType(userType: String) = dataStore.updateUserType(userType)
    suspend fun updateDataUser(userData : User) = dataStore.updateDataUser(userData)
    suspend fun clearDataUser() = dataStore.clearDataUser()
    // suspend fun savePreferences(preferences: UserPreferences) = dataStore.updatePreferences(preferences)
}