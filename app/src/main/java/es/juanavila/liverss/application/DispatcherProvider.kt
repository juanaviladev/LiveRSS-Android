package es.juanavila.liverss.application

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val IO : CoroutineDispatcher
    val Main : CoroutineDispatcher
    val Default : CoroutineDispatcher
    val Unconfined : CoroutineDispatcher
}