package com.example.zertte.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zertte.MainActivity
import com.example.zertte.auth.BaseActivity
import com.example.zertte.databinding.ActivityUserProfileBinding
import com.example.zertte.network.Firestore.FirestoreClass
import com.example.zertte.network.Firestore.User
import com.example.zertte.utils.Constants
import com.example.zertte.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.example.zertte.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        binding.etFirstName.isEnabled = false
        binding.etFirstName.setText(mUserDetails.firstName)

        binding.etLastName.isEnabled = false
        binding.etLastName.setText(mUserDetails.lastName)

        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUserDetails.email)

        binding.back.setOnClickListener{
            onBackPressed()
        }

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
            if(validateUserProfileDetails()){
                showProgressDialog("Please, wait...")

                if(mSelectedImageFileUri != null)
                FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                else{
                    updateUserProfileDetails()
                }
            }
        }
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()

        val mobileNumber = binding.etMobileNumber.text.toString().trim{ it <= ' ' }

        val gender = if (binding.rbMale.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }

        if(mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if(mobileNumber.isNotEmpty()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            "User profile updated successfully",
            Toast.LENGTH_LONG
        ).show()

        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
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
                            this@UserProfileActivity,
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

    private fun validateUserProfileDetails(): Boolean{
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

        mUserProfileImageURL = imageURL

        val sharedPreferences = getSharedPreferences(Constants.ZERTTE_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.USER_PROFILE_IMAGE, imageURL).apply()


        updateUserProfileDetails()
    }
}

