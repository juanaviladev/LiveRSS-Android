package es.juanavila.liverss.data

import android.util.Base64
import es.juanavila.liverss.application.Page
import es.juanavila.liverss.application.PagingOptions
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.data.db.SQLiteHeadlinesDB
import es.juanavila.liverss.domain.HeadlinesRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SimpleHeadlinesRepository @Inject constructor(private val headlinesDB: SQLiteHeadlinesDB) :
    HeadlinesRepository {

    override suspend fun findFavorites(options: PagingOptions): Page<Headline> {
        val decodedCursor = String(Base64.decode(options.cursor,Base64.DEFAULT))
        println("$decodedCursor | $options")
        when {
            decodedCursor.contains("after") -> {
                val pagingOptions = decodedCursor.split(",")

                val cursorMaxId = pagingOptions[0].toLong()

                //Sacamos las solicitadas, mas una, que nos sirve para saber si alguna mas antigua
                var pageNews = headlinesDB.allNewerOrEqualThanIdFavorites(cursorMaxId,options.count + 1)
                println(pageNews)

                //Comprobamos si hay posteriores (mas nuevas)
                val occurredAfterCursor = if(pageNews.size > options.count) {
                    val maxId = pageNews.first().id
                    Base64.encodeToString("$maxId,after".toByteArray(),Base64.DEFAULT)
                }
                else "0"

                //Comprobamos si hay anterior (mas antiguas)
                val beforeNews = headlinesDB.allOlderOrEqualThanIdFavorites(cursorMaxId,options.count)

                val occurredBeforeCursor = if(beforeNews.size > 1) {
                    val maxId = beforeNews.last().id;
                    Base64.encodeToString("$maxId,before".toByteArray(),Base64.DEFAULT)
                } else "0"


                pageNews = if(pageNews.size > options.count) {
                    pageNews.subList(0,pageNews.lastIndex)
                }
                else pageNews

                return Page(
                    pageNews,
                    beforeCursor = occurredBeforeCursor,
                    afterCursor = occurredAfterCursor
                )
            }
            decodedCursor.contains("before") -> {
                val pagingOptions = decodedCursor.split(",")

                val cursorMaxId = pagingOptions[0].toLong()

                //Sacamos las solicitadas
                var pageNews = headlinesDB.allOlderOrEqualThanIdFavorites(cursorMaxId,options.count + 1).filter { it.id > options.sinceId }

                //Se queda en 0 cuando no hay más que "count", esto falla para el caso de estar
                //haciendo scroll hacia abajo en medio de la lista (quizás haya que eliminar el cursor para los casos intermedios)
                //no guardarlo

                //Comprobamos si hay mas antiguas
                val occurredBeforeCursor = if(pageNews.size > options.count) {
                    val maxId = pageNews.last().id
                    Base64.encodeToString("$maxId,before".toByteArray(),Base64.DEFAULT)
                }
                else "0"

                val ocurredAfterNews = headlinesDB.allNewerOrEqualThanIdFavorites(cursorMaxId,options.count)
                //Comprobamos si hay mas nuevas
                val occurredAfterCursor = if(ocurredAfterNews.size > 1) {
                    val maxId = ocurredAfterNews[ocurredAfterNews.lastIndex - 1].id
                    Base64.encodeToString("$maxId,after".toByteArray(),Base64.DEFAULT)
                }
                else "0"

                pageNews = if(pageNews.size > options.count) {
                    pageNews.subList(0,pageNews.lastIndex)
                }
                else pageNews

                return Page(
                    pageNews,
                    beforeCursor = occurredBeforeCursor,
                    afterCursor = occurredAfterCursor
                )
            }
            else -> {
                val mostRecent = headlinesDB.allFromNewestToOldestFavorites(options.count + 1).filter { it.id > options.sinceId }

                //Comprobamos si hay mas antiguas
                val occurredBeforeCursor = if(mostRecent.size > options.count) {
                    val maxId = mostRecent.last().id
                    Base64.encodeToString("$maxId,before".toByteArray(),Base64.DEFAULT)
                }
                else "0"

                val pageNews = if(mostRecent.size > options.count) {
                    mostRecent.subList(0,mostRecent.lastIndex)
                }
                else mostRecent

                return Page(
                    pageNews,
                    beforeCursor = occurredBeforeCursor,
                    afterCursor = "0"
                )
            }
        }
    }

    override suspend fun findAll(options: PagingOptions): Page<Headline> {
        val decodedCursor = String(Base64.decode(options.cursor,Base64.DEFAULT))
        println("$decodedCursor | $options")
        when {
            decodedCursor.contains("after") -> {
                val pagingOptions = decodedCursor.split(",")

                val cursorMaxId = pagingOptions[0].toLong()

                //Sacamos las solicitadas, mas una, que nos sirve para saber si alguna mas antigua
                var pageNews = headlinesDB.allNewerOrEqualThanId(cursorMaxId,options.count + 1)
                println(pageNews)

                //Comprobamos si hay posteriores (mas nuevas)
                val occurredAfterCursor = if(pageNews.size > options.count) {
                    val maxId = pageNews.first().id
                    Base64.encodeToString("$maxId,after".toByteArray(),Base64.DEFAULT)
                }
                else "0"

                //Comprobamos si hay anterior (mas antiguas)
                val beforeNews = headlinesDB.allOlderOrEqualThanId(cursorMaxId,options.count)

                val occurredBeforeCursor = if(beforeNews.size > 1) {
                    val maxId = beforeNews.last().id;
                    Base64.encodeToString("$maxId,before".toByteArray(),Base64.DEFAULT)
                } else "0"


                pageNews = if(pageNews.size > options.count) {
                    pageNews.subList(0,pageNews.lastIndex)
                }
                else pageNews

                return Page(
                    pageNews,
                    beforeCursor = occurredBeforeCursor,
                    afterCursor = occurredAfterCursor
                )
            }
            decodedCursor.contains("before") -> {
                val pagingOptions = decodedCursor.split(",")

                val cursorMaxId = pagingOptions[0].toLong()

                //Sacamos las solicitadas
                var pageNews = headlinesDB.allOlderOrEqualThanId(cursorMaxId,options.count + 1).filter { it.id > options.sinceId }

                //Se queda en 0 cuando no hay más que "count", esto falla para el caso de estar
                //haciendo scroll hacia abajo en medio de la lista (quizás haya que eliminar el cursor para los casos intermedios)
                //no guardarlo

                //Comprobamos si hay mas antiguas
                val occurredBeforeCursor = if(pageNews.size > options.count) {
                    val maxId = pageNews.last().id
                    Base64.encodeToString("$maxId,before".toByteArray(),Base64.DEFAULT)
                }
                else "0"

                val ocurredAfterNews = headlinesDB.allNewerOrEqualThanId(cursorMaxId,options.count)
                //Comprobamos si hay mas nuevas
                val occurredAfterCursor = if(ocurredAfterNews.size > 1) {
                    val maxId = ocurredAfterNews[ocurredAfterNews.lastIndex - 1].id
                    Base64.encodeToString("$maxId,after".toByteArray(),Base64.DEFAULT)
                }
                else "0"

                pageNews = if(pageNews.size > options.count) {
                    pageNews.subList(0,pageNews.lastIndex)
                }
                else pageNews

                return Page(
                    pageNews,
                    beforeCursor = occurredBeforeCursor,
                    afterCursor = occurredAfterCursor
                )
            }
            else -> {
                val mostRecent = headlinesDB.allFromNewestToOldest(options.count + 1).filter { it.id > options.sinceId }

                //Comprobamos si hay mas antiguas
                val occurredBeforeCursor = if(mostRecent.size > options.count) {
                    val maxId = mostRecent.last().id
                    Base64.encodeToString("$maxId,before".toByteArray(),Base64.DEFAULT)
                }
                else "0"

                val pageNews = if(mostRecent.size > options.count) {
                    mostRecent.subList(0,mostRecent.lastIndex)
                }
                else mostRecent

                return Page(
                    pageNews,
                    beforeCursor = occurredBeforeCursor,
                    afterCursor = "0"
                )
            }
        }
    }

    override suspend fun save(entity: Headline): Headline {
        val savedHeadline = headlinesDB.read(entity.id)
        return if (savedHeadline != null) {
            headlinesDB.update(entity)
            savedHeadline
        } else {
            headlinesDB.create(entity)
        }
    }

    override suspend fun findAll(): List<Headline> = headlinesDB.allFromNewestToOldest(limit = Int.MAX_VALUE)

    override suspend fun deleteAllOlderThan(time: Long, unit: TimeUnit) : List<Long> {
        return headlinesDB.deleteAllOlderThan(unit.toSeconds(time))
    }
    override suspend fun deleteAll() {
        headlinesDB.deleteAll()
    }
}