package es.juanavila.liverss.application.usecases.preferences

import es.juanavila.liverss.application.services.SettingsVault
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCase
import javax.inject.Inject

class GetUserSourcesPreferences @Inject constructor(
    private val vault: SettingsVault
) :
    UseCase<NonInput, GetUserSourcesPreferences.Output>() {

    override suspend fun run(param: NonInput): Output {
        val _20Minutos : Boolean = vault["20_minutos_enabled"] ?: false
        val diarioAs : Boolean = vault["diario_as_enabled"] ?: false
        val elDiario : Boolean = vault["el_diario_enabled"] ?: false
        val elMundo : Boolean = vault["el_mundo_enabled"] ?: false
        val elPais : Boolean = vault["el_pais_enabled"] ?: false
        val elConfidencial : Boolean = vault["el_confidencial_enabled"] ?: false
        val europapress : Boolean = vault["europapress_enabled"] ?: false
        val marca : Boolean = vault["marca_enabled"] ?: false
        return Output(
            UserSourcesPreferences(
                _20Minutos,
                diarioAs,
                elDiario,
                elMundo,
                elPais,
                elConfidencial,
                europapress,
                marca
            )
        )
    }

    data class Output(
        val userSourcesPreferences: UserSourcesPreferences
    ): UseCase.Output()
}