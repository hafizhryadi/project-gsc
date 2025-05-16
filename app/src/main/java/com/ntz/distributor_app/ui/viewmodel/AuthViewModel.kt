package com.ntz.distributor_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.ntz.distributor_app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class SignInState{
    object Idle : SignInState()
    object Loading : SignInState()
    data class Success(val user: FirebaseUser) : SignInState()
    data class Error(val message: String) : SignInState()
}
class AuthViewModel : ViewModel(){

    private val auth : FirebaseAuth = Firebase.auth
    private val database : DatabaseReference = Firebase.database.reference

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState : StateFlow<SignInState> = _signInState

    init{
        if(auth.currentUser != null){
            _signInState.value = SignInState.Success(auth.currentUser!!)
        }
    }

    fun signInWithGoogle(idToken : String){
        viewModelScope.launch {
            _signInState.value = SignInState.Loading

            try{
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                authResult.user?.let{ firebaseUser ->
                    // save user data to realtime database
                    saveUserDataToRealtimeDatabase(firebaseUser)
                    _signInState.value = SignInState.Success(firebaseUser)
                    Log.d("AuthViewModel", "User data saved to Realtime Database")
                } ?: run{
                    _signInState.value = SignInState.Error("User data is null")
                    Log.e("AuthViewModel", "User data is null")
                }
            } catch (e : Exception){
                _signInState.value = SignInState.Error(e.message ?: "Unknown error")
                Log.e("AuthViewModel", "Error signing in with Google: ${e.message}")
            }
        }
    }

    fun signOut(onSuccess : () -> Unit){
        try{
            auth.signOut()
            onSuccess()
            _signInState.value = SignInState.Idle
        } catch (e : Exception){
            _signInState.value = SignInState.Error(e.message ?: "Unknown error")
        }
    }


    private suspend fun saveUserDataToRealtimeDatabase(user: FirebaseUser) {
        val userData = User(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString(),
            role = "belum_dipilih"
        )

        try{
            database.child("users").child(user.uid).setValue(userData).await()
            Log.d("AuthViewModel", "User data saved to Realtime Database")
        } catch (e : Exception){
            Log.e("AuthViewModel", "Error saving user data to Realtime Database: ${e.message}")
        }
    }

    fun updateRole(role : String){
        database.child("users").child(Firebase.auth.currentUser?.uid ?: "").child("role").setValue(role)
            .addOnSuccessListener {
                _signInState.value = SignInState.Success(auth.currentUser!!)
                Log.d("FirebaseRealtimeAgent", "Role updated successfully")
            }
            .addOnFailureListener {
                _signInState.value = SignInState.Error("Error updating role: ${it.message}")
                Log.e("FirebaseRealtimeAgent", "Error updating role: ${it.message}")
            }
    }

    fun resetState(){
        _signInState.value = SignInState.Idle
    }

}