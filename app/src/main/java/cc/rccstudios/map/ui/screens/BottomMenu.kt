package cc.rccstudios.map.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cc.rccstudios.map.R
import cc.rccstudios.map.ui.MainModelView
import cc.rccstudios.map.ui.theme.RCCMapTheme

enum class Destination(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    MAP(
        "map",
        R.string.map_button,
        Icons.Default.Map,
        R.string.map_button
    ),
    HOME(
        "home",
        R.string.home_button,
        Icons.Default.Home,
        R.string.home_button
    ),
    SETTINGS(
        "settings",
        R.string.settings_button,
        Icons.Default.Settings,
        R.string.settings_button
    ),
}

@Composable
fun AppNavHost(
    viewModel: MainModelView,
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.MAP -> MapScreen(
                        viewModel = viewModel,
                        modifier = modifier
                    )
                    Destination.HOME -> SettingsScreen(
                        viewModel = viewModel,
                        modifier = modifier
                    )
                    Destination.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
fun BottomMenu(
    viewModel: MainModelView,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = stringResource(destination.contentDescription)
                            )
                        },
                        label = { Text(stringResource(destination.label)) }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(viewModel, navController, startDestination, modifier = Modifier.padding(contentPadding))
    }
}

@Preview(showBackground = true)
@Composable
fun BottomMenuPreview() {
    RCCMapTheme{
        val navController = rememberNavController()
        val startDestination = Destination.HOME
        var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

        Scaffold(
            modifier = Modifier,
            bottomBar = {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    Destination.entries.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedDestination == index,
                            onClick = {
                                navController.navigate(route = destination.route)
                                selectedDestination = index
                            },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = stringResource(destination.contentDescription)
                                )
                            },
                            label = { Text(stringResource(destination.label)) }
                        )
                    }
                }
            }
        ) {

        }
    }
}