package es.juanavila.liverss.application.usecases.autoclean

import es.juanavila.liverss.domain.HeadlinesRepository
import es.juanavila.liverss.application.services.SettingsVault
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCase
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RunAutoClean @Inject constructor(
    private val repository: HeadlinesRepository,
    private val vault: SettingsVault
) :
    UseCase<NonInput, RunAutoClean.Output>() {

    override suspend fun run(param: NonInput): Output {
        val autoCleanCriteria : String? = vault["auto_clean_criteria"]
        return autoCleanCriteria?.let {
            val deletedIds = repository.deleteAllOlderThan(it.toLong(),TimeUnit.MINUTES)
            val time = DateTime.now(DateTimeZone.UTC)
            vault["last_auto_clean_time_utc"] = time.toString()
            if(deletedIds.isEmpty()) {
                return@let Output(0, 0)
            }
            Output(
                deletedIds.last(), deletedIds.first()
            )
        } ?: Output(0, 0)
    }

    data class Output(
        val fromId: Long,
        val toId: Long
    ) : UseCase.Output()
}