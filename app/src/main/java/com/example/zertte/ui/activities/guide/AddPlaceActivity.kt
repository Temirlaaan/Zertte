package com.example.zertte.ui.activities.guide

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zertte.R
import com.example.zertte.databinding.ActivityAddPlaceBinding
import com.example.zertte.model.Place
import com.example.zertte.ui.activities.BaseActivity
import com.example.zertte.utils.Constants
import com.example.zertte.utils.GlideLoader
import java.io.IOException

class AddPlaceActivity: BaseActivity(), View.OnClickListener  {

    private var mSelectedImageFileURI: Uri? = null
    private var mPlaceImageURL: String = ""

    private lateinit var binding: ActivityAddPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(this)
        binding.ivAddUpdateProduct.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id){
                R.id.back ->{
                    onBackPressed()
                }

                R.id.iv_add_update_product -> {
                    if(ContextCompat.checkSelfPermission(
                        this,
                            Manifest.permission.READ_MEDIA_IMAGES
                    )
                        == PackageManager.PERMISSION_GRANTED
                        ){
                        Constants.showImageChooser(this@AddPlaceActivity)
                    }else{
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {
                    if(validatePlaceDetails()){
                        uploadPlaceImage()
                    }
                }
            }
        }
    }

    private fun uploadPlaceImage(){
        showProgressDialog("Please wait...")
        FirestoreClassGuides().uploadImageToCloudStorage(this, mSelectedImageFileURI, Constants.PLACE_IMAGE)
    }

    fun placeUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@AddPlaceActivity,
            "Ur place uploaded successfully",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun imageUploadSuccess(imageURL: String){

        //hideProgressDialog()

        //showErrorSnackBar("Product image is uploaded successfully. Image URL: $imageURL", false)

        mPlaceImageURL = imageURL

        uploadPlaceDetails()
    }

    private fun uploadPlaceDetails(){
        val username = this.getSharedPreferences(Constants.ZERTTE_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_GUIDENAME, "")!!

        val place = Place(
            FirestoreClassGuides().getCurrentGuideID(),
            username,
            binding.placeTitle.text.toString().trim{it <= ' '},
            binding.placePrice.text.toString().trim{it <= ' '},
            binding.placeDescription.text.toString().trim{it <= ' '},
            binding.personQuantity.text.toString().trim{it <= ' '},
            mPlaceImageURL
            )

        FirestoreClassGuides().uploadPlaceDetails(this, place)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
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
                   binding.ivAddUpdateProduct.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_edit))

                    mSelectedImageFileURI =  data.data!!
                    try{
                        GlideLoader(this).loadUserPicture(mSelectedImageFileURI!!, binding.imgPlace)
                    }catch(e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validatePlaceDetails(): Boolean {
        return when{
            mSelectedImageFileURI == null -> {
                showErrorSnackBar("Please select image file", true)
                false
            }

            TextUtils.isEmpty(binding.title.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar("Please enter place title", true)
                false
            }

            TextUtils.isEmpty(binding.placePrice.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar("Please enter place price", true)
                false
            }

            TextUtils.isEmpty(binding.placeDescription.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar("Please enter place description", true)
                false
            }

            TextUtils.isEmpty(binding.personQuantity.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar("Please enter person quantity", true)
                false
            }

            else -> {
                true
            }
        }
    }


}