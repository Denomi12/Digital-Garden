package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import api.GardenApi
import api.CropApi
import kotlinx.coroutines.launch
import models.Garden
import models.Tile
import models.Crop

import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun formatDateForDisplay(isoDate: String?): String {
    if (isoDate.isNullOrEmpty()) return ""
    return try {
        val temporalAccessor = DateTimeFormatter.ISO_DATE_TIME.parse(isoDate)
        val localDate = LocalDate.from(temporalAccessor)
        localDate.format(DateTimeFormatter.ofPattern("dd. MM. yyyy"))
    } catch (e: DateTimeParseException) {
        try {
            val localDate = LocalDate.parse(isoDate, DateTimeFormatter.ISO_LOCAL_DATE)
            localDate.format(DateTimeFormatter.ofPattern("dd. MM. yyyy"))
        } catch (e2: DateTimeParseException) {
            isoDate
        }
    }
}

fun localDateToIsoDateTimeString(date: LocalDate): String {
    return date.atStartOfDay().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
}

@Composable
fun GardensTab() {
    var gardens by remember { mutableStateOf<List<Garden>>(emptyList()) }
    var crops by remember { mutableStateOf<List<Crop>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var expandedGardenId by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadGardensAndCrops() {
        coroutineScope.launch {
            isLoading = true
            error = null
            showSuccess = false
            try {
                gardens = GardenApi.getGardens()
                crops = CropApi.getCrops()
            } catch (e: Exception) {
                error = "Error loading data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadGardensAndCrops()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gardens", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        if (showSuccess) {
            Text("Garden updated successfully!", color = Color.Green)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (error != null) {
            Text(error!!, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { loadGardensAndCrops() }) {
                Text("Retry")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gardens) { garden ->
                    GardenCard(
                        garden = garden,
                        allCrops = crops,
                        isExpanded = expandedGardenId == garden._id,
                        onClick = {
                            expandedGardenId = if (expandedGardenId == garden._id) null else garden._id
                        },
                        onSave = { updatedGarden ->
                            coroutineScope.launch {
                                isLoading = true
                                error = null
                                showSuccess = false
                                try {
                                    if (GardenApi.updateGarden(updatedGarden)) {
                                        showSuccess = true
                                        loadGardensAndCrops() // Reload all data after save
                                    }
                                } catch (e: Exception) {
                                    error = "Error saving garden: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GardenCard(
    garden: Garden,
    allCrops: List<Crop>,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onSave: (Garden) -> Unit
) {
    var name by remember(garden.name) { mutableStateOf(garden.name) }
    var width by remember(garden.width) { mutableStateOf(garden.width.toString()) }
    var height by remember(garden.height) { mutableStateOf(garden.height.toString()) }
    var location by remember(garden.location) { mutableStateOf(garden.location ?: "") }
    var latitude by remember(garden.latitude) { mutableStateOf(garden.latitude?.toString() ?: "") }
    var longitude by remember(garden.longitude) { mutableStateOf(garden.longitude?.toString() ?: "") }
    var elements by remember(garden.elements) { mutableStateOf(garden.elements.toMutableList()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Yard, contentDescription = "Garden", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(garden.name, style = MaterialTheme.typography.h6)
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Yard, contentDescription = "Garden Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    OutlinedTextField(
                        value = width,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                width = newValue
                            }
                        },
                        label = { Text("Width") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Filled.SwapHoriz, contentDescription = "Width") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = height,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                height = newValue
                            }
                        },
                        label = { Text("Height") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Filled.SwapVert, contentDescription = "Height") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = "Location") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                                latitude = newValue
                            }
                        },
                        label = { Text("Latitude") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Filled.Public, contentDescription = "Latitude") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                                longitude = newValue
                            }
                        },
                        label = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = { Icon(Icons.Filled.Public, contentDescription = "Longitude") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Elements (Tiles)", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))

                if (elements.isNotEmpty()) {
                    elements.forEachIndexed { index, tile ->
                        TileEditor(
                            tile = tile,
                            allCrops = allCrops, // Pass all crops to TileEditor
                            onTileChange = { updatedTile ->
                                val newList = elements.toMutableList()
                                newList[index] = updatedTile
                                elements = newList
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Text("No elements found for this garden.")
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        onSave(garden.copy(
                            name = name,
                            width = width.toIntOrNull() ?: garden.width,
                            height = height.toIntOrNull() ?: garden.height,
                            location = location.ifEmpty { null },
                            latitude = latitude.toDoubleOrNull(),
                            longitude = longitude.toDoubleOrNull(),
                            elements = elements
                        ))
                    },
                    modifier = Modifier.fillMaxWidth() 
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TileEditor(
    tile: Tile,
    allCrops: List<Crop>,
    onTileChange: (Tile) -> Unit
) {
    val tileTypes = listOf("Greda", "Visoka greda", "Potka")
    var type by remember(tile.type) { mutableStateOf(tile.type) }
    var typeExpanded by remember { mutableStateOf(false) }

    val currentCrop = allCrops.find { it._id == tile.crop }
    var selectedCropName by remember(currentCrop?.name) { mutableStateOf(currentCrop?.name ?: "") }
    var cropExpanded by remember { mutableStateOf(false) }

    var plantedDate by remember(tile.plantedDate) { mutableStateOf(formatDateForDisplay(tile.plantedDate)) }
    var wateredDate by remember(tile.wateredDate) { mutableStateOf(formatDateForDisplay(tile.wateredDate)) }

    val isCropFieldEnabled = type != "Potka"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text("Tile (${tile.x}, ${tile.y})", style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = !typeExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { },
                readOnly = true,
                label = { Text("Type") },
                leadingIcon = { Icon(Icons.Filled.Category, contentDescription = "Tile Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                tileTypes.forEach { tileType ->
                    DropdownMenuItem(
                        onClick = {
                            type = tileType
                            typeExpanded = false
                            if (tileType == "Potka") {
                                selectedCropName = ""
                                plantedDate = ""
                                wateredDate = ""
                                onTileChange(tile.copy(type = tileType, crop = null, plantedDate = null, wateredDate = null))
                            } else {
                                onTileChange(tile.copy(type = tileType))
                            }
                        }
                    ) {
                        Text(tileType)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = cropExpanded,
            onExpandedChange = { if (isCropFieldEnabled) cropExpanded = !cropExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCropName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Crop") },
                leadingIcon = { Icon(Icons.Filled.Spa, contentDescription = "Crop") },
                trailingIcon = {
                    if (isCropFieldEnabled) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = cropExpanded)
                    }
                },
                enabled = isCropFieldEnabled,
                modifier = Modifier.fillMaxWidth()
            )

            if (isCropFieldEnabled) {
                ExposedDropdownMenu(
                    expanded = cropExpanded,
                    onDismissRequest = { cropExpanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            selectedCropName = ""
                            cropExpanded = false
                            onTileChange(tile.copy(crop = null))
                        }
                    ) {
                        Text("None")
                    }
                    allCrops.forEach { cropOption ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCropName = cropOption.name
                                cropExpanded = false
                                onTileChange(tile.copy(crop = cropOption._id))
                            }
                        ) {
                            Text(cropOption.name)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = plantedDate,
            onValueChange = {
                plantedDate = it
                if (it.isNotEmpty()) {
                    try {
                        val localDate = LocalDate.parse(it, DateTimeFormatter.ofPattern("dd. MM. yyyy"))
                        val isoDateString = localDateToIsoDateTimeString(localDate)
                        onTileChange(tile.copy(plantedDate = isoDateString))
                    } catch (e: Exception) {
                        println("Error with dates")
                    }
                } else {
                    onTileChange(tile.copy(plantedDate = null))
                }
            },
            label = { Text("Planted Date (dd. mm. yyyy)") },
            enabled = isCropFieldEnabled,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = "Planted Date") }
        )
//        Spacer(modifier = Modifier.height(4.dp))
//
//        OutlinedTextField(
//            value = wateredDate,
//            onValueChange = {
//                wateredDate = it
//                if (it.isNotEmpty()) {
//                    try {
//                        val localDate = LocalDate.parse(it, DateTimeFormatter.ofPattern("dd. MM. yyyy"))
//                        val isoDateString = localDateToIsoDateTimeString(localDate)
//                        onTileChange(tile.copy(wateredDate = isoDateString))
//                    } catch (e: Exception) {
//                        // Handle parsing error
//                    }
//                } else {
//                    onTileChange(tile.copy(wateredDate = null))
//                }
//            },
//            label = { Text("Watered Date (dd. mm. yyyy)") },
//            enabled = isCropFieldEnabled,
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}