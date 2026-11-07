package in.edu.jdsgcollege.bokakhat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import in.edu.jdsgcollege.bokakhat.ui.theme.BluePrimary
import in.edu.jdsgcollege.bokakhat.ui.theme.TealAccent

data class NotificationItem(
    val id: String,
    val title: String,
    val date: String,
    val category: String
)

@Composable
fun NotificationsScreen() {
    // Mock data for UI testing
    val mockNotifications = listOf(
        NotificationItem("1", "Scholarship for B.A./B.Com. 1st Semester Students (FYUGP)", "2026-07-28", "Scholarship"),
        NotificationItem("2", "Notice regarding 2nd Internal Examination", "2026-07-25", "Exam"),
        NotificationItem("3", "Independence Day Celebration 2026", "2026-08-15", "Event")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineLarge,
            color = BluePrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockNotifications) { notification ->
                NotificationCard(notification)
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notification.category.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = TealAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = notification.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Read More",
                style = MaterialTheme.typography.bodyLarge,
                color = BluePrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
