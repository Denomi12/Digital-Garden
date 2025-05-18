package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import api.UserApi
import models.User

@Composable
fun PeopleTab() {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var expandedUserId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        users = UserApi.getUsers()
        isLoading = false
    }

    if (isLoading) {
        Text("Nalagam podatke...", modifier = Modifier.padding(16.dp))
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 250.dp),
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                var username by remember { mutableStateOf(user.username) }
                var email by remember { mutableStateOf(user.email) }
                val isExpanded = expandedUserId == user._id

                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedUserId = if (isExpanded) null else user._id
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = user.username,
                                style = MaterialTheme.typography.h6
                            )
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))

                            TextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Username") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val updatedUser = user.copy(username = username, email = email)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val success = UserApi.updateUser(updatedUser)
                                        if (success) {
                                            users = UserApi.getUsers()
                                            expandedUserId = null
                                        } else {
                                            println("Napaka pri shranjevanju!")
                                        }
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Shrani")
                            }
                        }
                    }
                }
            }
        }
    }
}
