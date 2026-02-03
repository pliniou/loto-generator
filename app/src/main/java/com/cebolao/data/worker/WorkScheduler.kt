package com.cebolao.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Utilitário para agendar o worker de sincronização.
 */
object WorkScheduler {
    /**
     * Agenda a sincronização periódica.
     * Deve ser chamado na inicialização do app.
     */
    fun scheduleSync(context: Context) {
        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Precisa de internet
                .setRequiresBatteryNotLow(true) // Economia de bateria
                .build()

        val syncRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(
                repeatInterval = 12, // A cada 12 horas
                repeatIntervalTimeUnit = TimeUnit.HOURS,
            )
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Mantém o agendamento existente se já houver
            syncRequest,
        )
    }
}
