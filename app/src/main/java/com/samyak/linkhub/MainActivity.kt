package com.samyak.linkhub

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samyak.linkhub.ui.LinkViewModel
import com.samyak.linkhub.ui.screens.HomeScreen
import com.samyak.linkhub.ui.screens.WebViewScreen
import com.samyak.linkhub.ui.theme.LinkHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: LinkViewModel = viewModel()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onLinkClick = { link ->
                                    val encodedUrl = Uri.encode(link.url)
                                    val encodedTitle = Uri.encode(link.title)
                                    navController.navigate("webview/$encodedUrl/$encodedTitle")
                                }
                            )
                        }
                        composable(
                            route = "webview/{url}/{title}",
                            arguments = listOf(
                                navArgument("url") { type = NavType.StringType },
                                navArgument("title") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url")?.let { 
                                Uri.decode(it) 
                            } ?: ""
                            val title = backStackEntry.arguments?.getString("title")?.let { 
                                Uri.decode(it) 
                            } ?: ""
                            WebViewScreen(
                                url = url,
                                title = title,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
