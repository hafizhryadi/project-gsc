package com.ntz.distributor_app.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ntz.distributor_app.R // Pastikan R diimport dengan benar
import com.ntz.distributor_app.data.model.User // Model User aplikasi Anda
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await // Penting untuk await() pada Task GMS/Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(context, gso)
    }

    fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toUserModel()
    }

    fun getSignInClient(): Intent {
        return googleSignInClient.signInIntent
    }

    suspend fun handleSignInResultAndLoginFirebase(
        task: com.google.android.gms.tasks.Task<GoogleSignInAccount>
    ): Result<User> {
        return try {
            // Tahap 1: Dapatkan GoogleSignInAccount dari GMS Task
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                ?: throw ApiException(Status.RESULT_INTERNAL_ERROR) // Safety null check

            Log.d("AuthRepository", "Google Sign In (GMS) Success: ${account.email}")

            val idToken = account.idToken // Dapatkan ID Token dari GoogleSignInAccount
            if (idToken == null) {
                Log.e("AuthRepository", "GoogleSignInAccount idToken is null. Cannot proceed with Firebase Auth.")
                return Result.failure(Exception("ID Token Google Sign-In tidak ditemukan."))
            }

            // Tahap 2: Buat Firebase Auth Credential menggunakan ID Token
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Tahap 3: Login ke Firebase menggunakan credential
            Log.d("AuthRepository", "Attempting Firebase sign in with credential...")
            val authResult = firebaseAuth.signInWithCredential(credential).await() // Tunggu hasil Firebase Auth
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                Log.i("AuthRepository", "Firebase Authentication Success. UID: ${firebaseUser.uid}, Email: ${firebaseUser.email}")
                Result.success(firebaseUser.toUserModel()) // Kembalikan User model aplikasi
            } else {
                Log.e("AuthRepository", "Firebase Authentication failed after GMS success (firebaseUser is null).")
                Result.failure(Exception("Gagal autentikasi dengan Firebase (user null)."))
            }
        } catch (e: ApiException) {
            // Tangani error spesifik dari Google Play Services (misal: user membatalkan, error jaringan GMS)
            Log.w("AuthRepository", "Google Sign In Failed (GMS ApiException): statusCode=${e.statusCode}, message=${e.message}", e)
            Result.failure(Exception("Login Google gagal: ${e.localizedMessage} (Kode: ${e.statusCode})", e))
        } catch (e: Exception) {
            // Tangani error umum lainnya (misal: saat await() signInWithCredential, atau error tak terduga)
            Log.e("AuthRepository", "Firebase Authentication or GMS result handling failed: ${e.message}", e)
            Result.failure(Exception("Terjadi kesalahan saat login: ${e.localizedMessage}", e))
        } as Result<User>
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            Log.d("AuthRepository", "Attempting to sign out...")
            firebaseAuth.signOut() // Logout dari Firebase
            googleSignInClient.signOut().await() // Logout dari Google Client GMS
            Log.i("AuthRepository", "Signed out from Firebase and Google successfully.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing out: ${e.message}", e)
            Result.failure(Exception("Gagal logout: ${e.localizedMessage}", e))
        }
    }

    private fun FirebaseUser.toUserModel(): User? {
        return User(
            uid = this.uid,
            email = this.email,
            displayName = this.displayName,
            photoUrl = this.photoUrl?.toString()
        )
    }
}


