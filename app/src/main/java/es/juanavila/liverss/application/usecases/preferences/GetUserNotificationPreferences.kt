package es.juanavila.liverss.application.usecases.preferences

import es.juanavila.liverss.application.services.SettingsVault
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCase
import javax.inject.Inject

class GetUserNotificationPreferences @Inject constructor(
    private val vault: SettingsVault
) :
    UseCase<NonInput, GetUserNotificationPreferences.Output>() {

    override suspend fun run(param: NonInput): Output {
        val notifyNewNews : Boolean = vault["auto_search_notify"] ?: false
        return Output(
            UserNotificationPreferences(
                notifyNewNews
            )
        )
    }

    data class Output(
        val userNotificationPreferences: UserNotificationPreferences
    ): UseCase.Output()
}