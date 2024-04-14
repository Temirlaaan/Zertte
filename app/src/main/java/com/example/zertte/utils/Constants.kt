package com.example.zertte.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val GUIDES: String = "guides"
    const val PLACES: String = "places"
    const val IS_GUIDE = "is_guide"


    const val ZERTTE_PREFERENCES: String = "ZerttePrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    const val LOGGED_IN_GUIDENAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String =  "extra_user_details"
    const val EXTRA_GUIDE_DETAILS: String =  "extra_guide_details"
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val MALE: String =  "male"
    const val FEMALE: String = "female"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val MOBILE: String =  "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val USER_PROFILE_IMAGE:String = "user_profile_image"

    const val GUIDE_PROFILE_IMAGE:String = "guide_profile_image"

    const val COMPLETE_PROFILE: String = "profileCompleted"

    const val PLACE_IMAGE: String = "Place_Image"

    const val GUIDE_ID: String = "user_id"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))

    }
}