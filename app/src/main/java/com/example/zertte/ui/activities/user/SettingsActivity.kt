package com.example.zertte.ui.activities.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.zertte.R
import com.example.zertte.databinding.ActivitySettingsBinding
import com.example.zertte.model.User
import com.example.zertte.network.Firestore.FirestoreClass
import com.example.zertte.ui.activities.BaseActivity
import com.example.zertte.utils.Constants
import com.example.zertte.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity: BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edit.setOnClickListener(this)
        binding.logOut.setOnClickListener(this)
        binding.back.setOnClickListener(this)
        binding.tapsyrystarClient.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }


    private fun getUserDetails(){
        showProgressDialog("Please, wait...")
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User){
        mUserDetails = user

        hideProgressDialog()

        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, binding.imgViewProfile)
        binding.nameSurname.text = "${user.firstName} ${user.lastName}"
        binding.gender.text = user.gender
        binding.email.text = user.email
        binding.phone.text = user.mobile.toString()
    }


    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id){
                R.id.edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                 }

                R.id.logOut ->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, ActivityLogin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.back ->{
                    onBackPressed()
                }

                R.id.tapsyrystarClient ->{
                    startActivity(Intent(this@SettingsActivity, CartListActivity::class.java))
                }
            }
        }
    }
}