package es.juanavila.liverss.data

import android.util.Log
import es.juanavila.liverss.data.db.SQLiteHeadlinesDB
import es.juanavila.liverss.application.TransactionalSession
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SQLiteTransactionalSession @Inject constructor(private val sqLiteDatabase: SQLiteHeadlinesDB) :
    TransactionalSession {

    override suspend fun <T> invoke(callable: suspend (coroutineDispatcher: CoroutineDispatcher?) -> T): T = sqLiteDatabase.transaction {
        Log.d("Transactional", "******************* Starting transaction ${Thread.currentThread().name} *******************")
        val result = callable(it)
        Log.d("Transactional", "******************* Ending transaction ${Thread.currentThread().name} *******************")
        return@transaction result
    }
}