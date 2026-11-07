package in.edu.jdsgcollege.bokakhat.ui.screens

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
import in.edu.jdsgcollege.bokakhat.data.remote.RetrofitClient
import in.edu.jdsgcollege.bokakhat.data.remote.User
import in.edu.jdsgcollege.bokakhat.ui.theme.BluePrimary
import in.edu.jdsgcollege.bokakhat.ui.theme.TealAccent
import in.edu.jdsgcollege.bokakhat.ui.viewmodels.AuthViewModel
import in.edu.jdsgcollege.bokakhat.utils.ImageUtils
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onUpdateSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    var isLoading by remember { mutableStateOf(false) }
    
    // Pre-fill form state
    var fullName by remember { mutableStateOf(currentUser?.full_name ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var selectedRole by remember { mutableStateOf(currentUser?.role ?: "student") }
    var classSelection by remember { mutableStateOf(currentUser?.class_name ?: "") }
    var semester by remember { mutableStateOf(currentUser?.semester ?: "") }
    var deptSelection by remember { mutableStateOf(currentUser?.department ?: "") }
    var designation by remember { mutableStateOf(currentUser?.designation ?: "") }
    
    // Image State
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var compressedImageFile by remember { mutableStateOf<File?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            compressedImageFile = ImageUtils.compressImageToWebp(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Profile", color = BluePrimary) },
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
            // Profile Picture Picker
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
                        contentDescription = "New Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!currentUser?.profile_pic_url.isNullOrEmpty()) {
                    AsyncImage(
                        model = currentUser?.profile_pic_url,
                        contentDescription = "Current Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = Color.White)
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Edit", tint = Color.White)
                }
            }
            Text("Tap to change photo", style = MaterialTheme.typography.labelMedium, color = TealAccent)
            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name*") },
                modifier = Modifier.fillMaxWidth(),
                isError = fullName.isEmpty()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it },
                label = { Text("Phone No*") },
                modifier = Modifier.fillMaxWidth(),
                isError = phone.length != 10
            )
            
            Spacer(modifier = Modifier.height(16.dp))

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

            if (selectedRole == "student") {
                OutlinedTextField(
                    value = classSelection,
                    onValueChange = { classSelection = it },
                    label = { Text("Class*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = classSelection.isEmpty()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = semester,
                    onValueChange = { semester = it },
                    label = { Text("Semester*") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = deptSelection,
                    onValueChange = { deptSelection = it },
                    label = { Text("Department*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = deptSelection.isEmpty()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("Designation*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = designation.isEmpty()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (fullName.isEmpty() || phone.length != 10) {
                        Toast.makeText(context, "Please fill all required fields correctly", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    val token = authViewModel.getToken()
                    if (token == null) {
                        Toast.makeText(context, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            // 1. Prepare updated user object
                            val updatedUser = User(
                                id = currentUser?.id ?: "",
                                email = currentUser?.email ?: "",
                                full_name = fullName,
                                role = selectedRole,
                                class_name = if (selectedRole == "student") classSelection else null,
                                semester = if (selectedRole == "student") semester else null,
                                roll_no = currentUser?.roll_no,
                                emergency_contact = currentUser?.emergency_contact,
                                department = if (selectedRole == "teacher") deptSelection else null,
                                designation = if (selectedRole == "teacher") designation else null,
                                phone = phone,
                                profile_pic_url = currentUser?.profile_pic_url ?: ""
                            )

                            // 2. Call API
                            val response = RetrofitClient.apiService.updateProfile("Bearer $token", updatedUser)
                            
                            if (response.isSuccessful && response.body() != null) {
                                // 3. Update session and trigger success callback
                                authViewModel.updateSession(token, response.body()!!)
                                Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                                onUpdateSuccess()
                            } else {
                                Toast.makeText(context, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("UpdateProfile", "Error", e)
                            Toast.makeText(context, "Network error occurred", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
