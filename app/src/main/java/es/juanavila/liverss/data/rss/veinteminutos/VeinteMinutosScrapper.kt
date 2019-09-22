package es.juanavila.liverss.data.rss.veinteminutos

import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.DispatcherProvider
import org.joox.JOOX.`$` as J
import java.net.URL
import javax.inject.Inject


class VeinteMinutosScrapper @Inject constructor(val dispatchersProvider: DispatcherProvider) {

    private val url = "https://www.20minutos.es/rss/"

    suspend fun extractHeadlines() : List<Headline>  {
        val index = J(URL(url))
        val headlines = index.find("item")
        return headlines.map{VeinteMinutosHeadlineScrapper(J(it)).scrap()}
    }

}