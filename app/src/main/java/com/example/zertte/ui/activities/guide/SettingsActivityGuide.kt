package com.example.zertte.ui.activities.guide

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.zertte.R
import com.example.zertte.databinding.ActivitySettingsGuidesBinding
import com.example.zertte.model.Guide
import com.example.zertte.network.Firestore.FirestoreClassGuides
import com.example.zertte.ui.activities.BaseActivity
import com.example.zertte.ui.activities.user.ActivityLogin
import com.example.zertte.utils.Constants
import com.example.zertte.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivityGuide: BaseActivity(), View.OnClickListener {

    private lateinit var mGuideDetails: Guide

    private lateinit var binding: ActivitySettingsGuidesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsGuidesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edit.setOnClickListener(this)
        binding.logOut.setOnClickListener(this)
        binding.back.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getGuideDetails()
    }


    private fun getGuideDetails(){
        showProgressDialog("Please, wait...")
        FirestoreClassGuides().getGuideDetails(this@SettingsActivityGuide)
    }

    fun guideDetailsSuccess(guide: Guide){
        mGuideDetails = guide

        hideProgressDialog()

        GlideLoader(this@SettingsActivityGuide).loadUserPicture(guide.image, binding.imgViewProfile)
        binding.nameSurname.text = "${guide.firstName} ${guide.lastName}"
        binding.gender.text = guide.gender
        binding.email.text = guide.email
        binding.phone.text = guide.mobile.toString()
    }


    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id){
                R.id.edit -> {
                    val intent = Intent(this@SettingsActivityGuide, GuideProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_GUIDE_DETAILS, mGuideDetails)
                    startActivity(intent)
                }

                R.id.logOut ->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivityGuide, ActivityLogin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.back ->{
                    onBackPressed()
                }
            }
        }
    }
}