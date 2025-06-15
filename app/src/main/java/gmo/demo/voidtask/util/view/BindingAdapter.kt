package gmo.demo.voidtask.util.view

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@BindingAdapter("imagePath")
fun ImageView.setImagePath(path : String) {
    this.let {
        Glide.with(it)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(it)
    }
}

@BindingAdapter("isVisible")
fun View.isVisible(visible: Boolean) {
    isVisible = visible
}

@BindingAdapter("textAny")
fun TextView.setTextAny(value : Any) {
    text = value.toString()
}

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String? = "") {
    if (imageUrl?.isNotEmpty() == true && imageUrl.isNotBlank())
        Glide.with(view.context)
        .load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .into(view)
}

@BindingAdapter("android:backgroundImage")
fun setBackgroundImageUrl(view: View, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    view.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Xử lý khi ảnh bị clear (nếu cần)
                }
            })
    }
}


@SuppressLint("SetTextI18n")
@BindingAdapter("getDateNow")
fun getDateNow(textView: MaterialTextView, prefix: String) {
    val currentTime = Calendar.getInstance().time
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    textView.text = "$prefix ${dateFormatter.format(currentTime)}"
}

@BindingAdapter("android:visibility")
fun View.isVisibleView(state: Boolean) {
    visibility = if (state) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("android:textStyle")
fun setTextStyle(textView: TextView, style: String) {
    when (style) {
        "bold" -> textView.setTypeface(null, Typeface.BOLD)
        "italic" -> textView.setTypeface(null, Typeface.ITALIC)
        "bold_italic" -> textView.setTypeface(null, Typeface.BOLD_ITALIC)
        else -> textView.setTypeface(null, Typeface.NORMAL)
    }
}

@BindingAdapter("debouncedClick")
fun View.setDebouncedClickListener(clickListener: View.OnClickListener) {
    val handler = Handler(Looper.getMainLooper())
    var lastClickTime = 0L

    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= 600L) {
            lastClickTime = currentTime
            handler.post { clickListener.onClick(this) }
        }
    }
}