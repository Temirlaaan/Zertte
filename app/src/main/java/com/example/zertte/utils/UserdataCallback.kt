package com.example.zertte.utils

import com.example.zertte.model.User

interface UserdataCallback {
    fun onUserDetailsSuccess(user: User)
}