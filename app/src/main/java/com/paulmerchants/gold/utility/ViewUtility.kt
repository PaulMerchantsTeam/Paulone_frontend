package com.paulmerchants.gold.utility

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.HomeSweetBillsAdapter
import com.paulmerchants.gold.model.ActionItem
import kotlinx.coroutines.NonDisposableHandle.parent


fun ImageView.startCustomAnimation(drawable: Int) {
    val animationDrawable: AnimationDrawable
    this.apply {
        setBackgroundResource(drawable)
        animationDrawable = background as AnimationDrawable
        animationDrawable.start()
    }
}

fun RecyclerView.setUiOnHomeSweetHomeBills(context: Context) {
    val homeSweetBillsAdapter = HomeSweetBillsAdapter()
    val actionItem1 = ActionItem(1, R.drawable.elec_bill, context.getString(R.string.electricity))
    val actionItem2 =
        ActionItem(2, R.drawable.broadband_bill, context.getString(R.string.broadband))
    val actionItem3 =
        ActionItem(3, R.drawable.education_loan, context.getString(R.string.education))
    val actionItem4 =
        ActionItem(4, R.drawable.cylinder_gas, context.getString(R.string.gas_cylinder))
    val actionItem5 =
        ActionItem(5, R.drawable.apartment_group, context.getString(R.string.apartment))
    val actionItem6 =
        ActionItem(6, R.drawable.pipeline_gas, context.getString(R.string.gas_pipline))
    val actionItem7 = ActionItem(7, R.drawable.home_rent, context.getString(R.string.homerent))
    val actionItem8 = ActionItem(8, R.drawable.tap, context.getString(R.string.water))
    val actionItem9 =
        ActionItem(9, R.drawable.landline_action, context.getString(R.string.landline))
    val actionItem10 = ActionItem(10, R.drawable.cable_tv, context.getString(R.string.cabletv))
    val list = listOf(
        actionItem1,
        actionItem2,
        actionItem3,
        actionItem4,
        actionItem5,
        actionItem6,
        actionItem7,
        actionItem8,
        actionItem9,
        actionItem10
    )
    val staggeredGridLayoutManager = StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL)
    homeSweetBillsAdapter.submitList(list)
    this.apply {
        layoutManager = staggeredGridLayoutManager
        adapter = homeSweetBillsAdapter
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
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

fun View.shoViewWithAnim() {
    visibility = View.VISIBLE
    alpha = 0.0f
    this.animate()
        .translationY(this.height.toFloat())
        .alpha(1.0f)
        .setListener(null)
}

fun TextView.setTColor(str: String, context: Context, colorId: Int) {
    text = str
    setTextColor(ContextCompat.getColor(context, colorId))
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

