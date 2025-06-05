import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.CropApi
import kotlinx.coroutines.launch
import models.Crop
import ui.*

@Composable
fun MenuRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(50.dp)
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Icon(icon, contentDescription = label)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}


@Composable
fun SideBar(setTab: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column {
            MenuRow("Add person", Icons.Rounded.Add) { setTab("Add person") }
            MenuRow("People", Icons.Rounded.List) { setTab("People") }
        }

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Column {
            MenuRow("Add crop", Icons.Rounded.Add) { setTab("Add crop") }
            MenuRow("Crops", Icons.Rounded.List) { setTab("Crops") }
        }

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Column {
            MenuRow("Add garden", Icons.Rounded.Add) { setTab("Add garden") }
            MenuRow("Gardens", Icons.Rounded.List) { setTab("Gardens") }
        }

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Column {
            MenuRow("Scraper", Icons.Rounded.Share) { setTab("Scraper") }
            MenuRow("Generator", Icons.Rounded.Build) { setTab("Generator") }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column {
            MenuRow("About", Icons.Rounded.Info) { setTab("About") }
        }
    }
}


@Composable
@Preview
fun MainSection(tab: String, allCrops: List<Crop>, onCropsUpdated: () -> Unit) {
    if (tab == "Home") {
        Text(
            text = "Home",
            modifier = Modifier.padding(16.dp)
        )
    } else if (tab == "Add person") {
        AddPersonTab()
    } else if (tab == "People") {
        PeopleTab()
    } else if (tab == "Add crop" ) {
        AddCropTab(allCrops = allCrops, onCropAdded = onCropsUpdated)
    } else if (tab == "Crops") {
        CropsTab()
    } else if (tab == "Gardens") {
        GardensTab()
    } else if (tab == "Scraper") {
        ScraperTab()
    } else if (tab == "Generator") {
        GeneratorTab()
    } else {
        Text(
            text = "About",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
@Preview
fun App() {
    var tab by remember { mutableStateOf("Home") }
    var allCrops by remember { mutableStateOf<List<Crop>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    fun loadAllCrops() {
        coroutineScope.launch {
            try {
                allCrops = CropApi.getCrops()
                println("Crops loaded in App: ${allCrops.size} items")
            } catch (e: Exception) {
                println("Error loading all crops in App: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAllCrops()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 8.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            SideBar(setTab = { tab = it })
        }

        Card(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
                .padding(start = 8.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            MainSection(tab = tab, allCrops = allCrops, onCropsUpdated = { loadAllCrops() })
        }
    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
