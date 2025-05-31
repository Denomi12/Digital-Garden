package ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    val monthlyData by remember { mutableStateOf(scrapeVegetables()) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(monthlyData) { monthData ->
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

                    monthData.sections.forEach { section ->
                        if (section.vegetables.isNotEmpty() && section.vegetables[0] != "/") {
                            Text(
                                text = section.type,
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Text(
                                text = section.vegetables.joinToString(", "),
                                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeighboursView() {
    val neighboursData by remember { mutableStateOf(scrapeGoodNeighbours()) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        items(neighboursData) { neighbour ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
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
                            Text(
                                text = "Dobri sosedje:",
                                style = MaterialTheme.typography.subtitle2
                            )
                            Text(
                                text = neighbour.goodNeighbours,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Slabi sosedje:",
                                style = MaterialTheme.typography.subtitle2
                            )
                            Text(
                                text = neighbour.badNeighbours,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
