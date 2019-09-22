package es.juanavila.liverss.di

import dagger.Component
import es.juanavila.liverss.framework.tasks.AutoSearchWork
import es.juanavila.liverss.presentation.common.list.HeadlinesAdapter
import es.juanavila.liverss.presentation.main.MainActivity
import es.juanavila.liverss.presentation.favs.FavsFragment
import es.juanavila.liverss.presentation.headlines.HeadlinesFragment
import es.juanavila.liverss.presentation.settings.SettingsFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [FrameworkModule::class,DataModule::class,AppModule::class,ViewModelModule::class,
UseCaseModule::class])
interface AppComponent {
    fun inject(autoSearchWork: AutoSearchWork)
    fun inject(activity: MainActivity)
    fun inject(headlinesFragment: HeadlinesFragment)
    fun inject(favsFragment: FavsFragment)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(headlinesAdapter: HeadlinesAdapter)
}