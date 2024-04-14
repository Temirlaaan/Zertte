package com.example.zertte.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
        val user_id: String = "",
        val user_name: String = "",
        val title: String = "",
        val price: String = "",
        val description: String = "",
        val location: String = "",
        val image: String = "",
        val person_quantity: String = "",
        val rating: String = "",
        var place_id: String = "",
): Parcelable