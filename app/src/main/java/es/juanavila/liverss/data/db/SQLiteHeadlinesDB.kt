package es.juanavila.liverss.data.db

import android.app.Application
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.WorkerThread
import androidx.core.database.sqlite.transaction
import es.juanavila.liverss.domain.Headline
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import javax.inject.Inject
import javax.inject.Singleton


private const val VERSION = 1
private const val DB_NAME = "updates.db"

@WorkerThread
@Singleton
class SQLiteHeadlinesDB @Inject constructor(ctx: Application) : SQLiteOpenHelper(ctx,DB_NAME,null,VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE headline (id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp TEXT NOT NULL, provider_id TEXT UNIQUE NOT NULL, headline TEXT NOT NULL, author TEXT NOT NULL, url TEXT NOT NULL, image TEXT NOT NULL, provider TEXT NOT NULL,read BOOLEAN NOT NULL, shown BOOLEAN NOT NULL, liked BOOLEAN NOT NULL)")
    }

    init {
        println("NEW DB!!!")
    }

    fun create(entity: Headline): Headline {
        val values = ContentValues()

        values.put("provider_id",entity.providerId)
        values.put("headline",entity.headline)
        values.put("author",entity.author)
        values.put("url",entity.url)
        values.put("image",entity.image)
        values.put("provider",entity.provider)
        values.put("read",entity.hasBeenRead)
        values.put("shown",entity.hasBeenShown)
        values.put("timestamp",entity.timestamp.toString())
        values.put("liked",entity.liked)

        val generatedId = writableDatabase.insertOrThrow("headline", null, values)

        return Headline(
            id = generatedId,
            providerId = entity.providerId,
            headline = entity.headline,
            author = entity.author,
            url = entity.url,
            image = entity.image,
            provider = entity.provider,
            hasBeenRead = entity.hasBeenRead,
            hasBeenShown = entity.hasBeenShown,
            tags = entity.tags
        )
    }

    fun allFromNewestToOldest(limit: Int = 5): List<Headline> {
        return allFromNewestToOldest(limit,false)
    }

    private fun allFromNewestToOldest(limit: Int = 5, onlyFavs :Boolean): List<Headline> {
        val cursor = if(onlyFavs) {readableDatabase.rawQuery("SELECT * FROM headline WHERE liked = 1 ORDER BY headline.id DESC LIMIT $limit",null)}
        else {readableDatabase.rawQuery("SELECT * FROM headline ORDER BY headline.id DESC LIMIT $limit",null)}
        val entities = mutableListOf<Headline>()

        while(cursor.moveToNext()) {
            entities += Headline(
                cursor.getLong(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("provider_id")),
                cursor.getString(cursor.getColumnIndex("headline")),
                cursor.getString(cursor.getColumnIndex("author")),
                cursor.getString(cursor.getColumnIndex("url")),
                cursor.getString(cursor.getColumnIndex("image")),
                cursor.getString(cursor.getColumnIndex("provider")),
                cursor.getInt(cursor.getColumnIndex("read")) == 1,
                cursor.getInt(cursor.getColumnIndex("shown")) == 1,
                emptyList(),
                ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(
                    cursor.getString(
                        cursor.getColumnIndex("timestamp")
                    )
                ),
                cursor.getInt(cursor.getColumnIndex("liked")) == 1
            )
        }

        cursor.close()
        return entities
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {}

    fun read(id: Long): Headline? {
        val cursor = readableDatabase.rawQuery("SELECT headline.* FROM headline WHERE headline.id = $id", null)
        var entity : Headline? = null

        if(cursor.moveToNext()){
            entity = Headline(
                cursor.getLong(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("provider_id")),
                cursor.getString(cursor.getColumnIndex("headline")),
                cursor.getString(cursor.getColumnIndex("author")),
                cursor.getString(cursor.getColumnIndex("url")),
                cursor.getString(cursor.getColumnIndex("image")),
                cursor.getString(cursor.getColumnIndex("provider")),
                cursor.getInt(cursor.getColumnIndex("read")) == 1,
                cursor.getInt(cursor.getColumnIndex("shown")) == 1,
                emptyList(),
                ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(
                    cursor.getString(
                        cursor.getColumnIndex("timestamp")
                    )
                ),
                cursor.getInt(cursor.getColumnIndex("liked")) == 1
            )
        }

        cursor.close()
        return entity
    }


    fun update(entity: Headline) {
        val values = ContentValues()

        values.put("provider_id",entity.providerId)
        values.put("headline",entity.headline)
        values.put("author",entity.author)
        values.put("url",entity.url)
        values.put("image",entity.image)
        values.put("provider",entity.provider)
        values.put("read",entity.hasBeenRead)
        values.put("shown",entity.hasBeenShown)
        values.put("timestamp",entity.timestamp.toString())
        values.put("liked",entity.liked)

        val updatedRows = writableDatabase.update("headline",values,"id = ${entity.id}",null)

    }

    suspend fun <T> transaction(block: suspend (transactionDispatcher: CoroutineDispatcher) -> T) : T {
        val dispatcher = newSingleThreadContext("DB-Thread")
        return withContext(dispatcher) {
            writableDatabase.transaction { block(dispatcher) }
        }
    }

    fun allOlderOrEqualThanId(id: Long, limit: Int): List<Headline> = allOlderOrEqualThanId(id,limit,false)

    private fun allOlderOrEqualThanId(id: Long, limit: Int,favs : Boolean): List<Headline> {
        val cursor = if(favs) {readableDatabase.rawQuery("SELECT * FROM headline WHERE headline.id <= $id AND liked = 1 ORDER BY headline.id DESC LIMIT $limit",null)}
        else {readableDatabase.rawQuery("SELECT * FROM headline WHERE headline.id <= $id ORDER BY headline.id DESC LIMIT $limit",null)}

        val entities = mutableListOf<Headline>()

        while(cursor.moveToNext()) {
            entities += Headline(
                cursor.getLong(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("provider_id")),
                cursor.getString(cursor.getColumnIndex("headline")),
                cursor.getString(cursor.getColumnIndex("author")),
                cursor.getString(cursor.getColumnIndex("url")),
                cursor.getString(cursor.getColumnIndex("image")),
                cursor.getString(cursor.getColumnIndex("provider")),
                cursor.getInt(cursor.getColumnIndex("read")) == 1,
                cursor.getInt(cursor.getColumnIndex("shown")) == 1,
                emptyList(),
                ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(
                    cursor.getString(
                        cursor.getColumnIndex("timestamp")
                    )
                ),
                cursor.getInt(cursor.getColumnIndex("liked")) == 1
            )
        }

        cursor.close()
        return entities
    }

    private fun allNewerOrEqualThanId(id: Long, limit: Int, favs : Boolean): List<Headline> {
        val cursor = if(favs) {readableDatabase.rawQuery("SELECT * FROM headline WHERE headline.id >= $id AND liked = 1 ORDER BY headline.id ASC LIMIT $limit",null)}
        else {readableDatabase.rawQuery("SELECT * FROM headline WHERE headline.id >= $id ORDER BY headline.id ASC LIMIT $limit",null)}

        val entities = mutableListOf<Headline>()

        while(cursor.moveToNext()) {
            entities += Headline(
                cursor.getLong(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("provider_id")),
                cursor.getString(cursor.getColumnIndex("headline")),
                cursor.getString(cursor.getColumnIndex("author")),
                cursor.getString(cursor.getColumnIndex("url")),
                cursor.getString(cursor.getColumnIndex("image")),
                cursor.getString(cursor.getColumnIndex("provider")),
                cursor.getInt(cursor.getColumnIndex("read")) == 1,
                cursor.getInt(cursor.getColumnIndex("shown")) == 1,
                emptyList(),
                ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(
                    cursor.getString(
                        cursor.getColumnIndex("timestamp")
                    )
                ),
                cursor.getInt(cursor.getColumnIndex("liked")) == 1
            )
        }

        cursor.close()
        return entities
    }

    fun allNewerOrEqualThanId(id: Long, limit: Int): List<Headline> {
        return allNewerOrEqualThanId(id,limit,false)
    }

    fun deleteAllOlderThan(seconds: Long): List<Long> {
        val cursor = readableDatabase.rawQuery("SELECT id FROM headline WHERE strftime('%s','now') - strftime('%s',headline.timestamp) > $seconds AND liked = 0 ORDER BY id DESC",null)
        val list = mutableListOf<Long>()
        while(cursor.moveToNext()){
            list.add(cursor.getLong(cursor.getColumnIndex("id")))
        }
        readableDatabase.delete("headline","strftime('%s','now') - strftime('%s',headline.timestamp) > $seconds AND liked = 0",null)
        return list
    }

    fun deleteAll() {
        writableDatabase.delete("headline","",null)
    }

    fun allNewerOrEqualThanIdFavorites(id: Long, limit: Int): List<Headline> {
        return allNewerOrEqualThanId(id,limit,true)
    }

    fun allOlderOrEqualThanIdFavorites(id: Long, limit: Int): List<Headline> {
        return allOlderOrEqualThanId(id,limit,true)
    }

    fun allFromNewestToOldestFavorites(limit: Int): List<Headline> {
        return allFromNewestToOldest(limit,true)
    }
}