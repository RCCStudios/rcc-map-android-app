package cc.rccstudios.map.data.network

import cc.rccstudios.map.data.network.model.RegisterDto
import cc.rccstudios.map.data.network.model.TelemetryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("sendData")
    suspend fun sendTelemetry(
        @Body body: TelemetryDto
    ): Response<Unit>

    @POST("register")
    suspend fun register(
        @Body body: RegisterDto
    ): Response<Unit>
}