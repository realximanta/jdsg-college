package in.edu.jdsgcollege.bokakhat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in.edu.jdsgcollege.bokakhat.ui.theme.BluePrimary
import in.edu.jdsgcollege.bokakhat.ui.theme.TealAccent

@Composable
fun AccountScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture Header
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder icon until Coil is wired up
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Details
        Text(
            text = "Full Name", // Placeholder
            style = MaterialTheme.typography.headlineLarge,
            color = BluePrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // Role Badge
        Surface(
            color = TealAccent.copy(alpha = 0.1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "STUDENT", // Placeholder
                style = MaterialTheme.typography.labelMedium,
                color = TealAccent,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Details List
        ProfileDetailRow(label = "Class", value = "B.A. 1st Semester") // Placeholder
        ProfileDetailRow(label = "Phone", value = "9876543210") // Placeholder

        Spacer(modifier = Modifier.height(32.dp))

        // Update Profile Button
        Button(
            onClick = { /* TODO: Navigate to Edit Profile */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
            Text("Update Profile", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}
