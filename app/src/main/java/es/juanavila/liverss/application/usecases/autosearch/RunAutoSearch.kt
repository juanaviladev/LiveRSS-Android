package es.juanavila.liverss.application.usecases.autosearch

import es.juanavila.liverss.data.rss.diarioas.DiarioAsScrapper
import es.juanavila.liverss.data.rss.elconfidencial.ElConfidencialScrapper
import es.juanavila.liverss.data.rss.eldiario.ElDiarioScrapper
import es.juanavila.liverss.data.rss.elmundo.ElMundoScrapper
import es.juanavila.liverss.data.rss.elpais.ElPaisScrapper
import es.juanavila.liverss.data.rss.europapress.EuropaPressScrapper
import es.juanavila.liverss.data.rss.marca.MarcaScrapper
import es.juanavila.liverss.data.rss.veinteminutos.VeinteMinutosScrapper
import es.juanavila.liverss.domain.HeadlinesRepository
import es.juanavila.liverss.application.services.AppStateService
import es.juanavila.liverss.framework.notification.AndroidNotificationService
import es.juanavila.liverss.application.services.SimpleNotification
import es.juanavila.liverss.application.services.SettingsVault
import es.juanavila.liverss.application.usecases.UseCase
import es.juanavila.liverss.application.usecases.preferences.UserNotificationPreferences
import es.juanavila.liverss.application.usecases.preferences.UserSearchPreferences
import es.juanavila.liverss.application.usecases.preferences.UserSourcesPreferences
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import javax.inject.Inject

class RunAutoSearch @Inject constructor(
    private val repository: HeadlinesRepository,
    private val notificationService: AndroidNotificationService,
    private val appStateService: AppStateService,
    private val vault: SettingsVault,
    private val elPaisScrapper: ElPaisScrapper,
    private val elMundoScrapper: ElMundoScrapper,
    private val elDiarioScrapper: ElDiarioScrapper,
    private val elConfidencialScrapper: ElConfidencialScrapper,
    private val veinteMinutosScrapper: VeinteMinutosScrapper,
    private val europaPressScrapper: EuropaPressScrapper,
    private val diarioAsScrapper: DiarioAsScrapper,
    private val marcaScrapper: MarcaScrapper
) :
    UseCase<RunAutoSearch.Input, RunAutoSearch.Output>() {

    override suspend fun run(param: Input): Output {
        val lastHeadlines = coroutineScope {
            val elPaisHeadlines = async { if(param.sourcesPreferences.elPais) elPaisScrapper.extractHeadlines() else emptyList() }
            val elMundoHeadlines = async { if(param.sourcesPreferences.elMundo) elMundoScrapper.extractHeadlines() else emptyList() }
            val elDiarioHeadlines = async {if(param.sourcesPreferences.elDiario) elDiarioScrapper.extractHeadlines() else emptyList() }
            val elConfidencialHeadlines = async { if(param.sourcesPreferences.elConfidencial) elConfidencialScrapper.extractHeadlines() else emptyList() }
            val veinteMinutosHeadlines = async { if(param.sourcesPreferences._20Minutos) veinteMinutosScrapper.extractHeadlines() else emptyList() }
            val europaPressHeadlines = async { if(param.sourcesPreferences.europapress) europaPressScrapper.extractHeadlines() else emptyList() }
            val diarioAsHeadlines = async { if(param.sourcesPreferences.diarioAs) diarioAsScrapper.extractHeadlines() else emptyList() }
            val marcaHeadlines = async { if(param.sourcesPreferences.marca) marcaScrapper.extractHeadlines() else emptyList() }
            awaitAll(
                elPaisHeadlines, elMundoHeadlines,
                elDiarioHeadlines, elConfidencialHeadlines, veinteMinutosHeadlines,
                europaPressHeadlines, diarioAsHeadlines, marcaHeadlines
            ).flatten()
        }
        val oldHeadlines = repository.findAll() // Ocurre en T2
        val newHeadlines = lastHeadlines.minus(oldHeadlines) //Ocurre en T3
        newHeadlines.forEach { repository.save(it) }

        if (newHeadlines.isNotEmpty() && param.notificationPreferences.notifyNewNews && !appStateService.isAppInForeground()) {
            val notification = SimpleNotification(
                title = "Nuevas noticias te están esperando",
                message = "Hay ${newHeadlines.size} noticias nuevas"
            )
            notificationService.notify(notification)
        }
        else {
            println("NO NOTIFY")
        }

        val time = DateTime.now(DateTimeZone.UTC)
        vault["last_sync_time_utc"] = time.toString()

        return Output(newHeadlines.size)
    }

    override fun transactionRequired() = true


    data class Input(
        val searchPreferences : UserSearchPreferences,
        val sourcesPreferences : UserSourcesPreferences,
        val notificationPreferences : UserNotificationPreferences
    ) : UseCase.Input()

    data class Output(val newNewsNumber : Int) : UseCase.Output()
}