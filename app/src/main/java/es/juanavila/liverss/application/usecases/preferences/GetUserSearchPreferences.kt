package es.juanavila.liverss.application.usecases.preferences

import es.juanavila.liverss.application.services.SettingsVault
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCase
import javax.inject.Inject

class GetUserSearchPreferences @Inject constructor(
    private val vault: SettingsVault
) :
    UseCase<NonInput, GetUserSearchPreferences.Output>() {

    override suspend fun run(param: NonInput): Output {
        val autoCleanEnabled : Boolean = vault["auto_search_enabled"] ?: false
        val autoSearchEnabled : Boolean = vault["auto_clean_enabled"] ?: false
        return Output(
            UserSearchPreferences(
                autoCleanEnabled,
                autoSearchEnabled
            )
        )
    }

    data class Output(
       val userSearchPreferences: UserSearchPreferences
    ): UseCase.Output()
}