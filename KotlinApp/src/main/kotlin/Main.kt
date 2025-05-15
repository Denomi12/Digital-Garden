import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material.Icon
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color


@Composable
@Preview
fun SideBar() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add person")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Add person")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Rounded.List, contentDescription = "People")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "People")
            }
        }

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(16.dp)
                .padding(top = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                Icon(Icons.Rounded.Share, contentDescription = "Scraper")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Scraper")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Rounded.Build, contentDescription = "Generator")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Generator")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Info, contentDescription = "About")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "About")
            }
        }
    }
}


@Composable
@Preview
fun MainSection(){
    Text(
        text = "nevem2",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
@Preview
fun App() {
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
            SideBar()
        }

        Card(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight()
                .padding(start = 8.dp),
            shape = RoundedCornerShape(4.dp),
        ) {
            MainSection()
        }
    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
