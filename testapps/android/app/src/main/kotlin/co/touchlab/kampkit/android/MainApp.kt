package co.touchlab.kampkit.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import co.touchlab.kampkit.android.models.BreedViewModel
import co.touchlab.kmmbridgekickstart.Analytics
import co.touchlab.kmmbridgekickstart.repository.BreedRepository
import co.touchlab.kmmbridgekickstart.startSDK
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val analytics = object : Analytics{
            override fun sendEvent(eventName: String, eventArgs: Map<String, Any>) {
                println("eventName: ${eventName}, eventArgs: ${eventArgs.keys.joinToString(",") { key -> "[$key, ${eventArgs[key]}]" }}")
            }
        }
        val sdkHandle = startSDK(analytics, this)
        val koinApplication = startKoin {
            modules(
                module {
                    single<Context> { this@MainApp }
                    single<BreedRepository> { sdkHandle.breedRepository }
                    viewModel { BreedViewModel(get()) }
                    single<SharedPreferences> {
                        get<Context>().getSharedPreferences("KAMPSTARTER_SETTINGS", Context.MODE_PRIVATE)
                    }
                    single {
                        { Log.i("Startup", "Hello from Android/Kotlin!") }
                    }
                }
            )
        }

    }
}