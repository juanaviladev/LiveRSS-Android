package es.juanavila.liverss.application.usecases

interface UseCaseInvoker {
    suspend fun <I : UseCase.Input, O : UseCase.Output> run(useCase: UseCase<I, O>, param: I): O
}