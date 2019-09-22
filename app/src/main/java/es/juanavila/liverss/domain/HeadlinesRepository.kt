package es.juanavila.liverss.domain

import es.juanavila.liverss.application.Page
import es.juanavila.liverss.application.PagingOptions
import java.util.concurrent.TimeUnit

interface HeadlinesRepository {
    suspend fun save(entity: Headline) : Headline
    suspend fun findAll(options: PagingOptions): Page<Headline>
    suspend fun findFavorites(options: PagingOptions): Page<Headline>
    suspend fun deleteAllOlderThan(time: Long, unit: TimeUnit) : List<Long>
    suspend fun findAll(): List<Headline>
    suspend fun deleteAll()
}