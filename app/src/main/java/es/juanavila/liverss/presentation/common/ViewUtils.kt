package es.juanavila.liverss.presentation.common

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener

fun View.fadeOut(duration: Long = 2000,listener: () -> Unit = {}) {
    if(visibility == View.GONE || visibility == View.INVISIBLE)
        return

    val animator = ObjectAnimator.ofFloat(this,"alpha",1f,0f)
    animator.interpolator = LinearInterpolator()
    animator.duration = duration
    animator.addListener(
        onEnd = {
            visibility = View.GONE
            listener()
        },
        onStart = {
            visibility = View.VISIBLE
        }
    )
    animator.start()
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.fadeIn(duration: Long = 500,listener: () -> Unit = {}) {
    if(visibility == View.VISIBLE) {
        println("ALREADY VISIBLE SSKIP")
        return
    }
    forceFadeIn(duration,listener)
}

fun View.forceFadeIn(duration: Long,listener: () -> Unit = {}) {
    val animator = ObjectAnimator.ofFloat(this,"alpha",0f,1f)
    animator.interpolator = LinearInterpolator()
    animator.duration = duration
    animator.addListener(
        onEnd = {
            listener()
        },
        onStart = {
            visibility = View.VISIBLE
        }
    )
    animator.start()
}
