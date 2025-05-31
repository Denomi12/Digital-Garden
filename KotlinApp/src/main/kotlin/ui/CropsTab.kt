package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
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

@Composable
fun CropsTab() {
    var crops by remember { mutableStateOf<List<Crop>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var expandedCropId by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun loadCrops() {
        coroutineScope.launch {
            isLoading = true
            error = null
            showSuccess = false
            try {
                crops = CropApi.getCrops()
            } catch (e: Exception) {
                error = "Error loading crops: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCrops()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Crops", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        if (showSuccess) {
            Text("Crop updated successfully!", color = Color.Green)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (error != null) {
            Text(error!!, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { loadCrops() }) {
                Text("Retry")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(crops) { crop ->
                    CropCardExpandable(
                        crop = crop,
                        // posredujem seznam vseh rastlin, da jih lahko izberem kot sosede
                        allCrops = crops,
                        isExpanded = expandedCropId == crop._id,
                        onClick = {
                            expandedCropId = if (expandedCropId == crop._id) null else crop._id
                        },
                        onSave = { updatedCrop ->
                            coroutineScope.launch {
                                isLoading = true
                                error = null
                                showSuccess = false
                                try {
                                    if (CropApi.updateCrop(updatedCrop)) {
                                        showSuccess = true
                                        loadCrops()
                                    }
                                } catch (e: Exception) {
                                    error = "Error saving crop: ${e.message}"
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CropCardExpandable(
    crop: Crop,
    allCrops: List<Crop>,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onSave: (Crop) -> Unit
) {
    var name by remember { mutableStateOf(crop.name) }
    var latinName by remember { mutableStateOf(crop.latinName) }
    var plantingMonth by remember { mutableStateOf(crop.plantingMonth) }
    var wateringFrequency by remember { mutableStateOf(crop.watering.frequency) }
    var wateringAmount by remember { mutableStateOf(crop.watering.amount.toString()) }

    var goodCompanionsState by remember { mutableStateOf(crop.goodCompanions.toMutableList()) }
    var badCompanionsState by remember { mutableStateOf(crop.badCompanions.toMutableList()) }

    val plantingMonths = listOf(
        "Januar", "Februar", "Marec", "April", "Maj", "Junij",
        "Julij", "Avgust", "September", "Oktober", "November", "December"
    )
    var plantingMonthExpanded by remember { mutableStateOf(false) }

    val wateringFrequencies = listOf(
        "Vsak dan", "1-krat na teden", "2-krat na teden", "redko"
    )
    var wateringFrequencyExpanded by remember { mutableStateOf(false) }

    var showGoodCompanionPicker by remember { mutableStateOf(false) }
    var showBadCompanionPicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = "Crop", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(crop.name, style = MaterialTheme.typography.h6)
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = latinName,
                    onValueChange = { latinName = it },
                    label = { Text("Latin Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = plantingMonthExpanded,
                    onExpandedChange = { plantingMonthExpanded = !plantingMonthExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = plantingMonth,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Planting Month") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = plantingMonthExpanded)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = plantingMonthExpanded,
                        onDismissRequest = { plantingMonthExpanded = false }
                    ) {
                        plantingMonths.forEach { month ->
                            DropdownMenuItem(
                                onClick = {
                                    plantingMonth = month
                                    plantingMonthExpanded = false
                                }
                            ) {
                                Text(month)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row {
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
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = wateringFrequencyExpanded)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = wateringFrequencyExpanded,
                            onDismissRequest = { wateringFrequencyExpanded = false }
                        ) {
                            wateringFrequencies.forEach { frequency ->
                                DropdownMenuItem(
                                    onClick = {
                                        wateringFrequency = frequency
                                        wateringFrequencyExpanded = false
                                    }
                                ) {
                                    Text(frequency)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = wateringAmount,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() || it == '.' }) {
                                wateringAmount = newValue
                            }
                        },
                        label = { Text("Watering Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Urejanje dobrih sosedov
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
                        Icon(Icons.Default.Add, contentDescription = "Add good companion")
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
                                    it._id != crop._id && // Ne more biti sam sebi sosed
                                            !goodCompanionsState.any { gc -> gc._id == it._id } && // Še ni dober sosed
                                            !badCompanionsState.any { bc -> bc._id == it._id } // Ni slab sosed
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
                // Urejanje slabih sosedov
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
                        Icon(Icons.Default.Add, contentDescription = "Add bad companion")
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
                                    it._id != crop._id &&
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

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val amount = try {
                            wateringAmount.toDouble()
                        } catch (e: NumberFormatException) {
                            crop.watering.amount
                        }
                        onSave(crop.copy(
                            name = name,
                            latinName = latinName,
                            plantingMonth = plantingMonth,
                            watering = crop.watering.copy(
                                frequency = wateringFrequency,
                                amount = amount
                            ),
                            goodCompanions = goodCompanionsState,
                            badCompanions = badCompanionsState
                        ))
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
                ) {
                    Text("Save", color = Color.White)
                }
            }
        }
    }
}