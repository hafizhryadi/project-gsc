package com.ntz.distributor_app.data.repository

import com.ntz.distributor_app.firebasedatabase.FirebaseRealtimeAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRealtimeDatabaseAuthRepository @Inject constructor(
    private val firebaseRealtimeAuth: FirebaseRealtimeAuth
) {
    fun addAuthUser(
        uid: String?,
        role : String?,
        email: String?,
        displayName: String?,
        photoUrl: String?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) = firebaseRealtimeAuth.setAuthUser(
        uid,
        role,
        email,
        displayName,
        photoUrl,
        onSuccess,
        onFailure
    )
}