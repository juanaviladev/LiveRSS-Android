package es.juanavila.liverss.presentation.main

import androidx.lifecycle.ViewModel
import es.juanavila.liverss.application.usecases.headlines.LiveUpdate
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCaseInvoker
import es.juanavila.liverss.application.usecases.headlines.SubscribeToLiveUpdates
import es.juanavila.liverss.presentation.common.BufferedLiveEvent
import es.juanavila.liverss.presentation.common.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BottomMenuEvent
data class Reselected(val tag: String) : BottomMenuEvent()

class MainScreenViewModel @Inject constructor(val subscribeToLiveUpdates: SubscribeToLiveUpdates,
                                              val useCaseInvoker: UseCaseInvoker
) : ViewModel(),CoroutineScope by MainScope() {

    val bottomMenuEvents : SingleLiveEvent<BottomMenuEvent> =
        SingleLiveEvent()
    val scrolledToTop : SingleLiveEvent<Any> =
        SingleLiveEvent()
    val appEvents : BufferedLiveEvent<LiveUpdate> =
        BufferedLiveEvent()

    private lateinit var channel: ReceiveChannel<LiveUpdate>

    init {
        subscribeToLiveNews()
    }
    private fun subscribeToLiveNews() = launch {
        channel =  useCaseInvoker.run(subscribeToLiveUpdates,
            NonInput()
        ).channel
        channel.consumeEach {
            println("new live event: $it")
            appEvents.value = it
        }
    }
}