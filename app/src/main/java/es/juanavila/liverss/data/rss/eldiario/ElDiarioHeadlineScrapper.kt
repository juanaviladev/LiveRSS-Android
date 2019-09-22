package es.juanavila.liverss.data.rss.eldiario

import es.juanavila.liverss.data.rss.NewsProvider
import es.juanavila.liverss.domain.Headline
import org.joox.Match

class ElDiarioHeadlineScrapper(
    private val headline: Match
) {

    private val PLACEHOLDER_IMG = "https://picsum.photos/500/300?blur=4"

    fun scrap() : Headline {
        val id = extractId()
        val image = extractImage()
        val title = extractTitle()
        val url = extractUrl()
        val author = extractAuthor()
        val tags = extractTags()
        return Headline(
            providerId = id,
            headline = title,
            image = image,
            author = author,
            url = url,
            provider = NewsProvider.EL_DIARIO.name,
            tags = tags
        )
    }

    private fun extractTags(): List<String> {
        return listOf("Otros")
    }

    private fun extractId(): String {
        return headline.find("guid").text()
    }

    private fun extractImage(): String {
        val cdata = headline.find("enclosure").last()
        return if(cdata.isEmpty) {
            return PLACEHOLDER_IMG
        } else {
            cdata.attr("url")
        }
    }

    private fun extractTitle(): String {
        val a = headline.find("title").text()
        return a
    }

    private fun extractUrl(): String {
        val a = headline.find("link").text()
        return a
    }

    private fun extractAuthor(): String {
        val a = headline.find("author")
        return if(a.isNotEmpty) a.text() else "ElDiario"
    }

}