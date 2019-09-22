package es.juanavila.liverss.presentation.common

import android.os.Bundle


sealed class NavigationEvent
data class GoToScreen(val screenClazz : Class<*>,val data: Bundle) : NavigationEvent()


sealed class ListScrollEvent
data class ScrollTo(val pos: Int) : ListScrollEvent()