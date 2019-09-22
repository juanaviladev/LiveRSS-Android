package es.juanavila.liverss.data.rss.elmundo

import es.juanavila.liverss.data.rss.NewsProvider
import es.juanavila.liverss.domain.Headline
import org.joox.Match
import org.jsoup.Jsoup
import java.net.URL

class ElMundoHeadlineScrapper(
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
            provider = NewsProvider.EL_MUNDO.name,
            tags = tags
        )
    }

    private fun extractTags(): List<String> {
        val categories = headline.find("category")
        return categories.texts()
    }

    private fun extractId(): String {
        return headline.find("guid").text()
    }

    private fun extractImage(): String {
        val cdata = headline.find("content")
        return if(cdata.isEmpty) {
            val newsPage = Jsoup.parse(URL(extractUrl()),0)
            val img = newsPage.selectFirst("img.ue-c-article__media--image")
            return if(img != null) img.attr("src") else PLACEHOLDER_IMG
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
        val a = headline.find("creator")
        return a.text()
    }

}