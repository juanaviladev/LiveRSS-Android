package es.juanavila.liverss.framework.event

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import es.juanavila.liverss.application.services.AppStateService
import javax.inject.Inject

class AndroidAppStateService @Inject constructor() : AppStateService, LifecycleObserver {

    private var foregrounded : Boolean = false

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("AndroidAppStateService","onAppBackgrounded")
        foregrounded = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("AndroidAppStateService","onAppForegrounded")
        foregrounded = true
    }


    override fun isAppInForeground(): Boolean {
        return foregrounded
    }

}