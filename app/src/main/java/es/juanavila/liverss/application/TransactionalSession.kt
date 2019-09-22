package es.juanavila.liverss.application

import kotlinx.coroutines.CoroutineDispatcher

interface TransactionalSession {
    suspend operator fun <T> invoke(callable: suspend (coroutineDispatcher: CoroutineDispatcher?) -> T) : T
}