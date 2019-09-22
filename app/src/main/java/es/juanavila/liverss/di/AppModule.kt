package es.juanavila.liverss.di

import android.app.Application
import dagger.Module
import dagger.Provides

@Module
class AppModule(private var sApplication: Application) {

    @Provides
    fun providesApplication(): Application {
        return sApplication
    }
}