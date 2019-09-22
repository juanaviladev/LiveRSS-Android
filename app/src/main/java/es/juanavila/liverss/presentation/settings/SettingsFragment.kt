package es.juanavila.liverss.presentation.settings


import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import es.juanavila.liverss.*
import es.juanavila.liverss.application.usecases.headlines.DeleteAllHeadlines
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCaseInvoker
import es.juanavila.liverss.application.usecases.autoclean.GetDiskUsage
import es.juanavila.liverss.application.usecases.autoclean.GetLastAutoCleanTime
import es.juanavila.liverss.application.usecases.autosearch.CancelAutoSearch
import es.juanavila.liverss.application.usecases.autosearch.GetLastAutoSearchTime
import es.juanavila.liverss.application.usecases.autosearch.SchedulePeriodicAutoSearch
import es.juanavila.liverss.framework.App
import es.juanavila.liverss.presentation.common.fadeIn
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class SettingsFragment : PreferenceFragmentCompat(),CoroutineScope by MainScope() {

    @Inject
    lateinit var getDiskUsage: GetDiskUsage

    @Inject
    lateinit var deleteAllHeadlines: DeleteAllHeadlines

    @Inject
    lateinit var cancelAutoSearch: CancelAutoSearch

    @Inject
    lateinit var schedulePeriodicAutoSearch: SchedulePeriodicAutoSearch

    @Inject
    lateinit var getLastAutoCleanTime: GetLastAutoCleanTime

    @Inject
    lateinit var getLastAutoSearchTime: GetLastAutoSearchTime

    lateinit var autoSearchSwitch : SwitchPreferenceCompat
    lateinit var autoSearchPeriod : DropDownPreference
    lateinit var autoCleanSwitch : SwitchPreferenceCompat
    lateinit var autoCleanCriteria : DropDownPreference
    lateinit var cleanAll : Preference

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var useCaseInvoker: UseCaseInvoker

    init {
        App.appComponent.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences,rootKey)
        val syncPeriodProvider = Preference.SummaryProvider<DropDownPreference> {
            if(it.isEnabled) {
                "Se buscarán noticias cada ${it.entry}"
            }
            else { "" }
        }
        val cleanCriteriaProvider = Preference.SummaryProvider<DropDownPreference> {
            if(it.isEnabled) {
                "Se eliminarán las noticias con más de ${it.entry} de antigüedad"
            }
            else { "" }
        }
        autoSearchSwitch = findPreference("auto_search_enabled")!!
        autoSearchPeriod = findPreference("auto_search_period")!!
        autoSearchPeriod.summaryProvider = syncPeriodProvider
        autoSearchSwitch.setOnPreferenceChangeListener { _, newValue ->
            if(newValue as Boolean) {
                schedulePeriodicAutoSearch(autoSearchPeriod.value)
                launch { updateWithLastSearchTime() }
            } else
                cancelAutoSearch()
            return@setOnPreferenceChangeListener true
        }
        autoSearchPeriod.setOnPreferenceChangeListener { _ ,_->
            schedulePeriodicAutoSearch(autoSearchPeriod.value)
            return@setOnPreferenceChangeListener true
        }

        cleanAll = findPreference("clean_all")!!
        autoCleanSwitch = findPreference("auto_clean_enabled")!!
        autoCleanCriteria = findPreference("auto_clean_criteria")!!
        autoCleanCriteria.summaryProvider = cleanCriteriaProvider
        autoSearchSwitch.setOnPreferenceChangeListener { _, newValue ->
            if(newValue as Boolean)
                launch {  updateWithLastCleanTime() }
            return@setOnPreferenceChangeListener true
        }
        cleanAll.setOnPreferenceClickListener {
            cleanAll()
            return@setOnPreferenceClickListener true
        }
    }

    private suspend fun updateDiskUsage() {
        val size = useCaseInvoker.run(getDiskUsage,
            NonInput()
        ).humanReadableByteCount
        cleanAll.summary = "Ahorrarás $size de espacio en disco"
    }

    private fun cleanAll() = launch {
        useCaseInvoker.run(deleteAllHeadlines,
            NonInput()
        )
        updateDiskUsage()
    }

    private fun cancelAutoSearch() = launch {
        useCaseInvoker.run(cancelAutoSearch, NonInput())
    }

    private fun schedulePeriodicAutoSearch(newTime: String) = launch {
        useCaseInvoker.run(schedulePeriodicAutoSearch,
            SchedulePeriodicAutoSearch.Input(newTime.toLong()))
    }

    private suspend fun updateWithLastSearchTime() {
        val lastTime =  useCaseInvoker.run(getLastAutoSearchTime,
            NonInput()
        ).time
        if(lastTime != null) {
            val prettyTime = DateUtils.getRelativeTimeSpanString(
                lastTime.millis,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL
            ).toString().decapitalize()
            autoSearchSwitch.summaryOn = "Última sincronización $prettyTime"
        }
        else {
            autoSearchSwitch.summaryOn = "Sincronización pendiente"
        }
    }

    private suspend fun updateWithLastCleanTime() {
        val lastTime =  useCaseInvoker.run(getLastAutoCleanTime,
            NonInput()
        ).time
        if(lastTime != null) {
            val prettyTime = DateUtils.getRelativeTimeSpanString(
                lastTime.millis,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL
            ).toString().decapitalize()
            autoCleanSwitch.summaryOn = "Última limpieza automática $prettyTime"
        }
        else {
            autoCleanSwitch.summaryOn = "Limpieza pendiente"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch {
            updateWithLastSearchTime()
            updateWithLastCleanTime()
            updateDiskUsage()
            activity?.toolbar?.apply {
                findViewById<TextView>(R.id.textView)?.text = "Settings"
                findViewById<ImageView>(R.id.imageView)?.setImageResource(R.drawable.ic_settings_black_24dp)
                findViewById<ImageView>(R.id.imageView)?.fadeIn(4000)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
