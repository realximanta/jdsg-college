package in.edu.jdsgcollege.bokakhat.ui.screens

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import in.edu.jdsgcollege.bokakhat.BuildConfig
import in.edu.jdsgcollege.bokakhat.data.remote.AuthRequest
import in.edu.jdsgcollege.bokakhat.data.remote.RetrofitClient
import in.edu.jdsgcollege.bokakhat.data.remote.User
import in.edu.jdsgcollege.bokakhat.ui.theme.BluePrimary
import in.edu.jdsgcollege.bokakhat.ui.theme.TealAccent
import in.edu.jdsgcollege.bokakhat.ui.viewmodels.AuthViewModel
import in.edu.jdsgcollege.bokakhat.utils.ImageUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    isLogin: Boolean,
    onBackClick: () -> Unit,
    onAuthSuccess: (String, User) -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    
    // Auth State
    var isGoogleAuthenticated by remember { mutableStateOf(false) }
    var googleEmail by remember { mutableStateOf("") }
    var googleName by remember { mutableStateOf("") }
    var tempToken by remember { mutableStateOf("") }
    
    // Form State
    var selectedRole by remember { mutableStateOf("student") }
    var phone by remember { mutableStateOf("") }
    var classSelection by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var deptSelection by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    
    // Image State
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var compressedImageFile by remember { mutableStateOf<File?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            compressedImageFile = ImageUtils.compressImageToWebp(context, it)
            if (compressedImageFile == null) {
                Toast.makeText(context, "Failed to compress image", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                                    onAuthSuccess(body.token, body.user)
                                } else {
                                    googleEmail = body?.email ?: ""
                                    googleName = body?.name ?: ""
                                    tempToken = body?.token ?: ""
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                Text("Processing...", color = BluePrimary)
            } else if (!isGoogleAuthenticated) {
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
                            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
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

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = Color.White)
                    }
                }
                Text("Tap to add photo*", style = MaterialTheme.typography.labelMedium, color = TealAccent)
                Spacer(modifier = Modifier.height(16.dp))

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

                if (selectedRole == "student") {
                    OutlinedTextField(
                        value = classSelection, 
                        onValueChange = { classSelection = it }, 
                        label = { Text("Class (e.g., B.A., HS 1st Year)*") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = semester, 
                        onValueChange = { semester = it }, 
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
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = designation, 
                        onValueChange = { designation = it }, 
                        label = { Text("Designation*") }, 
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        if (phone.length != 10 || googleName.trim().isEmpty() || compressedImageFile == null) {
                            Toast.makeText(context, "Please fill all required fields & add a photo", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        isLoading = true
                        scope.launch {
                            try {
                                val newUser = User(
                                    id = "", email = googleEmail, full_name = googleName,
                                    role = selectedRole, phone = phone, profile_pic_url = "",
                                    class_name = if (selectedRole == "student") classSelection else null,
                                    semester = if (selectedRole == "student") semester else null,
                                    department = if (selectedRole == "teacher") deptSelection else null,
                                    designation = if (selectedRole == "teacher") designation else null
                                )
                                
                                val signupResponse = RetrofitClient.apiService.updateProfile("Bearer $tempToken", newUser)
                                if (!signupResponse.isSuccessful) {
                                    Toast.makeText(context, "Failed to create profile", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                val filePart = MultipartBody.Part.createFormData(
                                    "avatar", 
                                    compressedImageFile!!.name, 
                                    compressedImageFile!!.asRequestBody("image/webp".toMediaTypeOrNull())
                                )
                                val avatarResponse = RetrofitClient.apiService.uploadAvatar("Bearer $tempToken", filePart)
                                
                                if (avatarResponse.isSuccessful && avatarResponse.body() != null) {
                                    onAuthSuccess(tempToken, avatarResponse.body()!!)
                                } else {
                                    Toast.makeText(context, "Failed to upload avatar", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
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
