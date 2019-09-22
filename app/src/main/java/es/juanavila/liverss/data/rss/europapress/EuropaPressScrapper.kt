package es.juanavila.liverss.data.rss.europapress

import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.DispatcherProvider
import org.joox.JOOX.`$` as J
import java.net.URL
import javax.inject.Inject


class EuropaPressScrapper @Inject constructor(val dispatchersProvider: DispatcherProvider) {

    private val url = "https://www.europapress.es/rss/rss.aspx"

    suspend fun extractHeadlines() : List<Headline>  {
        val index = J(URL(url))
        val headlines = index.find("item")
        return headlines.map{EuropaPressHeadlineScrapper(J(it)).scrap()}
    }

}