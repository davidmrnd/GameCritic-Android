package es.ulpgc.gamecritic.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.gamecritic.viewmodel.UserSummary
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight

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
        title = { Text(text = title, fontWeight = FontWeight.Bold, color = Color.Black) },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                when {
                    isLoading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    users.isEmpty() -> {
                        Text(text = "No hay usuarios", color = Color.Gray)
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 400.dp)
                        ) {
                            items(users) { user ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable { onUserClick(user.id) },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color.LightGray,
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            if (!user.icon.isNullOrBlank()) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(user.icon),
                                                    contentDescription = user.username,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = user.name ?: "Usuario", color = Color.Black, fontWeight = FontWeight.Bold)
                                            Text(text = "@${user.username ?: ""}", color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

