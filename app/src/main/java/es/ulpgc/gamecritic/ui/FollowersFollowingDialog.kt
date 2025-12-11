package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.ulpgc.gamecritic.viewmodel.UserSummary

private val LightSurface = Color(0xFFFFFFFF)
private val TextBlack = Color(0xFF111827)
private val TextDarkGray = Color(0xFF4B5563)
private val MyYellow = Color(0xFFF4D73E)

@Composable
fun FollowersFollowingDialog(
    title: String,
    users: List<UserSummary>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onUserClick: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LightSurface,
        title = {
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MyYellow, thickness = 2.dp)
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(color = MyYellow)
                    }
                    users.isEmpty() -> {
                        Text(
                            text = "No hay usuarios en esta lista.",
                            color = TextDarkGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(users) { user ->
                                UserListItem(user = user, onClick = { onUserClick(user.id) })
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = TextBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {}
    )
}

@Composable
fun UserListItem(
    user: UserSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                if (!user.icon.isNullOrBlank()) {
                    UserProfileImage(
                        imageData = user.icon,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextDarkGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = user.name ?: "Usuario",
                    color = TextBlack,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "@${user.username ?: "desconocido"}",
                    color = TextDarkGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}