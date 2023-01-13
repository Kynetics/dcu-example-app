package com.kynetics.updatefactory.dcu.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kynetics.uf.android.update.dcu.api.DCUResult
import com.kynetics.uf.android.update.dcu.api.DCUServiceNotifier
import com.kynetics.updatefactory.dcu.BuildConfig

class SendResultToUFClientWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        DCUServiceNotifier.notify(applicationContext, DCUResult(
            true,
            listOf("Successfully changed the access point"),
            BuildConfig.APPLICATION_ID))
        return Result.success()
    }
}