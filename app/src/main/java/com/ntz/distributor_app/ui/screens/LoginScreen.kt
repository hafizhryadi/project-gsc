package com.ntz.distributor_app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.common.SignInButton // Tombol Google Sign In Bawaan
import com.ntz.distributor_app.R
import com.ntz.distributor_app.ui.navigation.AppNavigation
import com.ntz.distributor_app.ui.viewmodel.AuthUiState
import com.ntz.distributor_app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit, // Callback setelah login berhasil
    navController: NavController
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
        Image(
            painter = painterResource(R.drawable.loginimagebackground),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .height(500.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.Bottom)
                .padding(20.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "BuyNGO",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
            )


            when (val state = uiState) {
                is AuthUiState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Memproses...")
                }
                is AuthUiState.SignedOut, is AuthUiState.Error -> {
                    Button(
                        onClick = {
                            val signInIntent = authViewModel.getSignInIntent()
                            googleSignInLauncher.launch(signInIntent)
                        },
                        enabled = state !is AuthUiState.Loading, // Nonaktifkan saat loading
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
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
                    navController.navigate("UserDecision")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview(){
    LoginScreen(
        onLoginSuccess = {},
        navController = NavController(LocalContext.current)
    )
}