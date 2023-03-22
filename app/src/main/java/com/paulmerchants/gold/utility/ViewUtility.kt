package com.paulmerchants.gold.utility

import android.app.Activity
import android.view.View
import android.view.ViewGroup


fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun ViewGroup.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}