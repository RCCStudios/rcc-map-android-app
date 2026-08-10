package cc.rccstudios.map.di

import android.content.Context
import cc.rccstudios.map.data.repository.SettingsRepositoryImpl
import cc.rccstudios.map.data.tracker.battery.BatteryTrackerImpl
import cc.rccstudios.map.data.tracker.location.LocationTrackerImpl
import cc.rccstudios.map.data.tracker.network.NetworkTrackerImpl
import cc.rccstudios.map.data.tracker.screenlock.ScreenLockTrackerImpl
import cc.rccstudios.map.data.repository.TelemetryRepositoryImpl
import cc.rccstudios.map.domain.repository.SettingsRepository
import cc.rccstudios.map.domain.repository.TelemetryRepository
import cc.rccstudios.map.domain.tracker.BatteryTracker
import cc.rccstudios.map.domain.tracker.LocationTracker
import cc.rccstudios.map.domain.tracker.NetworkTracker
import cc.rccstudios.map.domain.tracker.ScreenLockTracker
import cc.rccstudios.map.domain.usecase.CollectAndSendTelemetryUseCase
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import androidx.datastore.preferences.preferencesDataStore
import cc.rccstudios.map.data.repository.AuthRepositoryImpl
import cc.rccstudios.map.data.repository.UpdateRepositoryImpl
import cc.rccstudios.map.data.repository.UserRepositoryImpl
import cc.rccstudios.map.domain.repository.AuthRepository
import cc.rccstudios.map.domain.repository.UpdateRepository
import cc.rccstudios.map.domain.repository.UserRepository
import cc.rccstudios.map.domain.usecase.CheckUpdatesUseCase
import cc.rccstudios.map.domain.usecase.GetOtpUseCase
import cc.rccstudios.map.domain.usecase.GetTokenUseCase
import cc.rccstudios.map.domain.usecase.LoginUseCase
import cc.rccstudios.map.domain.usecase.RegisterUseCase
import cc.rccstudios.map.domain.usecase.UpdateUserUseCase
import cc.rccstudios.map.ui.MainViewModel
import cc.rccstudios.map.utils.toNormalizedUrl
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.viewModel
import retrofit2.converter.gson.GsonConverterFactory

private val Context.dataStore by preferencesDataStore(name = "app_settings")

val appModule = module {

    single { androidContext().dataStore }

    single<SettingsRepository> { SettingsRepositoryImpl(dataStore = get()) }

    single<AuthRepository> { AuthRepositoryImpl(apiService = get(), get()) }

    single<TelemetryRepository> {
        TelemetryRepositoryImpl(
            apiService = get(),
            settingsRepository = get(),
            batteryTracker = get(),
            locationTracker = get(),
            networkTracker = get(),
            screenLockTracker = get()
        )
    }

    single<UpdateRepository> { UpdateRepositoryImpl(apiService = get()) }

    single<UserRepository> { UserRepositoryImpl(apiService = get(), settingsRepository = get()) }


    single<BatteryTracker> { BatteryTrackerImpl(context = androidContext()) }

    single<NetworkTracker> { NetworkTrackerImpl(context = androidContext()) }

    single<ScreenLockTracker> { ScreenLockTrackerImpl(context = androidContext()) }

    single<LocationTracker> {
        LocationTrackerImpl(
            context = androidContext(),
            fusedLocationClient = get(),
            locationRequest = get()
        )
    }

    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    single {
        com.google.android.gms.location.CurrentLocationRequest.Builder()
            .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .setDurationMillis(30000)
            .build()
    }

    single {
        val settingsRepository: SettingsRepository = get()

        val authInterceptor = okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()

            val isExternalService = originalRequest.url.host.contains("github.com")

            val (token, otp, serverUrl) = runBlocking {
                val t = settingsRepository.getToken() ?: ""
                val o = settingsRepository.getOtp() ?: ""
                val u = settingsRepository.getServerUrl() ?: ""
                Triple(t, o, u)
            }
            if (!isExternalService && serverUrl.isNotBlank()) {
                val formattedServerUrl = serverUrl.toNormalizedUrl()
                formattedServerUrl.toHttpUrlOrNull()?.let { parsedUrl ->
                    val newUrl = originalRequest.url.newBuilder()
                        .scheme(parsedUrl.scheme)
                        .host(parsedUrl.host)
                        .port(parsedUrl.port)
                        .build()
                    requestBuilder.url(newUrl)
                }
            }

            val authHeader = originalRequest.header("Auth")
            when (authHeader) {
                "Bearer {token}" -> {
                    requestBuilder.removeHeader("Auth")
                    if (token.isNotBlank()) {
                        requestBuilder.header("Authorization", "Bearer $token")
                    }
                }
                "Bearer {otp}" -> {
                    requestBuilder.removeHeader("Auth")
                    if (otp.isNotBlank()) {
                        requestBuilder.header("Authorization", "Bearer $otp")
                    }
                }
            }

            chain.proceed(requestBuilder.build())
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://localhost/")
            .client(get<OkHttpClient>())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(cc.rccstudios.map.data.network.ApiService::class.java) }

    factory { CollectAndSendTelemetryUseCase(settingsRepository = get(), telemetryRepository = get()) }

    factory { RegisterUseCase(authRepository = get()) }

    factory { GetOtpUseCase(authRepository = get()) }

    factory { GetTokenUseCase(authRepository = get()) }

    factory { CheckUpdatesUseCase(updateRepository = get()) }

    factory { LoginUseCase(getTokenUseCase = get(), userRepository = get()) }

    factory { UpdateUserUseCase(userRepository = get()) }

    viewModel {
        MainViewModel(
            settingsRepository = get(),
            collectAndSendTelemetryUseCase = get(),
            registerUseCase = get(),
            loginUseCase = get(),
            getOtpUseCase = get(),
            checkUpdatesUseCase = get(),
            updateUserUseCase = get()
        )
    }
}