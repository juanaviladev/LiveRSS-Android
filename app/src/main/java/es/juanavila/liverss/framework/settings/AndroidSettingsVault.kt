package es.juanavila.liverss.framework.settings

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import es.juanavila.liverss.application.services.SettingsVault
import javax.inject.Inject

class AndroidSettingsVault @Inject constructor(ctx: Application) : SettingsVault {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(ctx.applicationContext)

    override fun set(key: String, value: String) = editAndApply { putString(key,value) }

    private fun editAndApply(l : SharedPreferences.Editor.() -> Unit) {
        val editor : SharedPreferences.Editor = preferences.edit()
        editor.apply(l)
        editor.apply()
    }

    override fun set(key: String, value: Boolean) = editAndApply { putBoolean(key,value) }

    override fun <T> get(key: String): T = preferences.all[key] as T
}