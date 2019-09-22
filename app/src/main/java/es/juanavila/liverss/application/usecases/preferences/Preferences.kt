package es.juanavila.liverss.application.usecases.preferences

data class UserNotificationPreferences(
    val notifyNewNews: Boolean
)

data class UserSearchPreferences(
    val autoCleanEnabled: Boolean,
    val autoSearchEnabled: Boolean
)

data class UserSourcesPreferences(
    val _20Minutos: Boolean,
    val diarioAs: Boolean,
    val elDiario: Boolean,
    val elMundo: Boolean,
    val elPais: Boolean,
    val elConfidencial: Boolean,
    val europapress: Boolean,
    val marca: Boolean
)