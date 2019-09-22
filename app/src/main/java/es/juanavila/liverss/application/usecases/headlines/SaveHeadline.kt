package es.juanavila.liverss.application.usecases.headlines

import es.juanavila.liverss.domain.HeadlinesRepository
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.usecases.UseCase
import javax.inject.Inject

class SaveHeadline @Inject constructor(private val repository: HeadlinesRepository) :
    UseCase<SaveHeadline.Input, SaveHeadline.Output>() {

    override suspend fun run(param: Input): Output {
        val savedEntity = repository.save(param.headline)
        return Output(savedEntity)
    }

    data class Input(
        val headline : Headline
    ) : UseCase.Input()

    data class Output(
        val headline : Headline
    ) : UseCase.Output()

}
