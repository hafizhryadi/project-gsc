package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ntz.distributor_app.R
import com.ntz.distributor_app.ui.viewmodel.FirebaseRealtimeAgent
import com.ntz.distributor_app.ui.viewmodel.FirebaseRealtimeProducent
import com.ntz.distributor_app.ui.viewmodel.GeminiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentMainActivityView(navController: NavController){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "BuyNGo") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Account")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        ShowProductForAgent(innerPadding, navController = navController)
    }
}

@Composable
fun ShowProductForAgent(
    innerPadding : PaddingValues,
    modifier : Modifier = Modifier,
    navController: NavController,
    viewModelProducen: FirebaseRealtimeProducent = viewModel(),
    geminiViewModel: GeminiViewModel = viewModel()
){

    val userProducenData by viewModelProducen.producentDataList.collectAsState()
    viewModelProducen.getAllProducen()

    // gemini recommendation
    val geminiRecommendation by geminiViewModel.geminiResponse.collectAsState()
    // example tested
    geminiViewModel.generateRecommendationProducen(
        city = "Bogor",
        regency = "Kebun raya bogor",
        roleToSearch = "Produsens",
        listData = userProducenData
    )

    LazyColumn(
        contentPadding = innerPadding,
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(10){
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Row(
                    modifier = modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Producent Profile"
                    )

                    Column(
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Cv Lorem Ipsum",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        )

                        Spacer(modifier = modifier.height(8.dp))

                        Text("Palembang")

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AgentMainActivityViewPreview(){
    AgentMainActivityView(navController = NavController(LocalContext.current))
}