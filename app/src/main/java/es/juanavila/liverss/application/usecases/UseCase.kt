package es.juanavila.liverss.application.usecases

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UseCase<in I : UseCase.Input,out O : UseCase.Output> {

    suspend fun execute(param: I,dispatcher: CoroutineDispatcher = Dispatchers.IO) : O = withContext(dispatcher) {
        return@withContext run(param)
    }

    protected abstract suspend fun run(param: I) : O

    open fun transactionRequired() = false

    open class Input
    open class Output
}

open class NonInput : UseCase.Input()
open class NonOutput : UseCase.Output()

