package es.juanavila.liverss.application.usecases.headlines

import es.juanavila.liverss.domain.HeadlinesRepository
import es.juanavila.liverss.application.Page
import es.juanavila.liverss.application.PagingOptions
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.usecases.UseCase
import javax.inject.Inject

class GetHeadlines @Inject constructor(private val repository: HeadlinesRepository) :
    UseCase<GetHeadlines.Input, GetHeadlines.Output>() {

    override suspend fun run(param: Input): Output {


        val result = repository.findAll(
            PagingOptions(param.cursor, param.sinceId)
        )
     //   delay(10000)

        return Output(
            result
        )
    }


    data class Output(
        val resultsPage: Page<Headline>
    ) : UseCase.Output()

    data class Input (
        val cursor: String = "",
        val sinceId : Long = 0
    ) : UseCase.Input()
}