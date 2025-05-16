package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ntz.distributor_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailProductView(navController: NavController){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        ProductDetail(navController = navController, innerPadding = innerPadding)
    }
}

@Composable
fun ProductDetail(navController: NavController, innerPadding : PaddingValues, modifier : Modifier = Modifier){
    LazyColumn(
        contentPadding = innerPadding,
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {
            Column(
                modifier = modifier.fillMaxWidth()
            ){
                Text(
                    "Nama Produk",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Spacer(modifier = modifier.height(16.dp))

                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Product Image"
                )

                Spacer(modifier = modifier.height(16.dp))

                Text("Rp: 180,000")

                Spacer(modifier = modifier.height(16.dp))

                Text("Deskripsi Produk")

                Spacer(modifier = modifier.height(16.dp))

                Text("Lorem ipsum dolor sit amet bla bla bla")



            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShowDetailProductViewPreview(){
    ShowDetailProductView(navController = NavController(LocalContext.current))
}