package com.kynetics.updatefactory.dcu.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kynetics.uf.android.update.dcu.api.DCUResult
import com.kynetics.uf.android.update.dcu.api.DCUServiceNotifier
import com.kynetics.updatefactory.dcu.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class TasksWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams){
    override fun doWork(): Result {
        //do your tasks
        //simulate task execution
        Log.i(TAG, "doWork")
        Log.i(TAG, "Executing tasks")
        Thread.sleep(1000)
        Log.i(TAG, "Tasks executed")
        //notify result
        DCUServiceNotifier.notify(applicationContext, DCUResult(true, listOf(), BuildConfig.APPLICATION_ID))
        Log.i(TAG, "Result notified")
        return Result.success()
    }

    companion object{
        private val TAG = TasksWorker::class.java.simpleName
    }
}