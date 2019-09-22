package es.juanavila.liverss.data.rss.elconfidencial

import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.DispatcherProvider
import kotlinx.coroutines.*
import org.joox.JOOX.`$` as J
import java.net.URL
import javax.inject.Inject

class ElConfidencialScrapper @Inject constructor(val dispatchersProvider: DispatcherProvider) {

    private val urls = listOf(
        "https://rss.elconfidencial.com/espana/",
        "https://rss.elconfidencial.com/mundo/",
        "https://rss.elconfidencial.com/comunicacion/",
        "https://rss.elconfidencial.com/sociedad/")

    suspend fun extractHeadlines(): List<Headline> = withContext(Dispatchers.IO) {
        return@withContext urls.map {url ->
            val index = J(URL(url))
            val headlines = index.find("entry")
            headlines.map {ElConfidencialHeadlineScrapper(J(it)).scrap() }
        }.flatten()
    }

}