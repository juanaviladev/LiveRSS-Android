package es.juanavila.liverss.application.usecases.autosearch

import es.juanavila.liverss.application.services.SettingsVault
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCase
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import javax.inject.Inject

class GetLastAutoSearchTime @Inject constructor(
    private val vault: SettingsVault
) :
    UseCase<NonInput, GetLastAutoSearchTime.Output>() {

    override suspend fun run(param: NonInput): Output {
        val txtTime : String? = vault["last_sync_time_utc"]
        return if(txtTime != null) {
            Output(
                DateTime.parse(txtTime, ISODateTimeFormat.dateTimeParser())
            )
        } else {
            Output()
        }
    }

    data class Output(
        val time: DateTime? = null
    ): UseCase.Output()
}