package es.juanavila.liverss.data.rss.elconfidencial

import es.juanavila.liverss.data.rss.NewsProvider
import es.juanavila.liverss.domain.Headline
import org.joox.Match
import org.jsoup.Jsoup
import java.net.URL

class ElConfidencialHeadlineScrapper(
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
            provider = NewsProvider.EL_CONFIDENCIAL.name,
            tags = tags
        )
    }

    private fun extractTags(): List<String> {
        return listOf("Otros")
    }

    private fun extractId(): String {
        return headline.find("id").text()
    }

    private fun extractImage(): String {
        val cdata = headline.find("content").matchAttr("type", "image/jpeg").last()
        return if (cdata.isEmpty) {
            return try {
                val newsPage = Jsoup.parse(URL(extractUrl()), 0)
                newsPage.selectFirst(".news-header-img-figure img").attr("src")
            } catch (e: Exception) {
                return PLACEHOLDER_IMG
            }
        } else {
            cdata.attr("url")
        }
    }

    private fun extractTitle(): String {
        val a = headline.find("title").text()
        return a
    }

    private fun extractUrl(): String {
        val a = headline.find("link").attr("href")
        return a
    }

    private fun extractAuthor(): String {
        val a = headline.find("author").find("name")
        return a.text()
    }

}