package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import api.CropApi
import kotlinx.coroutines.launch
import models.CompanionCrop
import models.Crop
import models.Watering
import java.awt.FileDialog
import java.awt.Frame

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddCropTab(
    allCrops: List<Crop>,
    onCropAdded: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var latinName by remember { mutableStateOf("") }

    val plantingMonths = listOf(
        "Januar", "Februar", "Marec", "April", "Maj", "Junij",
        "Julij", "Avgust", "September", "Oktober", "November", "December"
    )
    var plantingMonth by remember { mutableStateOf(plantingMonths[0]) }
    var plantingMonthExpanded by remember { mutableStateOf(false) }

    val wateringFrequencies = listOf(
        "Vsak dan", "1-krat na teden", "2-krat na teden", "redko"
    )
    var wateringFrequency by remember { mutableStateOf(wateringFrequencies[0]) }
    var wateringFrequencyExpanded by remember { mutableStateOf(false) }

    var wateringAmount by remember { mutableStateOf("") }
    var imageSrc by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var goodCompanionsState by remember { mutableStateOf(mutableListOf<CompanionCrop>()) }
    var badCompanionsState by remember { mutableStateOf(mutableListOf<CompanionCrop>()) }

    var showGoodCompanionPicker by remember { mutableStateOf(false) }
    var showBadCompanionPicker by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun openFileDialog() {
        val fileDialog = FileDialog(Frame(), "Select Crop Image", FileDialog.LOAD).apply {
            setFilenameFilter { _, name ->
                name.endsWith(".png", ignoreCase = true) ||
                        name.endsWith(".jpg", ignoreCase = true) ||
                        name.endsWith(".jpeg", ignoreCase = true)
            }
            isMultipleMode = false
            isVisible = true
        }

        fileDialog.file?.let { fileName ->
            val directory = fileDialog.directory ?: ""
            imageSrc = "/assets/$fileName"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Add New Crop",
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
            label = { Text("Crop Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Spa, contentDescription = "Crop Name") }
        )

        OutlinedTextField(
            value = latinName,
            onValueChange = { latinName = it },
            label = { Text("Latin Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Science, contentDescription = "Latin Name") }
        )

        ExposedDropdownMenuBox(
            expanded = plantingMonthExpanded,
            onExpandedChange = { plantingMonthExpanded = !plantingMonthExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = plantingMonth,
                onValueChange = { },
                readOnly = true,
                label = { Text("Planting Month") },
                leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = "Planting Month") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = plantingMonthExpanded)
                },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = plantingMonthExpanded,
                onDismissRequest = { plantingMonthExpanded = false }
            ) {
                plantingMonths.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            plantingMonth = selectionOption
                            plantingMonthExpanded = false
                        }
                    ) {
                        Text(selectionOption)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = wateringFrequencyExpanded,
                onExpandedChange = { wateringFrequencyExpanded = !wateringFrequencyExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = wateringFrequency,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Watering Frequency") },
                    leadingIcon = { Icon(Icons.Filled.Schedule, contentDescription = "Watering frequency") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = wateringFrequencyExpanded)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = wateringFrequencyExpanded,
                    onDismissRequest = { wateringFrequencyExpanded = false }
                ) {
                    wateringFrequencies.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                wateringFrequency = selectionOption
                                wateringFrequencyExpanded = false
                            }
                        ) {
                            Text(selectionOption)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = wateringAmount,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() || it == '.' }) {
                        wateringAmount = newValue
                    }
                },
                label = { Text("Water Amount (ml)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.WaterDrop, contentDescription = "Water Amount") }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = imageSrc,
                onValueChange = { imageSrc = it },
                label = { Text("Image Path") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Image, contentDescription = "Image Path") },
                readOnly = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { openFileDialog() },
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = "Browse")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Browse")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Good Companions:", style = MaterialTheme.typography.subtitle2)
        Column {
            goodCompanionsState.forEach { companion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("- ${companion.name} (${companion.latinName})")
                    IconButton(
                        onClick = {
                            goodCompanionsState = goodCompanionsState.toMutableList().apply { remove(companion) }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove good companion")
                    }
                }
            }
            Button(
                onClick = { showGoodCompanionPicker = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Good Companion")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Good Companion")
            }
        }

        if (showGoodCompanionPicker) {
            AlertDialog(
                onDismissRequest = { showGoodCompanionPicker = false },
                title = { Text("Select Good Companion") },
                text = {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allCrops.filter {
                            !goodCompanionsState.any { gc -> gc._id == it._id } &&
                                    !badCompanionsState.any { bc -> bc._id == it._id }
                        }) { availableCrop ->
                            Text(
                                "${availableCrop.name} (${availableCrop.latinName})",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        goodCompanionsState = goodCompanionsState.toMutableList().apply {
                                            add(CompanionCrop(_id = availableCrop._id, name = availableCrop.name, latinName = availableCrop.latinName))
                                        }
                                        showGoodCompanionPicker = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showGoodCompanionPicker = false }) {
                        Text("Close")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Bad Companions:", style = MaterialTheme.typography.subtitle2)
        Column {
            badCompanionsState.forEach { companion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("- ${companion.name} (${companion.latinName})")
                    IconButton(
                        onClick = {
                            badCompanionsState = badCompanionsState.toMutableList().apply { remove(companion) }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove bad companion")
                    }
                }
            }
            Button(
                onClick = { showBadCompanionPicker = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bad Companion")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Bad Companion")
            }
        }

        if (showBadCompanionPicker) {
            AlertDialog(
                onDismissRequest = { showBadCompanionPicker = false },
                title = { Text("Select Bad Companion") },
                text = {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allCrops.filter {
                            !badCompanionsState.any { bc -> bc._id == it._id } &&
                                    !goodCompanionsState.any { gc -> gc._id == it._id }
                        }) { availableCrop ->
                            Text(
                                "${availableCrop.name} (${availableCrop.latinName})",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        badCompanionsState = badCompanionsState.toMutableList().apply {
                                            add(CompanionCrop(_id = availableCrop._id, name = availableCrop.name, latinName = availableCrop.latinName))
                                        }
                                        showBadCompanionPicker = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showBadCompanionPicker = false }) {
                        Text("Close")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Button(
                    onClick = {
                        if (name.isBlank() || latinName.isBlank() || wateringAmount.isBlank()) {
                            error = "Please fill all fields"
                            return@Button
                        }

                        val amount = try {
                            wateringAmount.toDouble()
                        } catch (e: NumberFormatException) {
                            error = "Water amount must be a number"
                            return@Button
                        }

                        isLoading = true
                        error = null
                        success = null

                        coroutineScope.launch {
                            try {
                                val newCrop = Crop(
                                    _id = "",
                                    name = name,
                                    latinName = latinName,
                                    goodCompanions = goodCompanionsState,
                                    badCompanions = badCompanionsState,
                                    plantingMonth = plantingMonth,
                                    watering = Watering(
                                        frequency = wateringFrequency,
                                        amount = amount
                                    ),
                                    imageSrc = if (imageSrc.isBlank()) null else imageSrc
                                )

                                if (CropApi.createCrop(newCrop)) {
                                    success = "Crop created successfully!"
                                    name = ""
                                    latinName = ""
                                    plantingMonth = plantingMonths[0]
                                    wateringFrequency = wateringFrequencies[0]
                                    wateringAmount = ""
                                    imageSrc = ""
                                    goodCompanionsState = mutableListOf()
                                    badCompanionsState = mutableListOf()
                                    onCropAdded()
                                } else {
                                    error = "Failed to create crop"
                                }
                            } catch (e: Exception) {
                                error = "Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Create", color = Color.White)
                }
            }
        }
    }
}