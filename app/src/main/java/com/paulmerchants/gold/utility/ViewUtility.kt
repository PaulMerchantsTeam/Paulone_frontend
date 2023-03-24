package com.paulmerchants.gold.utility

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.paulmerchants.gold.R
import kotlinx.coroutines.NonDisposableHandle.parent


fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun ViewGroup.showVg() {
    visibility = View.VISIBLE
}

fun View.disableButton(context: Context) {
    this.isEnabled = false
    this.setBackgroundColor(
        ContextCompat.getColor(context, R.color.color_prim_one_40)
    )

}

fun View.enableButton(context: Context) {
    isEnabled = true
    setBackgroundColor(
        ContextCompat.getColor(context, R.color.splash_screen_one)
    )

}

fun CircularProgressIndicator.startProgress(context: Context) {
    isIndeterminate = true
    progress = 50
}

fun CircularProgressIndicator.endProgress(context: Context) {
    isIndeterminate = false
    progress = 100
}


fun View.hideView() {
    this.animate()
        .translationY(this.height.toFloat())
        .alpha(0.0f)
        .setDuration(300)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = View.GONE
            }
        })
}

fun TextView.setTColor(str: String, context: Context, colorId: Int) {
    text = str
    setTextColor(ContextCompat.getColor(context, colorId))
}

fun View.showViewWithAnim() {
    this.animate()
        .translationY(this.height.toFloat())
        .alpha(0.0f)
        .setDuration(300)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = View.VISIBLE
            }
        })
}

fun toggle(view: View, show: Boolean) {
    val transition: Transition = Fade()
    transition.duration = 600
    transition.addTarget(view)
    TransitionManager.beginDelayedTransition(view as ViewGroup, transition)
    view.setVisibility(if (show) View.VISIBLE else View.GONE)
}

fun ViewGroup.hideViewGrp() {
    this.animate()
        .translationY(this.height.toFloat())
        .alpha(0.0f)
        .setDuration(300)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = View.GONE
            }
        })
}


fun ObjectAnimator.disableViewDuringAnimation(view: View) {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
            super.onAnimationStart(animation, isReverse)
        }

        override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
            super.onAnimationEnd(animation, isReverse)
        }
    })
}

