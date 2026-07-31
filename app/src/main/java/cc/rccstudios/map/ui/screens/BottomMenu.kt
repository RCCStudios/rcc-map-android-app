package cc.rccstudios.map.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cc.rccstudios.map.R
import cc.rccstudios.map.ui.MainViewModel

enum class Destination(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int
) {
    ACCOUNT(
        "account",
        R.string.account_button,
        Icons.Default.AccountCircle,
        R.string.account_button
    ),
    MAP(
        "map",
        R.string.map_button,
        Icons.Default.Map,
        R.string.map_button
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
    viewModel: MainViewModel,
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.ACCOUNT -> AccountScreen(
                        viewModel = viewModel,
                        modifier = modifier
                    )
                    Destination.MAP -> MapScreen(
                        viewModel = viewModel,
                        mapUrl = state.serverUrl
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
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val startDestination = Destination.MAP
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == destination.route } == true

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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