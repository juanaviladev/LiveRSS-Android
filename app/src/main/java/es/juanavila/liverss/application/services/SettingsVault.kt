package es.juanavila.liverss.application.services

interface SettingsVault {
    operator fun set(key: String, value: String)
    operator fun set(key: String, value: Boolean)
    operator fun <T> get(key: String) : T?
}