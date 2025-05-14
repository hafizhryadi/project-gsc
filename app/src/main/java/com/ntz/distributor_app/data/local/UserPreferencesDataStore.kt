package com.ntz.distributor_app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ntz.distributor_app.data.model.User
import com.ntz.distributor_app.util.Constants.PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

@Singleton
class UserPreferencesDataStore @Inject constructor(context: Context) {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        /*val PREF_CATEGORIES = stringSetPreferencesKey("preferred_categories")
        val PREF_LOCATION = stringPreferencesKey("preferred_location")
        val PREF_MIN_ORDER = intPreferencesKey("min_order")*/
        val PREF_USER_TYPE = stringPreferencesKey("user_type")
        val PREF_USER_ID = stringPreferencesKey("user_id")
        val PREF_USER_NAME = stringPreferencesKey("user_name")
        val PREF_USER_EMAIL = stringPreferencesKey("user_email")
        val PREF_PHOTO_URL = stringPreferencesKey("photo_url")

    }

    // this use for read all data from dataStore
    val userPreferencesFlow: Flow<User> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            /*val categories = preferences[PreferencesKeys.PREF_CATEGORIES] ?: emptySet()
            val location = preferences[PreferencesKeys.PREF_LOCATION] ?: ""
            val minOrder = preferences[PreferencesKeys.PREF_MIN_ORDER]*/

            val userType = preferences[PreferencesKeys.PREF_USER_TYPE] ?: ""
            val userId = preferences[PreferencesKeys.PREF_USER_ID] ?: ""
            val userName = preferences[PreferencesKeys.PREF_USER_NAME] ?: ""
            val userEmail = preferences[PreferencesKeys.PREF_USER_EMAIL] ?: ""
            val photoUrl = preferences[PreferencesKeys.PREF_PHOTO_URL] ?: ""

            User(userType,userId, userEmail, userName, photoUrl)
        }

    val getUserTypePreferenceFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

        .map {
            preferences ->
            preferences[PreferencesKeys.PREF_USER_TYPE] ?: ""
        }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_USER_TYPE] = user.userType ?: ""
            preferences[PreferencesKeys.PREF_USER_ID] = user.uid ?: ""
            preferences[PreferencesKeys.PREF_USER_NAME] = user.displayName ?: ""
            preferences[PreferencesKeys.PREF_USER_EMAIL] = user.email ?: ""
            preferences[PreferencesKeys.PREF_PHOTO_URL] = user.photoUrl ?: ""
        }
    }

    suspend fun updateUserType(userType: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_USER_TYPE] = userType
        }
    }

    suspend fun updateDataUser(userData : User){
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_USER_TYPE] = userData.userType ?: ""
            preferences[PreferencesKeys.PREF_USER_ID] = userData.uid ?: ""
            preferences[PreferencesKeys.PREF_USER_NAME] = userData.displayName ?: ""
            preferences[PreferencesKeys.PREF_USER_EMAIL] = userData.email ?: ""
            preferences[PreferencesKeys.PREF_PHOTO_URL] = userData.photoUrl ?: ""
        }
    }

    suspend fun clearDataUser(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    /*suspend fun updateLocation(location: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_LOCATION] = location
        }
    }*/

//    suspend fun updateMinOrder(minOrder: Int?) {
//        dataStore.edit { preferences ->
//            if (minOrder == null) {
//                preferences.remove(PreferencesKeys.PREF_MIN_ORDER)
//            } else {
//                preferences[PreferencesKeys.PREF_MIN_ORDER] = minOrder
//            }
//        }
//    }

   /* suspend fun updatePreferences(userPreferences: UserPreferences) {
        dataStore.edit { preference ->
            preference[PreferencesKeys.PREF_CATEGORIES] = userPreferences.preferredCategories
            preference[PreferencesKeys.PREF_LOCATION] = userPreferences.preferredLocation
            if (userPreferences.minOrder == null) {
                preference.remove(PreferencesKeys.PREF_MIN_ORDER)
            } else {
                preference[PreferencesKeys.PREF_MIN_ORDER] = userPreferences.minOrder
            }
        }
    }*/
}