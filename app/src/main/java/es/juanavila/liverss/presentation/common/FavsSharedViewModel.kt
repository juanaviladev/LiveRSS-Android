package es.juanavila.liverss.presentation.common

import androidx.lifecycle.ViewModel
import es.juanavila.liverss.domain.Headline
import javax.inject.Inject

sealed class FavAction {
    data class Liked(val item: Headline) : FavAction()
    data class Disliked(val item: Headline) : FavAction()
}

class FavsSharedViewModel @Inject constructor() : ViewModel() {

    val mainEvents : BufferedLiveEvent<FavAction> =
        BufferedLiveEvent()
    val likesListEvents : BufferedLiveEvent<FavAction> =
        BufferedLiveEvent()

    fun onLikeClick(item: Headline) {
        likesListEvents.value = FavAction.Liked(item)
        mainEvents.value = FavAction.Liked(item)
    }

    fun onDislikeClick(item: Headline) {
        likesListEvents.value = FavAction.Disliked(item)
        mainEvents.value = FavAction.Disliked(item)
    }

    /*
    Realmente no es necesario puesto que los likes acumulados al no estar ya presentes en la lista,
    no se procesarian
     */
    fun onDeletedAll() {
        mainEvents.clear()
        likesListEvents.clear()
    }

}