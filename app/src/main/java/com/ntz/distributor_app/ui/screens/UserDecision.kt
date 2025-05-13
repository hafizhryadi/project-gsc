package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun UserViewDecision(
    modifier: Modifier = Modifier,
    navController : NavController
){
    var userDecision by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Pilih salah satu peran anda")
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                userDecision = "Producen"
                navController.navigate("ProducenRegister")
                // update userType at here
            }
        ) {
            Text("Produsen")
        }

        Button(
            onClick = {
                userDecision = "Agent"
                navController.navigate("AgentRegister")
                // update userType at here
            }
        ) {
            Text("Agen")
        }

    }
}