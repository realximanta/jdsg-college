package in.edu.jdsgcollege.bokakhat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import in.edu.jdsgcollege.bokakhat.ui.theme.JDSGCollegeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JDSGCollegeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Placeholder for the Navigation Graph
                    Text(
                        text = "JDSG College App Initialized",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
