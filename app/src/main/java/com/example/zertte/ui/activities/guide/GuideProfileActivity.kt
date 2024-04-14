package com.example.zertte.ui.activities.guide

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zertte.MainActivityGuide
import com.example.zertte.R
import com.example.zertte.databinding.ActivityGuideProfileBinding
import com.example.zertte.model.Guide
import com.example.zertte.network.Firestore.FirestoreClassGuides
import com.example.zertte.ui.activities.BaseActivity
import com.example.zertte.utils.Constants
import com.example.zertte.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.example.zertte.utils.GlideLoader
import java.io.IOException


class GuideProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityGuideProfileBinding
    private lateinit var mGuideDetails: Guide
    private var mSelectedImageFileUri: Uri? = null
    private var mGuideProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(Constants.EXTRA_GUIDE_DETAILS)){
            mGuideDetails = intent.getParcelableExtra(Constants.EXTRA_GUIDE_DETAILS)!!
        }

        binding.etFirstName.setText(mGuideDetails.firstName)
        binding.etLastName.setText(mGuideDetails.lastName)
        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mGuideDetails.email)

        if(mGuideDetails.profileCompleted == 0){
            binding.tvProfile.text = "Complete Profile"

            binding.etFirstName.isEnabled = false

            binding.etLastName.isEnabled = false

            binding.back.visibility = View.GONE
        }else{
            binding.tvProfile.text = "Edit Profile"
            GlideLoader(this@GuideProfileActivity).loadUserPicture(mGuideDetails.image, binding.imgProfile)

            binding.etEmail.isEnabled = false
            binding.etEmail.setText(mGuideDetails.email)

            if(mGuideDetails.mobile != 0L){
                binding.etMobileNumber.setText(mGuideDetails.mobile.toString())
            }

            if(mGuideDetails.gender == Constants.MALE){
                binding.rbMale.isChecked = true
            }else{
                binding.rbFemale.isChecked = true
            }

            binding.back.visibility = View.VISIBLE
        }

        binding.back.setOnClickListener(this)

        binding.imgProfile.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+ (API level 33+)
                requestReadMediaImagesPermission()
            } else {
                // For older Android versions
                requestReadExternalStoragePermission()
            }
        }

        binding.btnSave.setOnClickListener {
            if(validateGuideProfileDetails()){
                showProgressDialog("Please, wait...")

                if(mSelectedImageFileUri != null)
                    FirestoreClassGuides().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.GUIDE_PROFILE_IMAGE)
                else{
                    updateGuideProfileDetails()
                }
            }
        }
    }

    private fun updateGuideProfileDetails(){
        val guideHashMap = HashMap<String, Any>()

        val firstName = binding.etFirstName.text.toString().trim{ it <= ' ' }
        if(firstName != mGuideDetails.firstName){
            guideHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = binding.etLastName.text.toString().trim{ it <= ' ' }
        if(firstName != mGuideDetails.lastName){
            guideHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = binding.etMobileNumber.text.toString().trim{ it <= ' ' }

        val gender = if (binding.rbMale.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }

        if(mGuideProfileImageURL.isNotEmpty()){
            guideHashMap[Constants.IMAGE] = mGuideProfileImageURL
        }

        if(mobileNumber.isNotEmpty() && mobileNumber != mGuideDetails.mobile.toString()){
            guideHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if(gender.isNotEmpty() && gender != mGuideDetails.gender){
            guideHashMap[Constants.GENDER] = gender
        }

        guideHashMap[Constants.GENDER] = gender

        guideHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClassGuides().updateGuideProfileData(this, guideHashMap)
    }

    fun guideProfileUpdateSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this@GuideProfileActivity,
            "User profile updated successfully",
            Toast.LENGTH_LONG
        ).show()

        startActivity(Intent(this@GuideProfileActivity, MainActivityGuide::class.java))
        finish()
    }

    private fun requestReadMediaImagesPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                READ_STORAGE_PERMISSION_CODE
            )
        } else {
            // Permission already granted, proceed with image selection
            Constants.showImageChooser(this)
        }
    }

    private fun requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE
            )
        } else {
            // Permission already granted, proceed with image selection
            Constants.showImageChooser(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  showErrorSnackBar("Granted", false)
                Constants.showImageChooser(this)
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied to access images.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode  == Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if(data != null){
                    try{
                        mSelectedImageFileUri = data.data!!
                        //binding.imgProfile.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.imgProfile)
                    }catch(e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@GuideProfileActivity,
                            "Image selection failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateGuideProfileDetails(): Boolean{
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar("Please enter your phone number", true)
                false
            }else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String){

        mGuideProfileImageURL = imageURL

        updateGuideProfileDetails()
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id){
                R.id.back ->{
                    onBackPressed()
                }
            }
        }
    }
}
