package es.juanavila.liverss.application.services

interface AppStateService {
    fun isAppInForeground() : Boolean
}