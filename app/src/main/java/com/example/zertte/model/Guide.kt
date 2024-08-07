package com.example.zertte.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Guide (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0): Parcelable