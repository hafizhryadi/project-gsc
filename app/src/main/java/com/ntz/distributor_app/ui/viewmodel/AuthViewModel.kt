package com.ntz.distributor_app.ui.viewmodel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ntz.distributor_app.data.model.User
import com.ntz.distributor_app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    object Loading : AuthUiState
    object SignedOut : AuthUiState
    data class SignedIn(val user: User) : AuthUiState
    data class Error(val message: String, val cause: Throwable? = null) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    // StateFlow untuk menyimpan dan mengamati state UI autentikasi
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Cek status login pengguna saat ViewModel diinisialisasi
        checkCurrentUser()
    }

    /**
     * Memeriksa apakah ada pengguna yang saat ini login (sesi aktif).
     */
    private fun checkCurrentUser() {
        val currentUser = authRepository.getCurrentUser() // Dapatkan user dari repository
        if (currentUser != null) {
            Log.i("AuthViewModel", "Pengguna sudah login (Firebase): UID ${currentUser.uid}")
            _uiState.value = AuthUiState.SignedIn(currentUser)
        } else {
            Log.i("AuthViewModel", "Tidak ada pengguna yang login (Firebase).")
            _uiState.value = AuthUiState.SignedOut
        }
    }

    /**
     * Mendapatkan Intent yang akan digunakan untuk memulai alur Google Sign In.
     */
    fun getSignInIntent(): Intent {
        return authRepository.getSignInClient()
    }

    /**
     * Menangani hasil dari Activity Google Sign In.
     * @param result Hasil dari Activity Google Sign In.
     */
    fun handleSignInResult(result: ActivityResult) {
        // Cek apakah hasilnya OK sebelum memproses lebih lanjut
        if (result.resultCode == Activity.RESULT_OK) {
            // Dapatkan task untuk mengambil akun Google dari data Intent
            val signInTask: com.google.android.gms.tasks.Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)

            viewModelScope.launch {
                _uiState.value = AuthUiState.Loading // Set state ke loading
                // Panggil fungsi di repository untuk memproses hasil GMS dan login ke Firebase
                val firebaseLoginResult = authRepository.handleSignInResultAndLoginFirebase(signInTask)

                firebaseLoginResult.onSuccess { user ->
                    Log.i("AuthViewModel", "Login Firebase berhasil: UID ${user.uid}")
                    _uiState.value = AuthUiState.SignedIn(user) // Update state ke SignedIn
                }.onFailure { exception ->
                    Log.e("AuthViewModel", "Login Firebase gagal", exception)
                    _uiState.value = AuthUiState.Error(
                        "Login Gagal: ${exception.localizedMessage ?: "Error tidak diketahui"}",
                        exception
                    )
                    // Opsional: kembali ke SignedOut setelah error agar UI bisa reset
                    // kotlinx.coroutines.delay(1000)
                    // _uiState.value = AuthUiState.SignedOut
                }
            }
        } else {
            // Handle jika pengguna membatalkan Google Sign-In atau terjadi error sebelum pemilihan akun
            Log.w("AuthViewModel", "Login Google dibatalkan atau gagal sebelum pemilihan akun. Kode Hasil: ${result.resultCode}")
            // Hanya update ke SignedOut jika state saat ini bukan Loading (untuk menghindari kedipan UI)
            if (_uiState.value !is AuthUiState.Loading) {
                _uiState.value = AuthUiState.SignedOut
            }
            // Atau tampilkan pesan error spesifik jika resultCode mengindikasikan error tertentu
            // _uiState.value = AuthUiState.Error("Login Google dibatalkan oleh pengguna.")
        }
    }

    /**
     * Melakukan proses logout pengguna.
     */
    fun signOut() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading // Set state ke loading
            val result = authRepository.signOut() // Panggil fungsi signOut di repository
            result.onSuccess {
                Log.i("AuthViewModel", "Logout berhasil.")
                _uiState.value = AuthUiState.SignedOut // Update state ke SignedOut
            }.onFailure { exception ->
                Log.e("AuthViewModel", "Logout gagal", exception)
                _uiState.value = AuthUiState.Error(
                    "Logout Gagal: ${exception.localizedMessage ?: "Error tidak diketahui"}",
                    exception
                )
                // Pertimbangkan untuk tetap mengarahkan ke SignedOut meskipun ada error di server
                // agar pengguna tidak terjebak di state yang salah.
                _uiState.value = AuthUiState.SignedOut
            }
        }
    }
}