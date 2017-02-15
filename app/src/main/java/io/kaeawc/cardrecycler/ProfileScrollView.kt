package io.kaeawc.cardrecycler

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView

class ProfileScrollView : ScrollView {

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    var scrollingEnabled = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {

        if (ev?.action == MotionEvent.ACTION_DOWN && !scrollingEnabled) {
            Log.i("ProfileScrollView", "Scrolling is disabled")
            return false
        }

        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when {
            !scrollingEnabled -> return false
            else -> return super.onInterceptTouchEvent(ev)
        }
    }
}
