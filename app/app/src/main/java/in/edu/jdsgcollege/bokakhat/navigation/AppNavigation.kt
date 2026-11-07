package in.edu.jdsgcollege.bokakhat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import in.edu.jdsgcollege.bokakhat.ui.screens.AboutScreen
import in.edu.jdsgcollege.bokakhat.ui.screens.AuthScreen
import in.edu.jdsgcollege.bokakhat.ui.screens.DashboardScreen
import in.edu.jdsgcollege.bokakhat.ui.screens.LandingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {
        composable("landing") {
            LandingScreen(
                onMenuClick = { navController.navigate("about") },
                onSignUpClick = { navController.navigate("signup") },
                onLoginClick = { navController.navigate("login") }
            )
        }
        
        composable("about") {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable("login") {
            AuthScreen(
                isLogin = true,
                onBackClick = { navController.popBackStack() },
                onAuthSuccess = { 
                    navController.navigate("dashboard") { 
                        popUpTo("landing") { inclusive = true } 
                    } 
                }
            )
        }
        
        composable("signup") {
            AuthScreen(
                isLogin = false,
                onBackClick = { navController.popBackStack() },
                onAuthSuccess = { 
                    navController.navigate("dashboard") { 
                        popUpTo("landing") { inclusive = true } 
                    } 
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen() // Handles internal bottom nav
        }
    }
}
