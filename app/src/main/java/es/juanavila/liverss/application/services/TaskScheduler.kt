package es.juanavila.liverss.application.services

import java.util.concurrent.TimeUnit

sealed class Constraint {
    object InternetConnection : Constraint()
}

interface TaskScheduler {
    fun periodic(time: Long,unit: TimeUnit, id: String,clazz: Class<*>, vararg taskConstraints: Constraint) : String
    fun cancel(id: String)
    fun enqueue(id: String,clazz: Class<*>, vararg taskConstraints: Constraint): String
}