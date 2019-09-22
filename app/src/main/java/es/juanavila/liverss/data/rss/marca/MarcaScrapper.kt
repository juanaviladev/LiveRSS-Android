package es.juanavila.liverss.data.rss.marca

import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.DispatcherProvider
import org.joox.JOOX.`$` as J
import java.net.URL
import javax.inject.Inject


class MarcaScrapper @Inject constructor(val dispatchersProvider: DispatcherProvider) {

    private val url = "https://e00-marca.uecdn.es/rss/portada.xml"

    suspend fun extractHeadlines() : List<Headline>  {
        val index = J(URL(url))
        val headlines = index.find("item")
        return headlines.map{ MarcaHeadlineScrapper(J(it)).scrap()}
    }

}