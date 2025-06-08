package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import api.GardenApi
import api.UserApi
import kotlinx.coroutines.launch
import models.Garden
import models.Tile
import models.User

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddGarden(onGardenAdded: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var ownerId by remember { mutableStateOf<String?>(null) }
    var ownerName by remember { mutableStateOf("Select Owner") }
    var ownerExpanded by remember { mutableStateOf(false) }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }

    var widthText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitudeText by remember { mutableStateOf("") }
    var longitudeText by remember { mutableStateOf("") }

    var elements by remember { mutableStateOf<List<Tile>>(emptyList()) }
    var showAddTileDialog by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingUsers by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        isLoadingUsers = true
        try {
            users = UserApi.getUsers()
        } catch (e: Exception) {
            error = "Failed to load users: ${e.message}"
        } finally {
            isLoadingUsers = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Add New Garden",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (success != null) {
            Text(
                text = success!!,
                color = Color.Green,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Garden Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Yard, contentDescription = "Garden Name") }
        )

        ExposedDropdownMenuBox(
            expanded = ownerExpanded,
            onExpandedChange = { ownerExpanded = !ownerExpanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = ownerName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Owner") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Owner") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ownerExpanded) },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = ownerExpanded,
                onDismissRequest = { ownerExpanded = false }
            ) {
                if (isLoadingUsers) {
                    DropdownMenuItem(onClick = {}) {
                        Text("Loading users...")
                    }
                } else {
                    users.forEach { user ->
                        DropdownMenuItem(
                            onClick = {
                                ownerId = user._id
                                ownerName = user.username
                                ownerExpanded = false
                            }
                        ) {
                            Text(user.username)
                        }
                    }
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = widthText,
                onValueChange = { if (it.all { char -> char.isDigit() }) widthText = it },
                label = { Text("Width (units)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Width") }
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { if (it.all { char -> char.isDigit() }) heightText = it },
                label = { Text("Height (units)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.SwapVert, contentDescription = "Height") }
            )
        }

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location (Optional)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = "Location") }
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = latitudeText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue == "-" || newValue == "." || newValue == "-." || newValue.toDoubleOrNull() != null) {
                        latitudeText = newValue
                    }
                },
                label = { Text("Latitude (Optional)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.Public, contentDescription = "Latitude") }
            )
            OutlinedTextField(
                value = longitudeText,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue == "-" || newValue == "." || newValue == "-." || newValue.toDoubleOrNull() != null) {
                        longitudeText = newValue
                    }
                },
                label = { Text("Longitude (Optional)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.Public, contentDescription = "Longitude") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Garden Elements (Tiles)", style = MaterialTheme.typography.h6)
        elements.forEach { tile ->
            Text("- Tile at (${tile.x}, ${tile.y}), Type: ${tile.type}", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
        }
        Button(
            onClick = { showAddTileDialog = true },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(Icons.Filled.AddLocationAlt, contentDescription = "Add Tile")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Tile Element")
        }
        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                val width = widthText.toIntOrNull()
                val height = heightText.toIntOrNull()

                if (name.isBlank() || ownerId == null || width == null || height == null) {
                    error = "Name, Owner, Width, and Height are required."
                    success = null
                    return@Button
                }
                if (width <= 0 || height <= 0) {
                    error = "Width and Height must be positive numbers."
                    success = null
                    return@Button
                }

                val latitude = if (latitudeText.isNotBlank()) latitudeText.toDoubleOrNull() else null
                if (latitudeText.isNotBlank() && latitude == null) {
                    error = "Invalid Latitude format."
                    success = null
                    return@Button
                }
                val longitude = if (longitudeText.isNotBlank()) longitudeText.toDoubleOrNull() else null
                if (longitudeText.isNotBlank() && longitude == null) {
                    error = "Invalid Longitude format."
                    success = null
                    return@Button
                }

                error = null
                success = null
                isLoading = true

                val newGarden = Garden(
                    _id = "",
                    name = name.trim(),
                    owner = ownerId!!, // Use ownerId
                    width = width,
                    height = height,
                    location = location.trim().ifBlank { null },
                    latitude = latitude,
                    longitude = longitude,
                    elements = elements, // Include added elements
                    createdAt = null,
                    updatedAt = null
                )

                coroutineScope.launch {
                    try {
                        val createdGarden = GardenApi.createGarden(newGarden)
                        success = "Garden '${createdGarden.name}' created successfully!"
                        name = ""
                        ownerId = null
                        ownerName = "Select Owner"
                        widthText = ""
                        heightText = ""
                        location = ""
                        latitudeText = ""
                        longitudeText = ""
                        elements = emptyList() // Clear elements
                        error = null
                        onGardenAdded()
                    } catch (e: Exception) {
                        error = "Error creating garden: ${e.message ?: "Unknown error"}"
                        success = null
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Add Garden", color = Color.White)
            }
        }
    }

    if (showAddTileDialog) {
        AddTileDialog(
            onDismiss = { showAddTileDialog = false },
            onAddTile = { tile ->
                elements = elements + tile // Add new tile to the list
                showAddTileDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddTileDialog(onDismiss: () -> Unit, onAddTile: (Tile) -> Unit) {
    var tileX by remember { mutableStateOf("") }
    var tileY by remember { mutableStateOf("") }
    val tileTypes = listOf("Greda", "Visoka greda", "Potka") // Available tile types
    var selectedTileType by remember { mutableStateOf(tileTypes[0]) }
    var typeExpanded by remember { mutableStateOf(false) }
    var tileError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Tile Element") },
        text = {
            Column {
                if (tileError != null) {
                    Text(tileError!!, color = Color.Red, style = MaterialTheme.typography.caption)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                OutlinedTextField(
                    value = tileX,
                    onValueChange = { if (it.all { char -> char.isDigit() }) tileX = it },
                    label = { Text("Tile X Coordinate") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tileY,
                    onValueChange = { if (it.all { char -> char.isDigit() }) tileY = it },
                    label = { Text("Tile Y Coordinate") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTileType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tile Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        tileTypes.forEach { type ->
                            DropdownMenuItem(onClick = {
                                selectedTileType = type
                                typeExpanded = false
                            }) {
                                Text(type)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val x = tileX.toIntOrNull()
                val y = tileY.toIntOrNull()
                if (x == null || y == null) {
                    tileError = "X and Y coordinates must be valid numbers."
                    return@Button
                }
                if (x < 0 || y < 0) {
                    tileError = "Coordinates cannot be negative."
                    return@Button
                }
                tileError = null
                onAddTile(Tile(x = x, y = y, type = selectedTileType, _id = null))
            }) {
                Text("Add Tile")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}