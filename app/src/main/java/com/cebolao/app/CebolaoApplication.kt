package com.cebolao.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.cebolao.data.initializer.DataInitializer
import com.cebolao.data.worker.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class com Hilt.
 * Inicializa dados na primeira execução e configura WorkManager.
 */
@HiltAndroidApp
class CebolaoApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var dataInitializer: DataInitializer

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Inicializar dados (assíncrono, não bloqueia)
        dataInitializer.initialize()

        // Agendar sincronização em segundo plano
        WorkScheduler.scheduleSync(this)
    }

    override val workManagerConfiguration: Configuration
        get() =
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
}
