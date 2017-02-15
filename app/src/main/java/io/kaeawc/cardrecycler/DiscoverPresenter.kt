package io.kaeawc.cardrecycler

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

class DiscoverPresenter : Animation.AnimationListener {

    companion object {
        const val COVER_OVERSHOOT_TENSION = 0.82f
        const val COVER_DURATION = 500L
        const val COVER_DELAY = 0L

        const val PREVIOUS_OVERSHOOT_TENSION = 0.82f
        const val PREVIOUS_DURATION = 500L
        const val PREVIOUS_DELAY = 205L

        const val NEXT_OVERSHOOT_TENSION = 1.5f
        const val NEXT_DURATION = 400L
        const val NEXT_DELAY = 500L
    }

    val colors = listOf(
            R.color.blue_dark,
            R.color.green_dark,
            R.color.orange_dark,
            R.color.red_dark,
            R.color.blue_light,
            R.color.green_light,
            R.color.orange_light,
            R.color.red_light)

    var current: String = "A"
    var currentColor = 0

    var weakActivity: WeakReference<MainActivity>? = null

    fun onCreate(activity: MainActivity) {
        weakActivity = WeakReference(activity)

        (activity.profileB as ProfileScrollView).scrollingEnabled = false
        (activity.profileC as ProfileScrollView).scrollingEnabled = false
        randomizeProfile(activity.profileA as ProfileScrollView)
        randomizeProfile(activity.profileB as ProfileScrollView)
        randomizeProfile(activity.profileC as ProfileScrollView)
        setProfileHeader(activity.profileA as ProfileScrollView, "A")
        setProfileHeader(activity.profileB as ProfileScrollView, "B")
        setProfileHeader(activity.profileC as ProfileScrollView, "C")

    }

    fun setProfileHeader(layout: ScrollView, name: String) {
        val header = layout.findViewById(R.id.header) as LinearLayout
        val subjectName = layout.findViewById(R.id.subjectName) as TextView
        subjectName.text = name
        header.setOnClickListener { transitionToNextProfile() }
    }

    override fun onAnimationStart(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
        if (animation is ProfileCoverAnimation) {
            onCoverAnimationEnd()
        } else if (animation is PrepareNextAnimation) {
            onNextAnimationEnd()
        }
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }

    fun getNextLayoutParams(alignBottom: Boolean = false): RelativeLayout.LayoutParams {
        return if (alignBottom) {
            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 148)
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params
        } else {
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 300)
        }
    }

    fun onNextAnimationEnd() {

        val activity = weakActivity?.get() ?: return
        when (current) {
            "A" -> activity.profileC.layoutParams = getNextLayoutParams(true)
            "B" -> activity.profileA.layoutParams = getNextLayoutParams(true)
            "C" -> activity.profileB.layoutParams = getNextLayoutParams(true)
        }

        current = when (current) {
            "A" -> { "B" }
            "B" -> { "C" }
            "C" -> { "A" }
            else -> throw IllegalArgumentException("Profile layout $current does not exist")
        }
    }

    fun prepareNextProfile(profileLayout: ProfileScrollView) {

        val activity = weakActivity?.get() ?: return
        profileLayout.layoutParams = getNextLayoutParams(false)
        profileLayout.scrollTo(0, 0)
        profileLayout.bringToFront()
        profileLayout.visibility = View.VISIBLE
        profileLayout.alpha = 0f
        profileLayout.scrollingEnabled = false

        val bottom = activity.rootLayout.measuredHeight.toFloat()
        val height = 148
        val prepareNext = PrepareNextAnimation(profileLayout, bottom, bottom - height)
        prepareNext.duration = NEXT_DURATION
        prepareNext.startOffset = NEXT_DELAY
        prepareNext.setAnimationListener(this)
        prepareNext.interpolator = OvershootInterpolator(NEXT_OVERSHOOT_TENSION)
        profileLayout.startAnimation(prepareNext)
    }

    fun hidePreviousProfile(profileLayout: ProfileScrollView) {
        val hidePrevious = HidePreviousAnimation(0f, -148f)
        hidePrevious.duration = PREVIOUS_DURATION
        hidePrevious.startOffset = PREVIOUS_DELAY
        hidePrevious.setAnimationListener(this)
        hidePrevious.interpolator = OvershootInterpolator(PREVIOUS_OVERSHOOT_TENSION)
        profileLayout.startAnimation(hidePrevious)
    }

    fun onCoverAnimationEnd() {

        val activity = weakActivity?.get() ?: return
        when (current) {
            "A" -> {
                activity.profileA.visibility = View.GONE
                (activity.profileB as ProfileScrollView).scrollingEnabled = true
            }
            "B" -> {
                activity.profileB.visibility = View.GONE
                (activity.profileC as ProfileScrollView).scrollingEnabled = true
            }
            "C" -> {
                activity.profileC.visibility = View.GONE
                (activity.profileA as ProfileScrollView).scrollingEnabled = true
            }
        }
    }

    fun transitionToNextProfile(profileLayout: View) {

        val activity = weakActivity?.get() ?: return
        val totalHeight = activity.rootLayout.measuredHeight.toFloat()
        val cover = ProfileCoverAnimation(profileLayout, profileLayout.measuredHeight.toFloat(), totalHeight, totalHeight)
        cover.duration = COVER_DURATION
        cover.startOffset = COVER_DELAY
        cover.setAnimationListener(this)
        cover.interpolator = OvershootInterpolator(COVER_OVERSHOOT_TENSION)
        profileLayout.startAnimation(cover)
    }

    fun transitionToNextProfile() {

        val activity = weakActivity?.get() ?: return
        when (current) {
            "A" -> {
                transitionToNextProfile(activity.profileB)
                prepareNextProfile(activity.profileC as ProfileScrollView)
                hidePreviousProfile(activity.profileA as ProfileScrollView)
            }
            "B" -> {
                transitionToNextProfile(activity.profileC)
                prepareNextProfile(activity.profileA as ProfileScrollView)
                hidePreviousProfile(activity.profileB as ProfileScrollView)
            }
            "C" -> {
                transitionToNextProfile(activity.profileA)
                prepareNextProfile(activity.profileB as ProfileScrollView)
                hidePreviousProfile(activity.profileC as ProfileScrollView)
            }
        }
    }

    fun randomizeProfile(layout: ScrollView) {
        val thumbnail = layout.findViewById(R.id.thumbnail) as ImageView?
        val photos = listOf(
                layout.findViewById(R.id.first_photo) as ImageView?,
                layout.findViewById(R.id.second_photo) as ImageView?).filterNotNull()

        val colors = photos.map { randomColor() }
        photos.forEachIndexed {
            index, photo ->
            photo.setImageDrawable(colors[index])
        }

        if (colors.isNotEmpty()) thumbnail?.setImageDrawable(colors[0])
    }

    fun randomColor(): Drawable? {
        currentColor++
        if (currentColor >= colors.size) currentColor = 0
        Log.i("asdf","Using color $currentColor")
        val context = weakActivity?.get()?.baseContext ?: return null
        return ContextCompat.getDrawable(context, colors[currentColor])
    }
}
