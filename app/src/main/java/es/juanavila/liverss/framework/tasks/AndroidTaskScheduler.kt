package es.juanavila.liverss.framework.tasks

import android.app.Application
import androidx.work.*
import es.juanavila.liverss.application.services.Constraint
import es.juanavila.liverss.application.services.TaskScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AndroidTaskScheduler @Inject constructor(ctx: Application) : TaskScheduler {


    override fun periodic(
        time: Long,
        unit: TimeUnit,
        id: String,
        clazz: Class<*>,
        vararg taskConstraints: Constraint
    ): String {
        val workerClass: Class<ListenableWorker> = clazz as Class<ListenableWorker>

        val constraints = Constraints.Builder()

        if (taskConstraints.contains(Constraint.InternetConnection)) {
            constraints.setRequiredNetworkType(NetworkType.CONNECTED)
        }

        val uploadWorkRequest = PeriodicWorkRequest.Builder(workerClass,time,unit)
            .setInitialDelay(time,unit)
            .setConstraints(constraints.build())
            .build()

        val work = WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
            id,
            ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest
        )

        return id
    }

    override fun enqueue(id: String, clazz: Class<*>, vararg taskConstraints: Constraint): String {
        val workerClass: Class<ListenableWorker> = clazz as Class<ListenableWorker>

        val constraints = Constraints.Builder()

        if (taskConstraints.contains(Constraint.InternetConnection)) {
            constraints.setRequiredNetworkType(NetworkType.CONNECTED)
        }

        val uploadWorkRequest = OneTimeWorkRequest.Builder(workerClass)
            .setConstraints(constraints.build())
            .build()

        val work = WorkManager.getInstance(ctx).enqueueUniqueWork(
            id,
            ExistingWorkPolicy.REPLACE, uploadWorkRequest
        )

        return id
    }


    private val ctx = ctx.applicationContext

    override fun cancel(id: String) {
        WorkManager.getInstance(ctx).cancelAllWorkByTag(id)
    }
}