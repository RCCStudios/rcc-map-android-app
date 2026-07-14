package cc.rccstudios.map.data.network

import cc.rccstudios.map.data.network.model.RegisterDto
import cc.rccstudios.map.data.network.model.TelemetryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST
    suspend fun sendTelemetry(
        @Url url: String,
        @Body body: TelemetryDto
    ): Response<Unit>

    @POST
    suspend fun register(
        @Url url: String,
        @Body body: RegisterDto
    ): Response<Unit>
}