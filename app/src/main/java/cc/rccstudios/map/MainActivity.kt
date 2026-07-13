package cc.rccstudios.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cc.rccstudios.map.data.tracker.location.LocationTrackerImpl
import cc.rccstudios.map.ui.theme.RCCMapTheme

class MainActivity : ComponentActivity() {
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            getLocationAndLog()
//        } else {
//            Log.d("SVO", "Donbass Dumbass")
//        }
//    }

    private lateinit var locationTracker: LocationTrackerImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val fusedClient = LocationServices.getFusedLocationProviderClient(this)
//        locationTracker = LocationTracker(this, fusedClient)
        enableEdgeToEdge()
        setContent {
            RCCMapTheme {
//                LaunchedEffect(Unit) {
//                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                }
//                RCCMapApp(
//                    {
//                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                    }
//                )
            }
        }
    }
//    private fun getLocationAndLog() {
//        lifecycleScope.launch {
//            val location = locationTracker.getLastLocation()
//            if (location != null) {
//                Log.d("SVO", "la lo ${location.latitude}, ${location.longitude}")
//            } else {
//                Log.d("SVO", "fsdjklfjlk")
//            }
//        }
//    }
}

//@Composable
//fun RCCMapApp(onMapClick: () -> Unit, modifier: Modifier = Modifier){
//    Box(
//        modifier = modifier.fillMaxSize()
//    ) {
//        BottomMenu(
//            onMapTabSelected = onMapClick,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .fillMaxWidth()
//        )
//    }
//}
//
//@Composable
//fun BottomMenu(onMapTabSelected: () -> Unit, modifier: Modifier = Modifier){
//    Row (
//        modifier = modifier,
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        val imageSize = 28.dp
//        val color = Color.Black
//        val fontSize = 16.sp
//        BottomMenuButton(
//            icon = R.drawable.map_icon,
//            text = R.string.map_button,
//            imageSize = imageSize,
//            color = color,
//            fontSize = fontSize,
//            imageContentDescription = "Map Icon",
//            modifier = Modifier.weight(1f),
//            onMapTabSelected
//        )
//        BottomMenuButton(
//            icon = R.drawable.settings_icon,
//            text = R.string.settings_button,
//            imageSize = imageSize,
//            color = color,
//            fontSize = fontSize,
//            imageContentDescription = "Settings Icon",
//            modifier = Modifier.weight(1f),
//        ) { }
//    }
//}
//
//@Composable
//fun BottomMenuButton(
//    @DrawableRes icon: Int,
//    @StringRes text: Int,
//    imageSize: Dp,
//    color: Color,
//    fontSize: TextUnit,
//    imageContentDescription: String,
//    modifier: Modifier,
//    onClick: () -> Unit
//){
//    Column(
//        modifier = modifier
//            .clickable(onClick = onClick)
//            .padding(vertical = 8.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Image(
//                painter = painterResource(icon),
//                contentDescription = imageContentDescription,
//                colorFilter = ColorFilter.tint(color),
//                modifier = Modifier
//                    .size(imageSize)
//            )
//            Text(
//                stringResource(text),
//                fontSize = fontSize,
//                color = color
//            )
//        }
//    }
//}
//@Composable
//fun Settings(
//    modifier: Modifier = Modifier
//){}
//
//@Preview(showBackground = true)
//@Composable
//fun RCCMapPreview() {
//    RCCMapTheme {
//    }
//}