package in.edu.jdsgcollege.bokakhat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import in.edu.jdsgcollege.bokakhat.ui.screens.AboutScreen
import in.edu.jdsgcollege.bokakhat.ui.screens.AuthScreen
import in.edu.jdsgcollege.bokakhat.ui.screens.DashboardScreen
import in.edu.jdsgcollege.bokakhat.ui.screens.LandingScreen
import in.edu.jdsgcollege.bokakhat.ui.screens.UpdateProfileScreen
import in.edu.jdsgcollege.bokakhat.ui.viewmodels.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("landing") {
            LandingScreen(
                onMenuClick = { navController.navigate("about") },
                onSignUpClick = { navController.navigate("signup") },
                onLoginClick = { navController.navigate("login") }
            )
        }
        
        composable("about") {
            AboutScreen(onBackClick = { navController.popBackStack() })
        }
        
        composable("login") {
            AuthScreen(
                isLogin = true,
                onBackClick = { navController.popBackStack() },
                onAuthSuccess = { token, user -> 
                    authViewModel.updateSession(token, user)
                    navController.navigate("dashboard") { 
                        popUpTo("landing") { inclusive = true } 
                    } 
                },
                authViewModel = authViewModel
            )
        }
        
        composable("signup") {
            AuthScreen(
                isLogin = false,
                onBackClick = { navController.popBackStack() },
                onAuthSuccess = { token, user -> 
                    authViewModel.updateSession(token, user)
                    navController.navigate("dashboard") { 
                        popUpTo("landing") { inclusive = true } 
                    } 
                },
                authViewModel = authViewModel
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                authViewModel = authViewModel,
                onNavigateToUpdateProfile = { navController.navigate("update_profile") }
            ) 
        }

        composable("update_profile") {
            UpdateProfileScreen(
                authViewModel = authViewModel,
                onBackClick = { navController.popBackStack() },
                onUpdateSuccess = { navController.popBackStack() }
            )
        }
    }
}
