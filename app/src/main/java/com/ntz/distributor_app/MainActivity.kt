package com.ntz.distributor_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntz.distributor_app.ui.screens.AgentMainActivityView
import com.ntz.distributor_app.ui.screens.AgentViewData
import com.ntz.distributor_app.ui.screens.LoginScreen
import com.ntz.distributor_app.ui.screens.ProducenViewData
import com.ntz.distributor_app.ui.screens.UserViewDecision
import com.ntz.distributor_app.ui.theme.Distributor_appTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Distributor_appTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "Login"
                    ){
                        composable("Login"){
                            LoginScreen(
                                onLoginSuccess = {
                                    Toast.makeText(this@MainActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                                    navController.navigate("UserDecision")
                                },
                                navController = navController
                            )
                        }

                        composable("UserDecision"){
                            Modifier.UserViewDecision(
                                navController = navController
                            )
                        }

                        composable("ProducenRegister"){
                            ProducenViewData(
                                navController = navController
                            )
                        }

                        composable("AgentRegister"){
                            AgentViewData(
                                navController = navController
                            )
                        }

                        composable("PreferenceProducefindAgent"){
                            AgentMainActivityView(
                                navController = navController
                            )
                        }

                    }
                    /*BuyerRecommendation(
                        onNavigateToPreferences = {
                            Toast.makeText(this, "Buka preferensi", Toast.LENGTH_SHORT).show()
                        },
                        onLogout = {
                            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                        }
                    )*/

                    /*LoginScreen(
                        onLoginSuccess = {
                            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                        },
                        navController = rememberNavController()
                    )*/

                }
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Distributor_appTheme {
//        Greeting("Android")
//    }
//}