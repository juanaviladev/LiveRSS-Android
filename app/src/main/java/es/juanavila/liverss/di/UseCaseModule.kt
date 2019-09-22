package es.juanavila.liverss.di

import dagger.Module
import dagger.Provides
import es.juanavila.liverss.application.TransactionalInvoker
import es.juanavila.liverss.application.usecases.UseCaseInvoker

@Module
class UseCaseModule() {

    @Provides
    fun useCaseInvoker(invoker: TransactionalInvoker): UseCaseInvoker = invoker
}