package es.juanavila.liverss.di

import dagger.Module
import dagger.Provides
import es.juanavila.liverss.application.services.*
import es.juanavila.liverss.application.DispatcherProvider
import es.juanavila.liverss.framework.*
import es.juanavila.liverss.framework.event.AndroidAppStateService
import es.juanavila.liverss.framework.event.GreenRobotEventBus
import es.juanavila.liverss.framework.settings.AndroidSettingsVault
import es.juanavila.liverss.framework.storage.AndroidFileService
import es.juanavila.liverss.framework.tasks.AndroidTaskScheduler

@Module
class FrameworkModule {

    @Provides
    fun settingsVault(settingsVault: AndroidSettingsVault) : SettingsVault = settingsVault

    @Provides
    fun appStateService(appStateService: AndroidAppStateService) : AppStateService = appStateService

    @Provides
    fun eventBus(eventBus: GreenRobotEventBus) : EventBus = eventBus

    @Provides
    fun fileService(fileService: AndroidFileService) : FileService = fileService

    @Provides
    fun taskScheduler(taskScheduler: AndroidTaskScheduler) : TaskScheduler = taskScheduler

    @Provides
    fun dispatcherProvider(dispatcherProvider: KotlinDispatcherProvider) : DispatcherProvider = dispatcherProvider
}