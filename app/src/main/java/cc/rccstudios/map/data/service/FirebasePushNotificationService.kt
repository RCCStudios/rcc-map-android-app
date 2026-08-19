package cc.rccstudios.map.data.service

import android.annotation.SuppressLint
import android.util.Log
import cc.rccstudios.map.domain.model.PushPayload
import cc.rccstudios.map.domain.usecase.UpdateDeviceUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebasePushNotificationService : FirebaseMessagingService(), KoinComponent {

    private val pushPayloadHandler: PushPayloadHandler by inject()
    private val updateDeviceUseCase: UpdateDeviceUseCase by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "FirebasePushNotificationService"
    }

    override fun onCreate() {
        super.onCreate()
        pushPayloadHandler.createNotificationChannels()
    }

    override fun onRegistered(fid: String) {
        super.onRegistered(fid)
        Log.d(TAG, "Registered FID: $fid")
        serviceScope.launch {
            updateDeviceUseCase(fid)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        pushPayloadHandler.handle(
            PushPayload(
                action = message.data["action"],
                title = message.notification?.title,
                body = message.notification?.body,
                data = message.data
            )
        )
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}