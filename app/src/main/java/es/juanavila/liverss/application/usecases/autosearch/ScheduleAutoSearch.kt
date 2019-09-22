package es.juanavila.liverss.application.usecases.autosearch

import es.juanavila.liverss.application.services.Constraint
import es.juanavila.liverss.application.services.TaskScheduler
import es.juanavila.liverss.framework.tasks.AutoSearchWork
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.NonOutput
import es.juanavila.liverss.application.usecases.UseCase
import javax.inject.Inject

class ScheduleAutoSearch @Inject constructor(private val taskScheduler: TaskScheduler) :
    UseCase<NonInput, NonOutput>() {

    override suspend fun run(param: NonInput): NonOutput {
        taskScheduler.enqueue("RSS_Spider_unique",AutoSearchWork::class.java, Constraint.InternetConnection)
        return NonOutput()
    }

}