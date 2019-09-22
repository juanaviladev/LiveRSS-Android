package es.juanavila.liverss.data.rss.diarioas

import es.juanavila.liverss.data.rss.NewsProvider
import es.juanavila.liverss.domain.Headline
import org.joox.Match

class DiarioAsHeadlineScrapper(
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
            provider = NewsProvider.AS.name,
            tags = tags
        )
    }

    private fun extractTags(): List<String> {
        return headline.find("category").texts()
    }

    private fun extractId(): String {
        return headline.find("guid").text()
    }

    private fun extractImage(): String {
        val img = headline.find("enclosure").last()
        return if(img.isNotEmpty) img.attr("url") else PLACEHOLDER_IMG
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