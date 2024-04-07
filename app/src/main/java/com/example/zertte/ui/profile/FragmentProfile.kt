package com.example.zertte.profile

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.zertte.auth.BaseActivity
import com.example.zertte.network.Firestore.FirestoreClass

class FragmentProfile : Fragment() {

    private val baseActivity = BaseActivity()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun getUserDetails(activity: Activity){
        baseActivity.showProgressDialog("Please, wait...")
        FirestoreClass().getUserDetails(activity)
    }
}