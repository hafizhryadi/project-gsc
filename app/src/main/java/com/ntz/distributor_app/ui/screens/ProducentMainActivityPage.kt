package com.ntz.distributor_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ntz.distributor_app.R
import com.ntz.distributor_app.data.model.AgentData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProducentMainPageActivity(
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
                        "BuyNGo",
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
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate("ProducenAddProduct")
                },
                icon = { Icon(Icons.Filled.Add, "Extended floating action button.") },
                text = { Text(text = "Tambah Barang") },
                modifier = Modifier.padding(end = 16.dp)
            )
        }

    ) { innerPadding ->
        AgentViewRecommendation(navController = navController, innerPadding = innerPadding)
    }
}

@Composable
fun AgentViewRecommendation(navController: NavController, modifier: Modifier = Modifier, innerPadding : PaddingValues){

    // make dummy data from AgentData Data class on list, in dataclass have field id, fullname, nickname, email, phoneNumber, country, gender, address
    val exampleList : List<AgentData> = listOf(
        AgentData("1", "John Doe", "johndoe", "john@example.com", "123-456-7890", "USA", "Male", "123 Main St", "New York", "New York"),
        AgentData("2", "Jane Smith", "janesmith", "jane@example.com", "098-765-4321", "Canada", "Female", "456 Oak Ave", "Toronto", "Ontario"),
        AgentData("3", "Peter Jones", "peterj", "peter@example.com", "111-222-3333", "UK", "Male", "789 Pine Ln", "London", "Greater London"),
        AgentData("4", "Mary Brown", "maryb", "mary@example.com", "444-555-6666", "Australia", "Female", "101 Maple Rd", "Sydney", "New South Wales"),
        AgentData("5", "Michael Davis", "michaeld", "michael@example.com", "777-888-9999", "Germany", "Male", "202 Elm St", "Berlin", "Berlin"),
        AgentData("6", "Sarah Wilson", "sarahw", "sarah@example.com", "000-111-2222", "France", "Female", "303 Birch Ave", "Paris", "Île-de-France"),
        AgentData("7", "David White", "davidw", "david@example.com", "333-444-5555", "Japan", "Male", "404 Cedar Ln", "Tokyo", "Tokyo"),
        AgentData("8", "Jessica Lee", "jessical", "jessica@example.com", "666-777-8888", "China", "Female", "505 Spruce Rd", "Shanghai", "Shanghai")
    )

    Column(
        modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
    ){
        Text(
            text = "Rekomendasi Agen",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            ),
            modifier = modifier.padding(16.dp)
        )

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = modifier.padding(top = 16.dp))

        Row(
            modifier = modifier.padding(start = 16.dp, end = 10.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Location Icon"
            )

            Text(
                text = "Lokasi",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = modifier.padding(start = 8.dp)
            )
        }

        LazyRow(
            modifier = modifier.padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(exampleList.size) {
                Card{
                    Row(
                        modifier = modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = "Image",
                        )

                        Column {
                            Text(
                                text = exampleList[it].fullname,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )

                            Text(
                                text = exampleList[it].address,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )

                            Text(
                                text = exampleList[it].phoneNumber,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = modifier.padding(top = 16.dp))

        Column(
            modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = "Barang Producen",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = modifier.padding(start = 8.dp)
            )

            Spacer(modifier = modifier.padding(top = 16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(exampleList.size) {
                    Card{
                        Row(
                            modifier = modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_launcher_foreground),
                                contentDescription = "Image",
                            )

                            Column {
                                Text(
                                    text = exampleList[it].fullname,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )

                                Text(
                                    text = exampleList[it].address,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )

                                Text(
                                    text = exampleList[it].phoneNumber,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// make preview function
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AgentViewRecommendationPreview(){
    ProducentMainPageActivity(navController = NavController(LocalContext.current))
}