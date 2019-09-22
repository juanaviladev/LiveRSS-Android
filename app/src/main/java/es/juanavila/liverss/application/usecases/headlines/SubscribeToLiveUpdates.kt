package es.juanavila.liverss.application.usecases.headlines

import android.util.Log
import es.juanavila.liverss.application.services.EventBus
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.produce
import javax.inject.Inject


sealed class LiveUpdate
data class AutoCleaned(val fromId: Long, val toId: Long) : LiveUpdate()
data class LiveNews(val quantity : Int) : LiveUpdate()
object DeletedAll : LiveUpdate()

class SubscribeToLiveUpdates @Inject constructor(private val eventBus: EventBus) :
    UseCase<NonInput, SubscribeToLiveUpdates.Output>() {

    override suspend fun run(param: NonInput): Output {
        val channel = subscribe(GlobalScope)
        return Output(channel)
    }

    private fun subscribe(coroutineScope: CoroutineScope): ReceiveChannel<LiveUpdate> =
        coroutineScope.produce(capacity = Channel.BUFFERED) {
            val registered = eventBus.register<LiveUpdate> {
                Log.d("EventBus","channel received an event $it")
                offer(it)
            }
            awaitClose {
                eventBus.remove(registered)
                println("Closed live news")
            }
        }

    data class Output(
        val channel: ReceiveChannel<LiveUpdate>
    ) : UseCase.Output()
}