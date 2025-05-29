package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import api.CropApi
import kotlinx.coroutines.launch
import models.Crop

@Composable
fun CropsTab() {
    var crops by remember { mutableStateOf<List<Crop>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var expandedCropId by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }  // Dodajmo success sporočilo
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
                                        loadCrops()  // Ponovno naložimo podatke iz baze
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

@Composable
fun CropCardExpandable(
    crop: Crop,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onSave: (Crop) -> Unit
) {
    var name by remember { mutableStateOf(crop.name) }
    var latinName by remember { mutableStateOf(crop.latinName) }
    var plantingMonth by remember { mutableStateOf(crop.plantingMonth) }
    var wateringFrequency by remember { mutableStateOf(crop.watering.frequency) }
    var wateringAmount by remember { mutableStateOf(crop.watering.amount.toString()) }

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
                OutlinedTextField(
                    value = plantingMonth,
                    onValueChange = { plantingMonth = it },
                    label = { Text("Planting Month") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = wateringFrequency,
                        onValueChange = { wateringFrequency = it },
                        label = { Text("Watering Frequency") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = wateringAmount,
                        onValueChange = { wateringAmount = it },
                        label = { Text("Watering Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Display companions if they exist
                if (crop.goodCompanions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Good Companions:", style = MaterialTheme.typography.subtitle2)
                    Column {
                        crop.goodCompanions.forEach { companion ->
                            Text("- ${companion.name} (${companion.latinName})")
                        }
                    }
                }

                if (crop.badCompanions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bad Companions:", style = MaterialTheme.typography.subtitle2)
                    Column {
                        crop.badCompanions.forEach { companion ->
                            Text("- ${companion.name} (${companion.latinName})")
                        }
                    }
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
                            )
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