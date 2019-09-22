package es.juanavila.liverss.application.usecases.autosearch

import es.juanavila.liverss.application.services.Constraint
import es.juanavila.liverss.application.services.TaskScheduler
import es.juanavila.liverss.framework.tasks.AutoSearchWork
import es.juanavila.liverss.application.usecases.NonOutput
import es.juanavila.liverss.application.usecases.UseCase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SchedulePeriodicAutoSearch @Inject constructor(private val taskScheduler: TaskScheduler) :
    UseCase<SchedulePeriodicAutoSearch.Input, NonOutput>() {

    override suspend fun run(param: Input): NonOutput {
        val time = param.periodInMinutes //TODO: >=15

        taskScheduler.periodic(time,TimeUnit.MINUTES,"RSS_Spider",AutoSearchWork::class.java, Constraint.InternetConnection)

        return NonOutput()
    }

    data class Input(
        val periodInMinutes : Long
    ) : UseCase.Input()

}