package es.juanavila.liverss.application

import es.juanavila.liverss.application.usecases.UseCase

class Transactional<I : UseCase.Input, O : UseCase.Output>(
    private val usecase: UseCase<I, O>,
    private val session: TransactionalSession
) : UseCase<I, O>() {

    override suspend fun run(param: I): O = session.invoke {
        if(it == null)
            return@invoke usecase.execute(param)
        else
            return@invoke usecase.execute(param,it)
    }

}