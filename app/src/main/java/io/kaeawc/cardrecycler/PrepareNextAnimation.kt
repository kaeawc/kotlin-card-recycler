package io.kaeawc.cardrecycler

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class PrepareNextAnimation(val view: View, val fromHeight: Float, val toHeight: Float) : Animation() {

    var fromHeightDelta = 0f
    var toHeightDelta = 0f

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        if (view.alpha != interpolatedTime) {
            val dy = fromHeightDelta + (toHeightDelta - fromHeightDelta) * interpolatedTime
            Log.i("Next", "setting alpha=$interpolatedTime and height=$dy")
            view.alpha = interpolatedTime
            t.matrix.setTranslate(0f, dy)
        }
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        fromHeightDelta = resolveSize(Animation.ABSOLUTE, fromHeight, height, parentHeight)
        toHeightDelta = resolveSize(Animation.ABSOLUTE, toHeight, height, parentHeight)
    }
}
