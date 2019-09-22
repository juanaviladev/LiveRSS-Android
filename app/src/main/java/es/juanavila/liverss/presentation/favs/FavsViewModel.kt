package es.juanavila.liverss.presentation.favs

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCaseInvoker
import es.juanavila.liverss.application.Page
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.usecases.headlines.GetFavsHeadlines
import es.juanavila.liverss.application.usecases.headlines.LiveUpdate
import es.juanavila.liverss.application.usecases.headlines.SaveHeadline
import es.juanavila.liverss.application.usecases.headlines.SubscribeToLiveUpdates
import es.juanavila.liverss.presentation.common.*
import es.juanavila.liverss.presentation.reader.FullScreenReaderActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import javax.inject.Inject

sealed class ViewState
object Visible : ViewState()
object Hidden : ViewState()

sealed class ViewAnimation
object FadeIn : ViewAnimation()
object FadeOut : ViewAnimation()

sealed class ListEvent
data class PrependPage(val page : Page<Headline>) : ListEvent()
data class AppendPage(val page : Page<Headline>) : ListEvent()
data class Prepend(val item : Headline) : ListEvent()
data class RemoveRange(val fromId: Long, val toId: Long) : ListEvent()
data class Remove(val item : Headline) : ListEvent()
object Purge : ListEvent()


class FavsViewModel @Inject constructor(private val saveHeadline: SaveHeadline,
                                        private val favsHeadlines: GetFavsHeadlines,
                                        private val useCaseInvoker: UseCaseInvoker,
                                        private val subscribeToLiveUpdates: SubscribeToLiveUpdates
) : ViewModel(), CoroutineScope by MainScope() {

    private lateinit var channel: ReceiveChannel<LiveUpdate>

    private var liveNewsInfoJob : Job? = null
    private var refreshJob : Job? = null
    private var loadMoreJob : Job? = null
    private var initialLoad : Job? = null
    val appEventsFavs : BufferedLiveEvent<LiveUpdate> =
        BufferedLiveEvent()
    val newsListScrollEvent : SingleLiveEvent<ListScrollEvent> =
        SingleLiveEvent()
    val navigationState : SingleLiveEvent<NavigationEvent> =
        SingleLiveEvent()
    val listClientEvent : BufferedLiveEvent<ListEvent> by lazy {
        val event = BufferedLiveEvent<ListEvent>()
        initialLoad = launch {
            val page = retrieveLikesPage(sinceId = 0)
            event.value = PrependPage(page)
        }
        return@lazy event
    }
    private var empty = true

    val emptyPlaceholderViewState : MutableLiveData<ViewState> = MutableLiveData(
        Hidden
    )
    val newsPlaceholderViewState : MutableLiveData<ViewState> = MutableLiveData(
        Visible
    )
    val newsListViewState : MutableLiveData<ViewState> = MutableLiveData(
        Hidden
    )
    val emptyPlaceholderViewAnimationEvent : SingleLiveEvent<ViewAnimation> =
        SingleLiveEvent()
    val newsPlaceholderViewAnimationEvent : SingleLiveEvent<ViewAnimation> =
        SingleLiveEvent()
    val newsListViewAnimationEvent : SingleLiveEvent<ViewAnimation> =
        SingleLiveEvent()
    init {
        subscribeToLiveNews()
    }
    fun onListEmpty() {
        empty = true
        println("onListEmpty()")

        emptyPlaceholderViewAnimationEvent.value = FadeIn

        newsPlaceholderViewState.value = Hidden
        newsListViewState.value = Hidden
    }

    fun onListFilled() {
        empty = false

        println("ListFilled()")
        emptyPlaceholderViewState.value = Hidden

        newsPlaceholderViewAnimationEvent.value =
            FadeOut
        newsPlaceholderViewState.value = Hidden

        newsListViewAnimationEvent.value = FadeIn
        newsListViewState.value = Visible
    }

    fun showLoading() {
        emptyPlaceholderViewState.value = Hidden
        newsPlaceholderViewState.value = Visible
        newsListViewState.value = Hidden
    }

    fun onDislikeClick(item: Headline): Any = launch {
        val modifiedItem = item.copy(liked = false)
        listClientEvent.value = Remove(modifiedItem)
        useCaseInvoker.run(saveHeadline, SaveHeadline.Input(modifiedItem))
    }



    fun onItemClicked(item: Headline) = launch {
        item.hasBeenRead = true
        useCaseInvoker.run(saveHeadline, SaveHeadline.Input(item))
        val bundle = Bundle()
        bundle.putString("url",item.url)
        navigationState.value = GoToScreen(
            FullScreenReaderActivity::class.java,
            bundle
        )
    }

    private fun cancelAllInProgress() {
        liveNewsInfoJob?.cancel()
        refreshJob?.cancel()
        loadMoreJob?.cancel()
        initialLoad?.cancel()
    }

    override fun onCleared() {
        channel.cancel()
        super.onCleared()
    }

    fun onReselected() {
        newsListScrollEvent.value = ScrollTo(0)
    }

    fun onLoadMore(cursor: String = "", sinceId: Long) {
        loadMoreJob = launch {
            val page = retrieveLikesPage(cursor,sinceId)
            listClientEvent.value = AppendPage(page)
        }
    }

    private suspend fun retrieveLikesPage(cursor: String = "", sinceId: Long): Page<Headline> {
        val result =  useCaseInvoker.run(favsHeadlines,
            GetFavsHeadlines.Input(
                cursor,
                sinceId
            )
        )
       return result.resultsPage
    }

    fun onLikeClick(item: Headline) {
        val modifiedItem = item.copy(liked = true)
        listClientEvent.value = Prepend(modifiedItem)
    }

    fun onAutoClean(fromId: Long, toId: Long) {
        listClientEvent.value = RemoveRange(fromId, toId)
    }

    fun onDeletedAll() {
        cancelAllInProgress()
        listClientEvent.value = Purge
    }


    private fun subscribeToLiveNews() = launch {
        channel =  useCaseInvoker.run(subscribeToLiveUpdates,
            NonInput()
        ).channel
        channel.consumeEach {
            println("new live event: $it")
            appEventsFavs.value = it
        }
    }

}