package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ntz.distributor_app.ui.viewmodel.FirebaseRealtimeProducent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducenAddProductView(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Tambah Barang",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        ProducenViewAddProduct(innerPadding = innerPadding, navController = navController)
    }
}

@Composable
fun ProducenViewAddProduct(
    innerPadding : PaddingValues,
    navController: NavController,
    modifier: Modifier = Modifier,
    firebaseRealtimeProducent: FirebaseRealtimeProducent = viewModel()
){

    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .padding(innerPadding)
            .fillMaxWidth()
    ) {
        item {
            Text("Nama Barang", modifier = modifier.padding(start = 16.dp, top = 16.dp))
            TextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Nama Barang") },
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            Text("Kategori Barang", modifier = modifier.padding(start = 16.dp, top = 16.dp))
            TextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori") },
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            Text("Harga Barang", modifier = modifier.padding(start = 16.dp, top = 16.dp))
            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Harga") },
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            Text("Deskripsi Barang", modifier = modifier.padding(start = 16.dp, top = 16.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi") },
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
            )

            Button(
                onClick = { /* Handle button click */ },
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Upload Foto Barang")
            }

            Button(
                onClick = {
                    firebaseRealtimeProducent.setProduct(
                        stuffName = productName,
                        category = category,
                        price = price.toDouble(),
                        description = description,
                        photo = photoUrl
                    )
                },
                modifier = modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Text("Tambah Barang")
            }
        }
    }
}

// make preview function
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductAddProductPreview(){
    ProducenAddProductView(navController = NavController(LocalContext.current))
}