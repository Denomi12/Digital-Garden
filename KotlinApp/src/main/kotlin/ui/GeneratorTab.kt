package ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRow
import androidx.compose.material.Tab
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import api.CropApi
import kotlinx.coroutines.launch
import models.Crop
import models.GenerateCropResponse

@Composable
fun GeneratorTab() {
    val tabs = listOf("Gardens", "Crops")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Data Generator", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> GeneratorCard(title = "Gardens", { amount -> CropApi.generateCrop(amount) })
            1 -> GeneratorCard(title = "Crops", { amount -> CropApi.generateCrop(amount) })
        }
    }
}

@Composable
fun GeneratorCard(title: String, generateFun: suspend (Int) -> GenerateCropResponse) {
    var count by remember { mutableStateOf("1") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<GenerateCropResponse?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var allCrops by remember { mutableStateOf<List<Crop>>(emptyList()) }
    var cropLoadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            allCrops = CropApi.getCrops()
        } catch (e: Exception) {
            cropLoadError = "Failed to load crop data: ${e.message}"
        }
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Generate $title", style = MaterialTheme.typography.h6)

            OutlinedTextField(
                value = count,
                onValueChange = { if (it.all { c -> c.isDigit() }) count = it },
                label = { Text("How many?") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (successMessage != null) {
                Text(successMessage!!, color = MaterialTheme.colors.primary)
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colors.error)
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        successMessage = null
                        errorMessage = null
                        try {
                            val howMany = count.toIntOrNull() ?: 0
                            if (howMany <= 0) throw IllegalArgumentException("Enter a positive number")

                            // Simulate data generation with delay or call to API
                            // For example: Api.generate(title.lowercase(), howMany)
                            generatedResult = generateFun(howMany)
                            println("Generated result: $generatedResult")
                            successMessage = "Successfully generated $howMany $title."
                            showDialog = true
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Generate")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Generated Crops") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    generatedResult!!.crops.forEachIndexed { index, crop ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text("🌱 Crop ${index + 1}", style = MaterialTheme.typography.subtitle1)
                            Text("Name: ${crop.name}")
                            Text("Latin Name: ${crop.latinName}")
                            Text("Planting Month: ${crop.plantingMonth}")
                            Text("Watering: ${crop.watering.frequency}, ${crop.watering.amount}L")

                            val goodNames = crop.goodCompanions.mapNotNull { id ->
                                allCrops.find { it._id == id }?.name
                            }

                            val badNames = crop.badCompanions.mapNotNull { id ->
                                allCrops.find { it._id == id }?.name
                            }

                            Text("✅ Good Companions: ${goodNames.joinToString()}")
                            Text("❌ Bad Companions: ${badNames.joinToString()}")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
