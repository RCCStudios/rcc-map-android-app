package cc.rccstudios.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.data.service.TelemetryService
import cc.rccstudios.map.domain.model.UpdateStatus
import cc.rccstudios.map.ui.MainViewModel
import cc.rccstudios.map.ui.screens.menu.BottomMenu
import cc.rccstudios.map.ui.dialogs.update.UpdateDialog
import cc.rccstudios.map.ui.theme.RCCMapTheme
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RCCMapTheme {
                val viewModel: MainViewModel = koinInject()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val uriHandler = LocalUriHandler.current

                LaunchedEffect(Unit) {
                    viewModel.checkUpdates()
                }

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

                    if (!isNotificationGranted) {
                        Toast.makeText(
                            this,
                            "Notification permission is necessary for RCC Map correct work",
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

                LaunchedEffect(
                    state.token,
                    state.isTelemetryEnabled,
                    state.telemetryInterval
                ) {
                    val shouldRunService = !state.token.isNullOrBlank() && state.isTelemetryEnabled

                    if (shouldRunService) {
                        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        } else true

                        if (hasNotificationPermission) {
                            val intent = Intent(this@MainActivity, TelemetryService::class.java).apply {
                                action = TelemetryService.ACTION_START
                                putExtra(TelemetryService.EXTRA_INTERVAL, state.telemetryInterval)
                            }
                            ContextCompat.startForegroundService(this@MainActivity, intent)
                        }
                    } else {
                        val intent = Intent(this@MainActivity, TelemetryService::class.java).apply {
                            action = TelemetryService.ACTION_STOP
                        }
                        startService(intent)
                    }
                }

                val updateInfo = state.updateInfo
                if (updateInfo is UpdateStatus.NewVersionAvailable) {
                    UpdateDialog(
                        updateInfo = updateInfo,
                        onDownloadClick = { url ->
                            uriHandler.openUri(url)
                            viewModel.dismissUpdateDialog()
                        },
                        onDismiss = { viewModel.dismissUpdateDialog() }
                    )
                }

                BottomMenu(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}