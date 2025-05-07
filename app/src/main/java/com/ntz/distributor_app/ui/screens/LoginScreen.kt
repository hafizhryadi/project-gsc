package com.ntz.distributor_app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.SignInButton // Tombol Google Sign In Bawaan
import com.ntz.distributor_app.R
import com.ntz.distributor_app.ui.viewmodel.AuthUiState
import com.ntz.distributor_app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit // Callback setelah login berhasil
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()

    // Launcher untuk menerima hasil dari Google Sign In Intent
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            // Kirim hasil ke ViewModel untuk diproses
            authViewModel.handleSignInResult(result)
        }
    )

    // Efek samping untuk memanggil onLoginSuccess saat state berubah jadi SignedIn
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.SignedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("Selamat Datang!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            when (val state = uiState) {
                is AuthUiState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Memproses...")
                }
                is AuthUiState.SignedOut, is AuthUiState.Error -> {
                    // Gunakan Tombol Google Sign In bawaan atau custom
                    // Button(onClick = { ... }) // Tombol Custom

                    // Tombol Google Sign-In Bawaan (perlu view binding atau AndroidView)
                    /*
                    AndroidView(factory = { ctx ->
                        SignInButton(ctx).apply {
                            setSize(SignInButton.SIZE_WIDE)
                            setOnClickListener {
                                val signInIntent = authViewModel.getSignInIntent()
                                googleSignInLauncher.launch(signInIntent)
                            }
                        }
                    })
                    */

                    // Tombol Material 3 biasa untuk memicu login
                    Button(
                        onClick = {
                            val signInIntent = authViewModel.getSignInIntent()
                            googleSignInLauncher.launch(signInIntent)
                        },
                        enabled = state !is AuthUiState.Loading // Nonaktifkan saat loading
                    ) {
                        // Anda bisa menambahkan ikon Google di sini
                        Text("Login dengan Google")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (state is AuthUiState.Error) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is AuthUiState.SignedIn -> {
                    // Seharusnya tidak tampil di sini karena ada navigasi otomatis
                    Text("Login Berhasil!")
                    // CircularProgressIndicator() // Tampilkan loading saat navigasi
                }
            }
        }
    }
}