package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class SchemeSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SchemeSyncWorker", "Starting daily recurring scheme synchronization worker task...")
        return try {
            val syncManager = SchemeSyncManager(applicationContext)
            // Perform scheme synchronization sweep
            syncManager.runSampleStateSweep()
            Log.d("SchemeSyncWorker", "Successfully finished scheme synchronization task.")
            Result.success()
        } catch (e: Exception) {
            Log.e("SchemeSyncWorker", "Error executing scheme synchronization task: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME_PERIODIC = "DailyGovernmentSchemeSyncWork"
        const val WORK_NAME_ONE_TIME = "ImmediateGovernmentSchemeSyncWork"

        fun scheduleDailySync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<SchemeSyncWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWorkRequest
            )
            Log.d("SchemeSyncWorker", "Enqueued 24-hour periodic WorkManager task: $WORK_NAME_PERIODIC")
        }

        fun triggerImmediateSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val immediateWorkRequest = OneTimeWorkRequestBuilder<SchemeSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_ONE_TIME,
                ExistingWorkPolicy.REPLACE,
                immediateWorkRequest
            )
            Log.d("SchemeSyncWorker", "Enqueued immediate one-time WorkManager task: $WORK_NAME_ONE_TIME")
        }
    }
}
