package es.juanavila.liverss.application.usecases.autoclean

import es.juanavila.liverss.application.services.SettingsVault
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCase
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import javax.inject.Inject

class GetLastAutoCleanTime @Inject constructor(
    private val vault: SettingsVault
) :
    UseCase<NonInput, GetLastAutoCleanTime.Output>() {

    override suspend fun run(param: NonInput): Output {
        val txtTime : String? = vault["last_auto_clean_time_utc"]
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