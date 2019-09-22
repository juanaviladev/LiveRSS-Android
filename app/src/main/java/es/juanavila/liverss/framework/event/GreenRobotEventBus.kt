package es.juanavila.liverss.framework.event

import es.juanavila.liverss.application.services.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject
import org.greenrobot.eventbus.EventBus as GreenRobotEventBus

class GreenRobotEventBus @Inject constructor(): EventBus {

    private class GreenObserver<T>(val wrapped : (T) -> Unit) {
        @Subscribe
        operator fun invoke(t: T) = wrapped(t)
    }

    private val observers = mutableMapOf<Any,Any>()

    override fun <T> remove(observer: (T) -> Unit) {
        GreenRobotEventBus.getDefault().unregister(observers[observer])
        observers.remove(observer)
    }

    override fun send(value: Any) {
        GreenRobotEventBus.getDefault().post(value)
    }

    override fun <T> register(observer: (T) -> Unit) : (T) -> Unit {
        val wrapped =
            GreenObserver(observer)
        GreenRobotEventBus.getDefault().register(wrapped)
        observers[observer] = wrapped
        return observer
    }

}