package com.example.zertte.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.zertte.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserPicture(image: Any, imageView: ImageView){
        try{
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.greetings)
                .into(imageView)
        }catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadPlacePicture(image: Any, imageView: ImageView){
        try{
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .into(imageView)
        }catch (e: IOException) {
            e.printStackTrace()
        }
    }
}