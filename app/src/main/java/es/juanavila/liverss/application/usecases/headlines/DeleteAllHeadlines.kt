package es.juanavila.liverss.application.usecases.headlines

import es.juanavila.liverss.application.services.EventBus
import es.juanavila.liverss.domain.HeadlinesRepository
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.NonOutput
import es.juanavila.liverss.application.usecases.UseCase
import javax.inject.Inject

class DeleteAllHeadlines @Inject constructor(private val eventBus: EventBus, private val repository: HeadlinesRepository) :
    UseCase<NonInput, NonOutput>() {

    override suspend fun run(param: NonInput): NonOutput {
        repository.deleteAll()
        eventBus.send(DeletedAll)
        return NonOutput()
    }

}
