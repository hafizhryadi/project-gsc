package com.ntz.distributor_app.firebasedatabase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.ntz.distributor_app.data.model.User

class FirebaseRealtimeAuth {
    fun firebaseInitAuth() : DatabaseReference{
        return Firebase.database.reference
    }

    fun setAuthUser(
        uid: String?,
        role : String?,
        email: String?,
        displayName: String?,
        photoUrl: String?
    ){
        val dataMap = mapOf(
           User::uid.name to uid,
           User::role.name to role,
           User::email.name to email,
           User::displayName.name to displayName,
           User::photoUrl.name to photoUrl
        )

        firebaseInitAuth().child("users").child(uid ?: "").setValue(dataMap)
            .addOnSuccessListener {
                Log.d("FirebaseRealtimeDatabaseAuthRepository", "Data added successfully")
            }
            .addOnFailureListener {
                Log.e("FirebaseRealtimeDatabaseAuthRepository", "Error adding data: ${it.message}")
            }
    }
}