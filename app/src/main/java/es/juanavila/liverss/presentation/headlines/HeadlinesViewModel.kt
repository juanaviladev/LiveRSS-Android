package es.juanavila.liverss.presentation.headlines

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.juanavila.liverss.application.usecases.NonInput
import es.juanavila.liverss.application.usecases.UseCaseInvoker
import es.juanavila.liverss.application.Page
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.application.usecases.headlines.GetHeadlines
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

sealed class NewsButtonState
object Down : NewsButtonState()
object Up : NewsButtonState()

sealed class ListEvent
data class Prepend(val page : Page<Headline>) : ListEvent()
data class Append(val page : Page<Headline>) : ListEvent()
data class Updated(val item : Headline) : ListEvent()
data class Remove(val fromId: Long, val toId: Long) : ListEvent()
object Purge : ListEvent()


class HeadlinesViewModel @Inject constructor(private val subscribeToLiveUpdates: SubscribeToLiveUpdates,
                                             private val saveHeadline: SaveHeadline, private val useCaseInvoker: UseCaseInvoker,
                                             private val getHeadlines: GetHeadlines
) : ViewModel(), CoroutineScope by MainScope() {

    private var liveNewsInfoJob : Job? = null
    private var refreshJob : Job? = null
    private var loadMoreJob : Job? = null
    private var initialLoad : Job? = null

    val appEventsHeadlines : BufferedLiveEvent<LiveUpdate> =
        BufferedLiveEvent()
    val newsListScrollEvent : SingleLiveEvent<ListScrollEvent> =
        SingleLiveEvent()
    val navigationState : SingleLiveEvent<NavigationEvent> =
        SingleLiveEvent()
    val listClientEvent : BufferedLiveEvent<ListEvent> by lazy {
        val event = BufferedLiveEvent<ListEvent>()
        initialLoad = launch {
            val page = retrieveNewsPage(sinceId = 0)
            event.value = Prepend(page)
        }
        return@lazy event
    }
    private var empty = true

    init {
        subscribeToLiveNews()
    }

    val newNewsButton : SingleLiveEvent<NewsButtonState> by lazy {
        val state = SingleLiveEvent<NewsButtonState>()
        state
    }

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

    val swipeRefreshLayoutRefreshingState : MutableLiveData<Boolean> = MutableLiveData()
    private lateinit var channel: ReceiveChannel<LiveUpdate>


    fun onListEmpty() {
        empty =true
        println("onListEmpty()")

        emptyPlaceholderViewState.value = Visible

        newsPlaceholderViewState.value = Hidden
        newsListViewState.value = Hidden
    }

    fun onListFilled() {
        empty =false

        println("ListFilled()")
        emptyPlaceholderViewState.value = Hidden

        newsPlaceholderViewAnimationEvent.value =
            FadeOut
        newsPlaceholderViewState.value = Hidden

        newsListViewAnimationEvent.value = FadeIn
        newsListViewState.value = Visible
    }

    private fun subscribeToLiveNews() = launch {
        channel =  useCaseInvoker.run(subscribeToLiveUpdates,
            NonInput()
        ).channel
        channel.consumeEach {
            println("new live event: $it")
            appEventsHeadlines.value = it
        }
    }
    override fun onCleared() {
        channel.cancel()
        super.onCleared()
    }
    fun showLoading() {
        emptyPlaceholderViewState.value = Hidden
        newsPlaceholderViewState.value = Visible
        newsListViewState.value = Hidden
    }

    fun onLastNewsClick(): Any = launch {
        newNewsButton.value = Up
        newsListScrollEvent.value = ScrollTo(0)
    }

    fun onLikeClick(item: Headline): Any = launch {
        val modifiedItem = item.copy(liked = true)
        listClientEvent.value = Updated(modifiedItem)
        useCaseInvoker.run(saveHeadline, SaveHeadline.Input(modifiedItem))
    }

    fun onDislikeClick(item: Headline): Any = launch {
        val modifiedItem = item.copy(liked = false)
        listClientEvent.value = Updated(modifiedItem)
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

    fun onRefreshRequest(firstItemId : Long){
        refreshJob = launch {
            println("onRefreshRequest: $firstItemId")
            swipeRefreshLayoutRefreshingState.value = true
            val page = retrieveNewsPage(sinceId = firstItemId)
            listClientEvent.value = Prepend(page)
            swipeRefreshLayoutRefreshingState.value = false
        }
    }


    private fun cancelAllInProgress() {
        liveNewsInfoJob?.cancel()
        refreshJob?.cancel()
        loadMoreJob?.cancel()
        initialLoad?.cancel()
    }

    private fun listCleaning(fromId: Long, toId: Long) {
        listClientEvent.value = Remove(fromId, toId)
    }

    fun onReselected() {
        newsListScrollEvent.value = ScrollTo(0)
    }

    fun onLoadMore(cursor: String = "", sinceId: Long) {
        loadMoreJob = launch {
            val page = retrieveNewsPage(cursor,sinceId)
            listClientEvent.value = Append(page)
        }
    }

    private suspend fun retrieveNewsPage(cursor: String = "", sinceId: Long): Page<Headline> {
        val result =  useCaseInvoker.run(getHeadlines,
            GetHeadlines.Input(
                cursor,
                sinceId
            )
        )
       return result.resultsPage
    }

    fun onAutoClean(fromId: Long, toId: Long) {
        listCleaning(fromId,toId)
    }

    fun onDeletedAll() {
        cancelAllInProgress()
        listClientEvent.value = Purge
    }

    fun onLiveNews(first: Headline?) {
        liveNewsInfoJob = launch {
            if(empty)
                showLoading()
            val page = retrieveNewsPage(sinceId = first?.id ?: 0)
            listClientEvent.value = Prepend(page)
            newNewsButton.value = Down
            delay(4000)
            newNewsButton.value = Up
        }
    }

}