package in.edu.jdsgcollege.bokakhat.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in.edu.jdsgcollege.bokakhat.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    isLogin: Boolean,
    onBackClick: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    // Local UI State
    var isGoogleAuthenticated by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Student") } // "Student" or "Teacher"

    Scaffold(
        topBar = {
            if (!isGoogleAuthenticated) {
                // Only show back button before Google Auth
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BluePrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isGoogleAuthenticated) {
                // Initial Login / Signup Screen
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = if (isLogin) "Welcome Back" else "Create an Account",
                    style = MaterialTheme.typography.headlineLarge,
                    color = BluePrimary
                )
                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = { isGoogleAuthenticated = true }, // Simulating Google Auth
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    // Note: Add Google Logo drawable later
                    Text("Continue with Google", fontWeight = FontWeight.Bold)
                }

            } else {
                // Signup Details Form (shown after Google Auth if it's a signup)
                Text(
                    text = "Complete Your Profile",
                    style = MaterialTheme.typography.headlineLarge,
                    color = BluePrimary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Role Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = selectedRole == "Student",
                        onClick = { selectedRole = "Student" },
                        label = { Text("Student") }
                    )
                    FilterChip(
                        selected = selectedRole == "Teacher",
                        onClick = { selectedRole = "Teacher" },
                        label = { Text("Teacher") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Common Fields
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Full Name*") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Phone No*") }, modifier = Modifier.fillMaxWidth())
                
                // Role Specific Fields
                if (selectedRole == "Student") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Class*") }, modifier = Modifier.fillMaxWidth())
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Department*") }, modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onAuthSuccess,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Sign Up")
                }
            }
        }
    }
}
