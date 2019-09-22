package es.juanavila.liverss.data.rss.elmundo

import android.annotation.SuppressLint
import es.juanavila.liverss.data.rss.NewsProvider
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.data.rss.NewspaperMetadata
import es.juanavila.liverss.application.DispatcherProvider
import org.joda.time.DateTime
import org.joox.JOOX.`$` as J
import org.joda.time.format.ISODateTimeFormat
import org.jsoup.Jsoup
import java.net.URL
import javax.inject.Inject


class ElMundoScrapper @Inject constructor(val dispatchersProvider: DispatcherProvider) {

    private val url = "https://e00-elmundo.uecdn.es/elmundo/rss/portada.xml"

    suspend fun extractHeadlines() : List<Headline>  {

        val index = J(URL(url))
        val headlines = index.find("item")
        return headlines.map{ ElMundoHeadlineScrapper(J(it)).scrap()}
    }

    @SuppressLint("SimpleDateFormat")
    fun extractMetadata(): NewspaperMetadata {
        val index = Jsoup.parse(URL(url),0)

        //2019-08-28T13:17:21+02:00
        val lastUpdatedRaw = index.selectFirst("div.site-logo time").attr("datetime")

        val dt: DateTime =
            ISODateTimeFormat.dateTimeParser().withOffsetParsed()
                .parseDateTime(lastUpdatedRaw)

        return NewspaperMetadata(
            dt.millis,
            NewsProvider.EL_MUNDO
        )
    }

}