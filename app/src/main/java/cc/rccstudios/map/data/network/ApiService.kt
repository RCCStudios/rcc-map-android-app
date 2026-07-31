package cc.rccstudios.map.data.network

import cc.rccstudios.map.data.network.model.GetOtpResponseDto
import cc.rccstudios.map.data.network.model.GetTokenResponseDto
import cc.rccstudios.map.data.network.model.RegisterDto
import cc.rccstudios.map.data.network.model.RegisterResponseDto
import cc.rccstudios.map.data.network.model.TelemetryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @Headers("Auth: Bearer {token}")
    @POST("api/sendTelemetry")
    suspend fun sendTelemetry(
        @Body body: TelemetryDto
    ): Response<Unit>

    @POST("api/register")
    suspend fun register(
        @Body body: RegisterDto
    ): Response<RegisterResponseDto>

    @Headers("Auth: Bearer {token}")
    @POST("api/getOtp")
    suspend fun getOtp(): Response<GetOtpResponseDto>

    @Headers("Auth: Bearer {otp}")
    @POST("api/getToken")
    suspend fun getToken(): Response<GetTokenResponseDto>
}