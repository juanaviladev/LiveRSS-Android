package es.juanavila.liverss.framework;

import android.app.Application;
import es.juanavila.liverss.di.AppComponent
import es.juanavila.liverss.di.AppModule
import es.juanavila.liverss.di.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

}