package com.paulmerchants.gold.animations

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.paulmerchants.gold.R

object AppAnimation {
    fun rotate(iv: ImageView) {
        val animator = ObjectAnimator.ofFloat(iv, View.ROTATION, -360F, 0f)
        animator.duration = 1000
        animator.start()
    }

    fun translate(iv: ImageView) {
        val animator = ObjectAnimator.ofFloat(iv, View.TRANSLATION_X, 200f)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()
    }

    fun fader(iv: ImageView) {
        val animator = ObjectAnimator.ofFloat(iv, View.ALPHA, 0f)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
//        animator.disableViewDuringAnimation(fadeButton)
        animator.start()
    }

    fun colorizer(iv: ImageView) {

        // Animate the color of the star's container from black to red over a half
        // second, then reverse back to black. Note that using a propertyName of
        // "backgroundColor" will cause the animator to call the backgroundColor property
        // (in Kotlin) or setBackgroundColor(int) (in Java).

        val animator = ObjectAnimator.ofArgb(
            iv.parent,
            "backgroundColor", R.color.splash_screen_one, R.color.splash_screen_three
        )
        animator.duration = 500
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
//        animator.disableViewDuringAnimation(colorizeButton)
        animator.start()
    }


    fun scaler(iv: ImageView) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.2f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            iv, scaleX, scaleY
        )
        animator.repeatCount = 0
//        animator.repeatMode = ObjectAnimator.REVERSE
//        animator.disableViewDuringAnimation(scaleButton)
        animator.start()
    }

    private fun shower(context: Context, star: ImageView, drawable: Int) {

        // Create a new star view in a random X position above the container.
        // Make it rotateButton about its center as it falls to the bottom.

        // Local variables we'll need in the code below
        val container = star.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var starW: Float = star.width.toFloat()
        var starH: Float = star.height.toFloat()

        // Create the new star (an ImageView holding our drawable) and add it to the container
        val newStar = AppCompatImageView(context)
        newStar.setImageResource(drawable)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        container.addView(newStar)

        // Scale the view randomly between 10-160% of its default size
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY

        // Position the view at a random place between the left and right edges of the container
        newStar.translationX = Math.random().toFloat() * containerW - starW / 2

        // Create an animator that moves the view from a starting position right about the container
        // to an ending position right below the container. Set an accelerate interpolator to give
        // it a gravity/falling feel
        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y, -starH, containerH + starH)
        mover.interpolator = AccelerateInterpolator(1f)

        // Create an animator to rotateButton the view around its center up to three times
        val rotator = ObjectAnimator.ofFloat(
            newStar, View.ROTATION,
            (Math.random() * 1080).toFloat()
        )
        rotator.interpolator = LinearInterpolator()

        // Use an AnimatorSet to play the falling and rotating animators in parallel for a duration
        // of a half-second to two seconds
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()

        // When the animation is done, remove the created view from the container
//        set.addListener(object : AnimatorListenerAdapter() {
//            fun onAnimationEnd(animation: Animator?) {
//                container.removeView(newStar)
//            }
//        })

        // Start the animation
        set.start()
    }


}