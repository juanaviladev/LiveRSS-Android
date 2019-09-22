package es.juanavila.liverss.di

import dagger.Module
import dagger.Provides
import es.juanavila.liverss.data.SQLiteTransactionalSession
import es.juanavila.liverss.data.SimpleHeadlinesRepository
import es.juanavila.liverss.domain.HeadlinesRepository
import es.juanavila.liverss.application.TransactionalSession

@Module
class DataModule {

    @Provides
    fun headlinesRepo(headlinesRepository: SimpleHeadlinesRepository): HeadlinesRepository {
        return headlinesRepository
    }

    @Provides
    fun transactionalSession(transactionalSession: SQLiteTransactionalSession) : TransactionalSession = transactionalSession

}