package io.kokoichi.sample.mastodonclient

import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("spannedContent")   // メソッドをDataBindingからSpannedContetn属性として利用する
fun TextView.setSpannedString(content: String) {
    text = HtmlCompat.fromHtml(
        content,
        HtmlCompat.FROM_HTML_MODE_COMPACT
    )
}

@BindingAdapter("media")
fun ImageView.setMedia(media: Media?) {
    if (media == null) {
        setImageDrawable(null)
        return
    }
    Glide.with(this)
        .load(media.url)
        .into(this)
}
