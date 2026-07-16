package cc.rccstudios.map

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.data.service.TelemetryService
import cc.rccstudios.map.ui.MainModelView
import cc.rccstudios.map.ui.screens.BottomMenu
import cc.rccstudios.map.ui.theme.RCCMapTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RCCMapTheme {
                val viewModel: MainModelView = koinInject()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                val permissionsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permission ->
                    val isLocationGranted = permission[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    val isNotificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permission[Manifest.permission.POST_NOTIFICATIONS] == true
                    } else true

                    if (!isLocationGranted) {
                        Toast.makeText(
                            this,
                            "Location permission is strictly necessary for RCC Map correct work",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                LaunchedEffect(Unit) {
                    val permissions = mutableListOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    permissionsLauncher.launch(permissions.toTypedArray())
                }

                LaunchedEffect(state.token) {
                    val intent = Intent(applicationContext, TelemetryService::class.java)
                    if (!state.token.isNullOrBlank()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent)
                        } else {
                            startService(intent)
                        }
                    } else {
                        stopService(intent)
                    }
                }

                LaunchedEffect(state.logMessage) {
                    if (state.logMessage.isNotEmpty()) {
                        Toast.makeText(applicationContext, state.logMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                BottomMenu(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}