package es.juanavila.liverss.presentation.common.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.juanavila.liverss.application.Page
import es.juanavila.liverss.domain.Headline
import es.juanavila.liverss.presentation.common.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Inject

sealed class ListElement<T>
data class Item<T>(val item: T) : ListElement<T>()
data class Gap<T>(var forId: Long, val beforeCursor: String) : ListElement<T>()

data class ListState(
    var empty: Boolean = true,
    val items: List<ListElement<Headline>> = emptyList(),
    var gapPos: Int = 0,
    var loadingMore: Boolean = false
)

sealed class ItemChange
object Like : ItemChange()

sealed class InternalAdapterEvent
data class LoadMore(val cursor: String = "", val sinceId: Long) : InternalAdapterEvent()
data class Clicked(val item : Headline) : InternalAdapterEvent()
data class Liked(val item : Headline) : InternalAdapterEvent()
data class Disliked(val item : Headline) : InternalAdapterEvent()

sealed class InternalListVisibility
object Empty : InternalListVisibility()
object Filled : InternalListVisibility()

class HeadlinesAdapterViewModel @Inject constructor() : ViewModel(), CoroutineScope by MainScope() {

    //Internal
    val adapterEvents : SingleLiveEvent<InternalAdapterEvent> =
        SingleLiveEvent()
    private var newsListState : ListState =
        ListState()
    val listStateObs : MutableLiveData<ListState> = MutableLiveData(newsListState)
    val listVisObs : MutableLiveData<InternalListVisibility> = MutableLiveData()

    init {
        println("NEW!!!!!")
    }

    fun append(page: Page<Headline>) {
        println("append")

        val newItems = newsListState.items.toMutableList()

        val loadingPos = newsListState.gapPos

        if(newsListState.items[loadingPos] !is Gap) {
            Log.d("HeadlinesAdapterVM","El gap de la posicion $loadingPos ha sido borrado mientras se cargaba el cursor")
            return
        }

        newItems.removeAt(loadingPos)
        newItems.addAll(loadingPos, page.data.map {
            Item(
                it
            )
        })

        val lastIndex = loadingPos + page.data.size - 1

        if(page.hasBeforeCursor()) {
            val lastItem = newItems[lastIndex] as Item<Headline>
            val gap = Gap<Headline>(
                lastItem.item.id - 1,
                page.beforeCursor
            )
            newItems.add(lastIndex + 1,gap)
        }

        newsListState.empty = newItems.isEmpty()

        if(newsListState.empty) {
            listVisObs.value = Empty
        }
        else {
            listVisObs.value = Filled
        }

        newsListState.gapPos = 0

        newsListState.loadingMore = false

        newsListState = newsListState.copy(items = newItems)

        listStateObs.value = newsListState
    }

    fun prepend(page: Page<Headline>) {
        println("prepend")

        val newItems = newsListState.items.toMutableList()

        if (newItems.size == 1 && newItems.last() is Gap)
            newItems.clear()

        newsListState.gapPos = 0

        newItems.addAll(0, page.data.map { Item(it) })

        val lastIndex = page.data.size - 1

        if(page.hasBeforeCursor()) {
            val lastItem = newItems[lastIndex] as Item<Headline>
            val gap = Gap<Headline>(
                lastItem.item.id - 1,
                page.beforeCursor
            )
            newItems.add(lastIndex + 1,gap)
        }

        newsListState.empty = newItems.isEmpty()

        if(newsListState.empty) {
            listVisObs.value = Empty
        }
        else {
            listVisObs.value = Filled
        }

        newsListState.loadingMore = false

        newsListState = newsListState.copy(items = newItems)

        listStateObs.value = newsListState
    }

    fun onScrolled(lastVisibleItemPosition: Int) {
        val item = newsListState.items[lastVisibleItemPosition]
        if (item !is Gap || newsListState.loadingMore) {
            return
        }

        val sinceId = if (lastVisibleItemPosition < newsListState.items.lastIndex) {
            val nextItem = newsListState.items[lastVisibleItemPosition + 1] as Item<Headline>
            nextItem.item.id
        }
        else 0L

        newsListState.loadingMore = true
        newsListState.gapPos = lastVisibleItemPosition
        adapterEvents.value =
            LoadMore(item.beforeCursor, sinceId)
    }

    fun onItemClicked(item: Headline) {
        adapterEvents.value = Clicked(item)
    }

    fun onLikeClick(item: Headline) {
        adapterEvents.value = Liked(item)
    }

    fun removeAll() {
        newsListState = ListState()
        listStateObs.value = newsListState
        listVisObs.value = Empty
    }

    fun remove(fromId: Long, toId: Long) { // 40 -> 60
        val startIndexInc = newsListState.items.indexOfFirst { it is Item<Headline> && it.item.id <= toId }
        val endIndexInc = newsListState.items.indexOfLast { it is Item<Headline> && it.item.id >= fromId }

        if(startIndexInc == -1) {
            println("nothing to remove $startIndexInc -> $endIndexInc")
            return
        }

        val newItems = mutableListOf<ListElement<Headline>>()

        for (i in 0 until startIndexInc) {
            val item = newsListState.items[i]
            newItems.add(item)
        }
        for (i in startIndexInc..endIndexInc) {
            val item = newsListState.items[i]
            if(item is Item && item.item.liked)
                newItems.add(item)
        }
        for (i in endIndexInc+1..newsListState.items.lastIndex) {
            val item = newsListState.items[i]
            newItems.add(item)
        }

        val newState = newsListState.copy(items = newItems)

        newState.empty = newState.items.isEmpty()

        if (newState.empty) {
            listVisObs.value = Empty
        } else {
            listVisObs.value = Filled
        }
        newsListState = newState

        listStateObs.value = newState
    }

    fun prepend(item: Headline) {
        val newItems = newsListState.items.toMutableList()

        newItems.add(0, Item(item))

        newsListState = newsListState.copy(items = newItems)

        newsListState.empty = newItems.isEmpty()

        if(newsListState.empty) {
            listVisObs.value = Empty
        }
        else {
            listVisObs.value = Filled
        }

        listStateObs.value = newsListState
    }

    fun update(item: Headline) {
        val index = newsListState.items.indexOf(
            Item(
                item
            )
        )
        if(index == -1) //Puede que llegue un item desde favs que aun no haya sido cargado por la paginacion
            return

        val newItems = newsListState.items.toMutableList()

        val oldItem = newItems[index] as Item<Headline>
        val changes = mutableListOf<ItemChange>()
        if(oldItem.item.liked != item.liked) {
            changes.add(Like)
        }
        newItems[index] = Item(item)

        newsListState = newsListState.copy(items = newItems)

        listStateObs.value = newsListState
    }

    fun remove(item: Headline) {
        val index = newsListState.items.indexOf(
            Item(
                item
            )
        )
        if(index != -1) {
            val newItems = newsListState.items.toMutableList()

            if(index + 1 < newsListState.items.size){
                val gap = newsListState.items[index + 1]
                if(gap is Gap) {
                    gap.forId = gap.forId + 1
                }
            }

            newItems.remove(Item(item))
            newsListState.empty = newItems.isEmpty()

            newsListState = newsListState.copy(items = newItems)

            listStateObs.value = newsListState

            if(newsListState.empty) {
                listVisObs.value = Empty
            }
            else {
                listVisObs.value = Filled
            }
        }
    }

    fun onDislikeClick(item: Headline) {
        adapterEvents.value = Disliked(item)
    }

}