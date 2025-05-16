package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ntz.distributor_app.ui.viewmodel.AuthViewModel

@Composable
fun Modifier.UserViewDecision(
    authViewModel: AuthViewModel = viewModel(),
    navController: NavController
){
    var userDecision by remember { mutableStateOf("") }

    Column(
        modifier = fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pilih salah satu peran anda")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                userDecision = "Producen"
                authViewModel.updateRole("produsen")
                navController.navigate("ProducenRegister")
            }
        ) {
            Text("Produsen")
        }

        Button(
            onClick = {
                userDecision = "Agent"
                authViewModel.updateRole("agent")
                navController.navigate("AgentRegister")

            }
        ) {
            Text("Agen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                AuthViewModel().signOut(
                    onSuccess = {
                        navController.navigate("Login")
                    }
                )
            }
        ) {
            Text("Sign Out, Test")
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserViewDecisionPreview() {
    Modifier.UserViewDecision(
        navController = NavController(LocalContext.current)
    )
}