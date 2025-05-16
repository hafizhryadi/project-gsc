package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProducenViewData(
    navController: NavController
){
    var id by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // in here is a for data
    Column {
        Text("Nama Panjang")
        TextField(
            value = fullname,
            onValueChange = { fullname = it },
            label = { Text("Nama Panjang") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Nama Panggilan")
        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Nama Panggilan") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // for email, get from firebase
        Text("Email")
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Nomor Telepon")
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Nomor Telepon") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Negara")
            TextField(
                value = "Indonesia",
                onValueChange = { "Indonesia" },
                label = { Text("Negara") },
                readOnly = true
            )

            Text("Jenis Kelamin")
            TextField(
                value = gender,
                onValueChange = { gender = it },
                label = { Text("Jenis Kelamin") }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))


        Text("Alamat")
        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Alamat") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                /*TODO: Place at here to store data*/
                navController.navigate("PreferenceProducefindAgent")
            }
        ) {
            Text("Simpan")
        }

    }
}