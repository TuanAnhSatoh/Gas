package com.example.gas.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gas.di.RepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import timber.log.Timber

class NetworkSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        RepositoryEntryPoint::class.java
    )
    private val userRepository = entryPoint.userRepository()
    private val emergencyRepository = entryPoint.emergencyRepository()
    private val alertRepository = entryPoint.alertRepository()


    override suspend fun doWork(): Result {
        val userId = userRepository.getCurrentUserId()
        if (userId == null) {
            Timber.tag("NetworkSyncWorker").e("No CurrentId found. Skipping sync.")
            return Result.failure()
        }

        Timber.tag("NetworkSyncWorker").d("Sync Data Firebase and Room for userId: $userId")
        return try {
            userRepository.refresh()
            emergencyRepository.refresh()
            alertRepository.refresh()

            Result.success()
        } catch (e: Exception) {
            Timber.tag("NetworkSyncWorker").e(e, "Error during sync")
            Result.failure()
        }
    }
}