package in.edu.jdsgcollege.bokakhat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import in.edu.jdsgcollege.bokakhat.navigation.AppNavigation
import in.edu.jdsgcollege.bokakhat.ui.theme.JDSGCollegeTheme
import in.edu.jdsgcollege.bokakhat.ui.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JDSGCollegeTheme {
                val authViewModel: AuthViewModel = viewModel()
                val currentUser by authViewModel.currentUser.collectAsState()
                
                // Determine start destination based on active session
                val startDestination = if (currentUser != null) "dashboard" else "landing"

                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        authViewModel = authViewModel,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
