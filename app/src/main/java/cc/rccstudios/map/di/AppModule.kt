package cc.rccstudios.map.di

import cc.rccstudios.map.data.tracker.battery.BatteryTrackerImpl
import cc.rccstudios.map.data.tracker.location.LocationTrackerImpl
import cc.rccstudios.map.data.tracker.network.NetworkTrackerImpl
import cc.rccstudios.map.data.tracker.screenlock.ScreenLockTrackerImpl
import cc.rccstudios.map.data.repository.TelemetryRepositoryImpl
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

val appModule = module {

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
        Retrofit.Builder()
            .baseUrl("")
            .build()
    }

    single { get<Retrofit>().create(cc.rccstudios.map.data.network.ApiService::class.java) }

    single<TelemetryRepository> {
        TelemetryRepositoryImpl(
            apiService = get(),
            batteryTracker = get(),
            locationTracker = get(),
            networkTracker = get(),
            screenLockTracker = get()
        )
    }

    factory { CollectAndSendTelemetryUseCase(repository = get()) }
}