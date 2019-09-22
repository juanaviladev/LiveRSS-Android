package es.juanavila.liverss.data.rss.diarioas

import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.DispatcherProvider
import org.joox.JOOX.`$` as J
import java.net.URL
import javax.inject.Inject


class DiarioAsScrapper @Inject constructor(val dispatchersProvider: DispatcherProvider) {

    private val url = "https://as.com/rss/tags/ultimas_noticias.xml"

    suspend fun extractHeadlines() : List<Headline>  {
        val index = J(URL(url))
        val headlines = index.find("item")
        return headlines.map{ DiarioAsHeadlineScrapper(J(it)).scrap()}
    }

}