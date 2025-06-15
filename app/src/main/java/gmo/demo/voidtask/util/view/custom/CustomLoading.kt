package gmo.demo.voidtask.util.view.custom

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import gmo.demo.voidtask.R

class CustomLoading : LinearLayoutCompat {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun startAnimation() {
        startAnimation(findViewById(R.id.loading_1), 0)
        startAnimation(findViewById(R.id.loading_2), 100)
        startAnimation(findViewById(R.id.loading_3), 200)
    }

    private fun startAnimation(image: ImageView?, delay: Long) {
        image?.postDelayed({
            image.setBackgroundResource(R.drawable.view_custom_loading)
            (image.background as? AnimationDrawable)?.start()
        }, delay)
    }

    fun stopAnimation() {
        findViewById<ImageView>(R.id.loading_1)?.background = null
        findViewById<ImageView>(R.id.loading_2)?.background = null
        findViewById<ImageView>(R.id.loading_3)?.background = null
    }
}