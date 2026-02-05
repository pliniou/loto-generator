package com.cebolao.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cebolao.app.di.IoDispatcher
import com.cebolao.domain.repository.LotteryRepository
import com.cebolao.domain.result.AppResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Worker para sincronização de dados em background.
 * Executa repository.refresh() para buscar novos concursos.
 */
@HiltWorker
class SyncWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val repository: LotteryRepository,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result =
            withContext(ioDispatcher) {
                try {
                    Log.d(TAG, "Iniciando sincronização em background...")
                    when (val result = repository.refresh()) {
                        is AppResult.Success -> {
                            Log.d(TAG, "Sincronização concluída com sucesso.")
                            Result.success()
                        }
                        is AppResult.Failure -> {
                            Log.e(TAG, "Erro na sincronização em background", result.cause)
                            if (runAttemptCount < 3) Result.retry() else Result.failure()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Erro na sincronização em background", e)
                    if (runAttemptCount < 3) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            }

        companion object {
            const val WORK_NAME = "lottery_sync_work"
            private const val TAG = "SyncWorker"
        }
    }
