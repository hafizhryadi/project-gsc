package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ntz.distributor_app.ui.viewmodel.FirebaseRealtimeProducent

@Composable
fun ProducenViewData(
    navController: NavController,
    firebaseRealtimeProducent: FirebaseRealtimeProducent = viewModel()
){
    var id by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid ?: "") }
    var fullname by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.email ?: "") }
    var phoneNumber by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("Indonesia") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var regency by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Nama Panjang")
            TextField(
                value = fullname,
                onValueChange = { fullname = it },
                label = { Text("Nama Panjang") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Nama Panggilan")
            TextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nama Panggilan") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // for email, get from firebase
            Text("Email")
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Nomor Telepon")
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Nomor Telepon") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Negara")
                    TextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Negara") },
                        readOnly = true
                    )
                }

                Spacer(modifier = Modifier.padding(start = 10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Jenis Kelamin")
                    TextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Jenis Kelamin") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Alamat")
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Kota")
            TextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Kota") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Kabupaten")
            TextField(
                value = regency,
                onValueChange = { regency = it },
                label = { Text("Kabupaten") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    /*TODO: Place at here to store data*/
                    firebaseRealtimeProducent.setProducentData(
                        id = id,
                        fullname = fullname,
                        nickname = nickname,
                        email = email,
                        phoneNumber = phoneNumber,
                        country = country,
                        gender = gender,
                        address = address,
                        city = city,
                        regency = regency
                    )
                    navController.navigate("PreferenceProducefindAgent")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Simpan")
            }
        }
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun ProducenRegisterPreview(){
    ProducenViewData(navController = NavController(LocalContext.current))
}