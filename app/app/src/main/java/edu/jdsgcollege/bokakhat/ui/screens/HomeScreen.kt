package edu.jdsgcollege.bokakhat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.jdsgcollege.bokakhat.ui.theme.TealAccent
import edu.jdsgcollege.bokakhat.ui.viewmodels.AuthViewModel

@Composable
fun HomeScreen(authViewModel: AuthViewModel) {
    val currentUser by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Welcome,",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = currentUser?.full_name ?: "Student",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // Placeholder Card 1: Routine
        ActionCard(
            title = "Class Routine",
            subtitle = "Check your daily schedule",
            icon = Icons.Default.Schedule,
            onClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Placeholder Card 2: Room Locator
        ActionCard(
            title = "Find a Room",
            subtitle = "Locate classrooms instantly",
            icon = Icons.Default.LocationOn,
            onClick = { /* TODO */ }
        )
    }
}

@Composable
fun ActionCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TealAccent,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
