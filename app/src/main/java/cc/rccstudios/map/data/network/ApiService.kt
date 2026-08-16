package cc.rccstudios.map.data.network

import cc.rccstudios.map.data.network.model.FidDto
import cc.rccstudios.map.data.network.model.GetOtpResponseDto
import cc.rccstudios.map.data.network.model.GetTokenResponseDto
import cc.rccstudios.map.data.network.model.GetUserResponseDto
import cc.rccstudios.map.data.network.model.GithubReleaseDto
import cc.rccstudios.map.data.network.model.RegisterDto
import cc.rccstudios.map.data.network.model.RegisterResponseDto
import cc.rccstudios.map.data.network.model.TelemetryDto
import cc.rccstudios.map.data.network.model.UpdateUserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @Headers("Auth: Bearer {token}")
    @POST("api/telemetry")
    suspend fun sendTelemetry(
        @Body body: TelemetryDto
    ): Response<Unit>

    @POST("api/register")
    suspend fun register(
        @Body body: RegisterDto
    ): Response<RegisterResponseDto>

    @Headers("Auth: Bearer {token}")
    @GET("api/otp")
    suspend fun getOtp(): Response<GetOtpResponseDto>

    @Headers("Auth: Bearer {otp}")
    @GET("api/token")
    suspend fun getToken(): Response<GetTokenResponseDto>

    @Headers("Auth: Bearer {token}")
    @GET("api/user")
    suspend fun getUser(): Response<GetUserResponseDto>

    @Headers("Auth: Bearer {token}")
    @POST("api/user")
    suspend fun updateUser(
        @Body body: UpdateUserDto
    ): Response<Unit>

    @Headers("Auth: Bearer {token}")
    @POST("api/user/fid")
    suspend fun updateFid(
        @Body body: FidDto
    ): Response<Unit>

    @GET
    suspend fun getLatestRelease(
        @Url url: String = "https://api.github.com/repos/RCCStudios/rcc-map-android-app/releases/latest"
    ): Response<GithubReleaseDto>
}