package com.kynetics.updatefactory.dcu.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kynetics.updatefactory.dcu.workers.TasksWorker

class DCUMinimalAppService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Submitting works")
        WorkManager
            .getInstance(applicationContext)
            .beginWith(
                OneTimeWorkRequestBuilder<TasksWorker>().build())
            .enqueue()
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object{
        private val TAG:String = DCUMinimalAppService::class.java.simpleName
    }
}