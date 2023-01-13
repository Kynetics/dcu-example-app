package com.kynetics.updatefactory.dcu.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kynetics.uf.android.update.dcu.api.DCUConfiguration
import com.kynetics.uf.android.update.dcu.api.DCUNotification
import com.kynetics.uf.android.update.dcu.api.DCUServiceNotifier
import com.kynetics.updatefactory.dcu.BuildConfig
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SendConfigurationToUFClientWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.i(TAG, "Sending dcu configuration to uf client")
        val conf = DCUConfiguration(BuildConfig.APPLICATION_ID, timeout = 10.toDuration(DurationUnit.MINUTES).toLong(DurationUnit.MILLISECONDS))
        DCUServiceNotifier.notify(applicationContext, conf)
        Log.i(TAG, "Sending notification to uf client")
        DCUServiceNotifier.notify(applicationContext, DCUNotification(
            listOf("Sent new configuration: $conf"),
            true,
            false,
            BuildConfig.APPLICATION_ID))
        return Result.success()
    }

    companion object{
        private val TAG:String = SendConfigurationToUFClientWorker::class.java.simpleName
    }
}