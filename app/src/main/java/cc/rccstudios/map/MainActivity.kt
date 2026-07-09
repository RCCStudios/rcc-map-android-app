package cc.rccstudios.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.rccstudios.map.ui.theme.RCCMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RCCMapTheme {
                RCCMapApp()
            }
        }
    }
}

@Composable
fun RCCMapApp(modifier: Modifier = Modifier){
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Settings(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
        BottomMenu(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun BottomMenu(modifier: Modifier = Modifier){
    var selectedItem by remember { mutableIntStateOf(0) }
    
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageSize = 28.dp
        val color = Color.White
        val fontSize = 16.sp
        BottomMenuButton(
            icon = R.drawable.map_icon,
            text = R.string.map_button,
            imageSize = imageSize,
            color = color,
            fontSize = fontSize,
            imageContentDescription = "Map Icon",
            modifier = Modifier.weight(1f)
        ) { }
        BottomMenuButton(
            icon = R.drawable.settings_icon,
            text = R.string.settings_button,
            imageSize = imageSize,
            color = color,
            fontSize = fontSize,
            imageContentDescription = "Settings Icon",
            modifier = Modifier.weight(1f)
        ) { }
    }
}

@Composable
fun BottomMenuButton(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    imageSize: Dp,
    color: Color,
    fontSize: TextUnit,
    imageContentDescription: String,
    modifier: Modifier,
    onClick: () -> Unit
){
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = imageContentDescription,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier
                    .size(imageSize)
            )
            Text(
                stringResource(text),
                fontSize = fontSize,
                color = color
            )
        }
    }
}
@Composable
fun Settings(
    modifier: Modifier = Modifier
){}

@Preview(showBackground = true)
@Composable
fun RCCMapPreview() {
    RCCMapTheme {
        RCCMapApp()
    }
}