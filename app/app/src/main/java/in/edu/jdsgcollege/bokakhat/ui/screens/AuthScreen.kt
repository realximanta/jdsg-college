package in.edu.jdsgcollege.bokakhat.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import in.edu.jdsgcollege.bokakhat.data.remote.AuthRequest
import in.edu.jdsgcollege.bokakhat.data.remote.RetrofitClient
import in.edu.jdsgcollege.bokakhat.data.remote.User
import in.edu.jdsgcollege.bokakhat.ui.theme.BluePrimary
import in.edu.jdsgcollege.bokakhat.ui.theme.TealAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    isLogin: Boolean,
    onBackClick: () -> Unit,
    onAuthSuccess: (String, User) -> Unit,
    webClientId: String = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com" // Must be replaced with actual Web Client ID
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    
    // Auth State
    var isGoogleAuthenticated by remember { mutableStateOf(false) }
    var googleEmail by remember { mutableStateOf("") }
    var googleName by remember { mutableStateOf("") }
    
    // Form State
    var selectedRole by remember { mutableStateOf("student") }
    var phone by remember { mutableStateOf("") }
    var classSelection by remember { mutableStateOf("") }
    var deptSelection by remember { mutableStateOf("") }

    // Google Sign-In Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    isLoading = true
                    scope.launch {
                        try {
                            val response = RetrofitClient.apiService.googleLogin(AuthRequest(idToken))
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body?.exists == true && body.token != null && body.user != null) {
                                    // User exists, log them in immediately
                                    onAuthSuccess(body.token, body.user)
                                } else {
                                    // User doesn't exist, show signup form
                                    googleEmail = body?.email ?: ""
                                    googleName = body?.name ?: ""
                                    isGoogleAuthenticated = true
                                }
                            } else {
                                Toast.makeText(context, "Auth failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            } catch (e: ApiException) {
                Log.e("GoogleAuth", "signInResult:failed code=" + e.statusCode)
                Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = BluePrimary)
                Spacer(Modifier.height(16.dp))
                Text("Connecting...", color = BluePrimary)
            } else if (!isGoogleAuthenticated) {
                // Initial Login / Signup Screen
                Spacer(modifier = Modifier.height(64.dp))
                Text(
                    text = if (isLogin) "Welcome Back" else "Create an Account",
                    style = MaterialTheme.typography.headlineLarge,
                    color = BluePrimary
                )
                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(webClientId)
                            .requestEmail()
                            .build()
                        val client = GoogleSignIn.getClient(context, gso)
                        launcher.launch(client.signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Continue with Google", fontWeight = FontWeight.Bold)
                }
            } else {
                // Signup Form (Shown after Google Auth if it's a new user)
                Text(
                    text = "Complete Your Profile",
                    style = MaterialTheme.typography.headlineLarge,
                    color = BluePrimary
                )
                Text(
                    text = googleEmail,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Role Selection
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = selectedRole == "student",
                        onClick = { selectedRole = "student" },
                        label = { Text("Student") }
                    )
                    FilterChip(
                        selected = selectedRole == "teacher",
                        onClick = { selectedRole = "teacher" },
                        label = { Text("Teacher") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Common Fields
                OutlinedTextField(
                    value = googleName, 
                    onValueChange = { googleName = it }, 
                    label = { Text("Full Name*") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone, 
                    onValueChange = { if (it.length <= 10) phone = it }, 
                    label = { Text("Phone No* (10 digits)") }, 
                    modifier = Modifier.fillMaxWidth(),
                    isError = phone.isNotEmpty() && phone.length < 10
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                // Role Specific Fields
                if (selectedRole == "student") {
                    OutlinedTextField(
                        value = classSelection, 
                        onValueChange = { classSelection = it }, 
                        label = { Text("Class (e.g., B.A., HS 1st Year)*") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "", // Semester logic here
                        onValueChange = {}, 
                        label = { Text("Semester (If B.A./B.Com)*") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = deptSelection, 
                        onValueChange = { deptSelection = it }, 
                        label = { Text("Department*") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        if (phone.length != 10) {
                            Toast.makeText(context, "Phone must be 10 digits", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // TODO: Call the backend /api/users endpoint with the form data
                        // For now, we simulate a successful signup to test UI
                        isLoading = true
                        scope.launch {
                            kotlinx.coroutines.delay(1000) // Mock network delay
                            isLoading = false
                            val mockUser = User(
                                id = "1", email = googleEmail, full_name = googleName,
                                role = selectedRole, phone = phone, profile_pic_url = ""
                            )
                            onAuthSuccess("mock_jwt_token", mockUser)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Sign Up", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
