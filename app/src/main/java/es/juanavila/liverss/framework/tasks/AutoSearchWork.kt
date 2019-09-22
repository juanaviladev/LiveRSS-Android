package es.juanavila.liverss.framework.tasks

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import es.juanavila.liverss.application.services.EventBus
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCaseInvoker
import es.juanavila.liverss.application.usecases.autoclean.RunAutoClean
import es.juanavila.liverss.application.usecases.autosearch.RunAutoSearch
import es.juanavila.liverss.application.usecases.headlines.AutoCleaned
import es.juanavila.liverss.application.usecases.headlines.LiveNews
import es.juanavila.liverss.application.usecases.preferences.GetUserNotificationPreferences
import es.juanavila.liverss.application.usecases.preferences.GetUserSearchPreferences
import es.juanavila.liverss.application.usecases.preferences.GetUserSourcesPreferences
import es.juanavila.liverss.framework.App
import javax.inject.Inject


class AutoSearchWork(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var getUserSearchPreferences: GetUserSearchPreferences

    @Inject
    lateinit var getUserSourcesPreferences: GetUserSourcesPreferences

    @Inject
    lateinit var notificationPreferences: GetUserNotificationPreferences

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var runAutoSearch: RunAutoSearch

    @Inject
    lateinit var runAutoClean: RunAutoClean

    @Inject
    lateinit var useCaseInvoker: UseCaseInvoker

    init {
        App.appComponent.inject(this)
    }

    override suspend fun doWork(): Result {
        try {
            val searchPreferences = useCaseInvoker.run(getUserSearchPreferences,
                NonInput()
            ).userSearchPreferences
            val sourcesPreferences = useCaseInvoker.run(getUserSourcesPreferences,
                NonInput()
            ).userSourcesPreferences
            val notificationPreferences = useCaseInvoker.run(notificationPreferences,
                NonInput()
            ).userNotificationPreferences

            val result = useCaseInvoker.run(runAutoSearch,
                RunAutoSearch.Input(searchPreferences, sourcesPreferences, notificationPreferences))

            if(searchPreferences.autoCleanEnabled) {
                val result = useCaseInvoker.run(runAutoClean,
                    NonInput()
                )
                if(result.fromId != 0L && result.toId != 0L)
                    eventBus.send(
                        AutoCleaned(
                            result.fromId,
                            result.toId
                        )
                    )
            }

            eventBus.send(LiveNews(result.newNewsNumber))

            return Result.success()
        }
        catch (e : Exception) {
            Log.e("AutoSearchWork",e.message,e)
            return Result.failure()
        }
    }


}