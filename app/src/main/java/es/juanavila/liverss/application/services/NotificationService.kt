package es.juanavila.liverss.application.services

interface NotificationService {
    fun notify(msg : SimpleNotification)
}

data class SimpleNotification(
    val title : String,
    val message : String
)