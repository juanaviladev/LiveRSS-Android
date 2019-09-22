package es.juanavila.liverss.domain

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

data class Headline(
    val id: Long = -1,
    val providerId: String,
    val headline: String,
    val author: String,
    val url: String,
    val image: String,
    val provider: String,
    var hasBeenRead : Boolean = false,
    var hasBeenShown : Boolean = false,
    val tags : List<String> = emptyList(),
    val timestamp : DateTime = DateTime.now(DateTimeZone.UTC),
    val liked : Boolean = false
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Headline

        if (providerId != other.providerId) return false

        return true
    }

    override fun hashCode(): Int {
        return providerId.hashCode()
    }
}