package com.ntz.distributor_app.ui.screens

import android.app.Activity
import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.ntz.distributor_app.R
import com.ntz.distributor_app.ui.viewmodel.AuthViewModel
import com.ntz.distributor_app.ui.viewmodel.SignInState

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: (String) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val uiState by authViewModel.signInState.collectAsState()

    val googleSignInOption = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember{
        GoogleSignIn.getClient(context, googleSignInOption)
    }

    // Launcher untuk menerima hasil dari Google Sign In Intent
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if(result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try{
                    val account = task.getResult(ApiException::class.java)
                    account?.idToken?.let{ token ->
                        authViewModel.signInWithGoogle(token)
                    } ?: run{
                        Log.e("LoginScreen", "ID Token is null")
                    }
                } catch (e : ApiException){
                    Log.e("LoginScreen", "Google sign in failed", e)
                }
            } else if(result.resultCode == Activity.RESULT_CANCELED) {
                Log.w("LoginScreen", "Google sign in canceled by user")
            } else {
                Log.e("LoginScreen", "Unknown result code: ${result.resultCode}")
            }
        }
    )

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
                is SignInState.Idle -> {
                    Button(onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    }){
                        Text("Login Dengan Google")
                    }
                }
                is SignInState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Memproses...")
                }
                is SignInState.Success -> {
                    LaunchedEffect(Unit) {
                        onLoginSuccess(state.user.uid)
                    }
                }

                is SignInState.Error -> {
                    Text("Login Gagal, Silahkan ulangi kembali")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        authViewModel.resetState()
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    }) {
                        Text("Coba Lagi")
                    }
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