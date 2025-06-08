package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed // Uporabimo itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import scraping.*

@Composable
fun ScraperTab() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Mesečna sajenja", "Sosedje")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        when (selectedTab) {
            0 -> MonthlyPlantingView()
            1 -> NeighboursView()
        }
    }
}

@Composable
fun MonthlyPlantingView() {
    val monthlyData = remember { mutableStateListOf<MonthlyPlanting>() }

    val showEditDialog = remember { mutableStateOf(false) }
    val editingSection = remember { mutableStateOf<PlantingSection?>(null) }
    val editingMonthIndex = remember { mutableStateOf(-1) }
    val editingSectionIndex = remember { mutableStateOf(-1) }


    LaunchedEffect(Unit) {
        val scrapedData = withContext(Dispatchers.IO) { scrapeVegetables() }
        monthlyData.addAll(scrapedData)
    }

    if (showEditDialog.value && editingSection.value != null) {
        EditPlantingDialog(
            section = editingSection.value!!,
            onDismiss = { showEditDialog.value = false },
            onSave = { updatedVegetables ->
                // Posodobimo podatke v našem glavnem seznamu
                val month = monthlyData[editingMonthIndex.value]
                val updatedSections = month.sections.toMutableList()
                updatedSections[editingSectionIndex.value] = editingSection.value!!.copy(vegetables = updatedVegetables.split(",").map { it.trim() })

                monthlyData[editingMonthIndex.value] = month.copy(sections = updatedSections)
                showEditDialog.value = false
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        itemsIndexed(monthlyData) { monthIndex, monthData ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = monthData.month,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    monthData.sections.forEachIndexed { sectionIndex, section ->
                        if (section.vegetables.isNotEmpty() && section.vegetables[0] != "/") {
                            Text(
                                text = section.type,
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = section.vegetables.joinToString(", "),
                                modifier = Modifier
                                    .padding(start = 8.dp, bottom = 4.dp)
                                    // 3. Dodamo možnost klika za urejanje
                                    .clickable {
                                        editingMonthIndex.value = monthIndex
                                        editingSectionIndex.value = sectionIndex
                                        editingSection.value = section
                                        showEditDialog.value = true
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditPlantingDialog(
    section: PlantingSection,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(section.vegetables.joinToString(", ")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Uredi: ${section.type}") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Zelenjava (ločena z vejico)") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(text) }) {
                Text("Shrani")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Prekliči")
            }
        }
    )
}

@Composable
fun NeighboursView() {
    val neighboursData = remember { mutableStateListOf<GoodNeighbours>() }
    val showEditDialog = remember { mutableStateOf(false) }
    val editingNeighbour = remember { mutableStateOf<GoodNeighbours?>(null) }
    val editingIndex = remember { mutableStateOf(-1) }


    LaunchedEffect(Unit) {
        val scrapedData = withContext(Dispatchers.IO) { scrapeGoodNeighbours() }
        neighboursData.addAll(scrapedData)
    }

    if (showEditDialog.value && editingNeighbour.value != null) {
        EditNeighboursDialog(
            neighbour = editingNeighbour.value!!,
            onDismiss = { showEditDialog.value = false },
            onSave = { updatedGood, updatedBad ->
                neighboursData[editingIndex.value] = editingNeighbour.value!!.copy(
                    goodNeighbours = updatedGood,
                    badNeighbours = updatedBad
                )
                showEditDialog.value = false
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        itemsIndexed(neighboursData) { index, neighbour ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable {
                        editingIndex.value = index
                        editingNeighbour.value = neighbour
                        showEditDialog.value = true
                    },
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = neighbour.vegetable,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dobri sosedje:", style = MaterialTheme.typography.subtitle2)
                            Text(neighbour.goodNeighbours, modifier = Modifier.padding(start = 8.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Slabi sosedje:", style = MaterialTheme.typography.subtitle2)
                            Text(neighbour.badNeighbours, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditNeighboursDialog(
    neighbour: GoodNeighbours,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var goodText by remember { mutableStateOf(neighbour.goodNeighbours) }
    var badText by remember { mutableStateOf(neighbour.badNeighbours) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Uredi sosede za: ${neighbour.vegetable}") },
        text = {
            Column {
                OutlinedTextField(
                    value = goodText,
                    onValueChange = { goodText = it },
                    label = { Text("Dobri sosedje") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = badText,
                    onValueChange = { badText = it },
                    label = { Text("Slabi sosedje") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(goodText, badText) }) {
                Text("Shrani")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Prekliči")
            }
        }
    )
}