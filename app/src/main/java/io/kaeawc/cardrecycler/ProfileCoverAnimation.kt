package io.kaeawc.cardrecycler

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class ProfileCoverAnimation(val view: View, val fromHeight: Float, val toHeight: Float, val parentHeight: Float) : Animation() {

    var fromHeightDelta = 0f
    var toHeightDelta = 0f

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val dy = fromHeightDelta + (toHeightDelta - fromHeightDelta) * interpolatedTime
        Log.i("CoverAnim", "Height is $dy")
        if (dy > parentHeight) {
            view.top = (parentHeight - dy).toInt()
            view.layoutParams.height = parentHeight.toInt()
        } else {
            view.layoutParams.height = dy.toInt()
        }

        view.requestLayout()
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        fromHeightDelta = resolveSize(Animation.ABSOLUTE, fromHeight, height, parentHeight)
        toHeightDelta = resolveSize(Animation.ABSOLUTE, toHeight, height, parentHeight)
    }
}
