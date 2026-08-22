package cc.rccstudios.map

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cc.rccstudios.map.data.service.BomberService
import cc.rccstudios.map.ui.screens.bomber.BomberScreen

class BomberActivity : ComponentActivity() {

    private var title by mutableStateOf<String?>(null)
    private var body by mutableStateOf<String?>(null)
    private var sender by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockscreenAndTurnScreenOn()

        setContent {
            BomberScreen(
                onStopClick = { stopBomber() },
                sender = sender
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        sender = intent.getStringExtra(BomberService.EXTRA_SENDER)
    }

    private fun stopBomber() {
        startService(Intent(this, BomberService::class.java).apply {
            action = BomberService.ACTION_STOP
        })
        finish()
    }

    private fun showOverLockscreenAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
}