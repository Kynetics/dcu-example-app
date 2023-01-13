package com.kynetics.updatefactory.dcu.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kynetics.updatefactory.dcu.workers.SendConfigurationToUFClientWorker
import com.kynetics.updatefactory.dcu.workers.SendResultToUFClientWorker
import com.kynetics.updatefactory.dcu.workers.WirelessWorker

class WirelessConfigurationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Submitting works")
        WorkManager
            .getInstance(applicationContext)
            .beginWith(getRequest<SendConfigurationToUFClientWorker>())
            .then(getRequest<WirelessWorker>())
            .then(getRequest<SendResultToUFClientWorker>())
            .enqueue()

        return super.onStartCommand(intent, flags, startId)
    }

    private inline fun <reified T: ListenableWorker> getRequest():OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<T>()
            .build()

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object{
        private val TAG:String = WirelessConfigurationService::class.java.simpleName
    }
}