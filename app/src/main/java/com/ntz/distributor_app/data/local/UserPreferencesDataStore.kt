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
import com.ntz.distributor_app.data.model.UserPreferences
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
        val PREF_CATEGORIES = stringSetPreferencesKey("preferred_categories")
        val PREF_LOCATION = stringPreferencesKey("preferred_location")
        val PREF_MIN_ORDER = intPreferencesKey("min_order")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val categories = preferences[PreferencesKeys.PREF_CATEGORIES] ?: emptySet()
            val location = preferences[PreferencesKeys.PREF_LOCATION] ?: ""
            val minOrder = preferences[PreferencesKeys.PREF_MIN_ORDER]

            UserPreferences(categories, location, minOrder)
        }

    suspend fun updateLocation(location: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_LOCATION] = location
        }
    }

//    suspend fun updateMinOrder(minOrder: Int?) {
//        dataStore.edit { preferences ->
//            if (minOrder == null) {
//                preferences.remove(PreferencesKeys.PREF_MIN_ORDER)
//            } else {
//                preferences[PreferencesKeys.PREF_MIN_ORDER] = minOrder
//            }
//        }
//    }

    suspend fun updatePreferences(userPreferences: UserPreferences) {
        dataStore.edit { preference ->
            preference[PreferencesKeys.PREF_CATEGORIES] = userPreferences.preferredCategories
            preference[PreferencesKeys.PREF_LOCATION] = userPreferences.preferredLocation
            if (userPreferences.minOrder == null) {
                preference.remove(PreferencesKeys.PREF_MIN_ORDER)
            } else {
                preference[PreferencesKeys.PREF_MIN_ORDER] = userPreferences.minOrder
            }
        }
    }
}