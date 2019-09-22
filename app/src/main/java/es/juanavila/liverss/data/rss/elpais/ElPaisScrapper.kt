package es.juanavila.liverss.data.rss.elpais

import es.juanavila.liverss.data.rss.NewsProvider
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.data.rss.NewspaperMetadata
import es.juanavila.liverss.application.DispatcherProvider
import org.jsoup.Jsoup
import java.net.URL
import java.util.*
import javax.inject.Inject
import org.joox.JOOX.`$` as J

class ElPaisScrapper @Inject constructor(private val dispatchersProvider: DispatcherProvider) {

    private val url = "https://ep00.epimg.net/rss/tags/ultimas_noticias.xml"

    fun extractHeadlines() : List<Headline>  {

        val index = J(URL(url))
        val headlines = index.find("item")
        return headlines.map { ElPaisHeadlineScrapper(J(it)).scrap() }
    }

    fun extractMetadata(): NewspaperMetadata {
        val index = Jsoup.parse(URL(url),0)
        val lastUpdatedRaw = index.selectFirst("span.cabecera-fecha-actualizado").ownText().split(":")

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY,lastUpdatedRaw[0].toInt())
        calendar.set(Calendar.MINUTE,lastUpdatedRaw[1].toInt())
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)

        return NewspaperMetadata(
            calendar.timeInMillis,
            NewsProvider.EL_PAIS
        )
    }

}