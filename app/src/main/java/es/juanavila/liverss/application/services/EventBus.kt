package es.juanavila.liverss.application.services

interface EventBus {
    fun <T> register(observer : (T) -> Unit) : (T) -> Unit
    fun <T> remove(observer : (T) -> Unit)
    fun send(value : Any)
}