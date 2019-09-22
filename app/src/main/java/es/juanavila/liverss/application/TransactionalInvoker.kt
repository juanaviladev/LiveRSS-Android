package es.juanavila.liverss.application

import es.juanavila.liverss.application.usecases.UseCase
import es.juanavila.liverss.application.usecases.UseCaseInvoker
import javax.inject.Inject

class TransactionalInvoker @Inject constructor(private val session: TransactionalSession) :
    UseCaseInvoker {

    override suspend fun <I : UseCase.Input,O : UseCase.Output> run(useCase: UseCase<I, O>, param : I): O {
        return if(useCase.transactionRequired())
            Transactional(useCase, session).execute(param)
        else
            useCase.execute(param)
    }

}