package com.example.apiwithmvvm.UtilityTools

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.apiwithmvvm.R
import com.example.apiwithmvvm.Retrofit.Const
import com.squareup.picasso.Picasso

// Binding adapter to load images using Glide
@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, imageUrl: String?) {

    if (!imageUrl.isNullOrEmpty()) {
        Picasso.get()
            .load(Const.HOST_URL + imageUrl)
            .placeholder(R.drawable.logo)  // optional placeholder while loading
            .error(R.drawable.logo)              // optional error image
            .into(imageView)
    }
}