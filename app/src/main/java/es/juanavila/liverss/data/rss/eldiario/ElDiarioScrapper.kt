package es.juanavila.liverss.data.rss.eldiario

import es.juanavila.liverss.data.rss.NewsProvider
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.data.rss.NewspaperMetadata
import es.juanavila.liverss.application.DispatcherProvider
import org.joox.JOOX.`$` as J
import java.net.URL
import javax.inject.Inject

class ElDiarioScrapper @Inject constructor(val dispatchersProvider: DispatcherProvider) {

    private val url = "https://www.eldiario.es/rss/"

    suspend fun extractHeadlines() : List<Headline>  {

        val index = J(URL(url))
        val headlines = index.find("item")
        return headlines.map { ElDiarioHeadlineScrapper(J(it)).scrap() }
    }

    fun extractMetadata(): NewspaperMetadata {
        return NewspaperMetadata(
            -1,
            NewsProvider.EL_DIARIO

        )
    }

}