package es.juanavila.liverss.data.rss.veinteminutos

import es.juanavila.liverss.data.rss.NewsProvider
import es.juanavila.liverss.domain.Headline
import org.joox.Match
import org.jsoup.Jsoup

class VeinteMinutosHeadlineScrapper(
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
            provider = NewsProvider._20_MINUTOS.name,
            tags = tags
        )
    }

    private fun extractTags(): List<String> {
        return listOf("Otros")
    }

    private fun extractId(): String {
        return headline.attr("about")
    }

    private fun extractImage(): String {
        val newsPage = Jsoup.parse(headline.find("description").text())
        val img = newsPage.selectFirst("img")
        return if(img != null) img.attr("src") else PLACEHOLDER_IMG
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
        val a = headline.find("creator")
        return a.text()
    }

}