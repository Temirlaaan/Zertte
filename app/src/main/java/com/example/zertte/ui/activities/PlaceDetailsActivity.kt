package com.example.zertte.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.zertte.R
import com.example.zertte.databinding.ActivityPlaceDetailsBinding
import com.example.zertte.model.CartItem
import com.example.zertte.model.Place
import com.example.zertte.network.Firestore.FirestoreClass
import com.example.zertte.network.Firestore.FirestoreClassGuides
import com.example.zertte.ui.activities.user.CartListActivity
import com.example.zertte.utils.Constants
import com.example.zertte.utils.GlideLoader

class PlaceDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPlaceDetailsBinding

    private var mPlaceId: String = ""
    private lateinit var mPlaceDetails: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(Constants.EXTRA_PLACE_ID)){
            mPlaceId = intent.getStringExtra(Constants.EXTRA_PLACE_ID)!!

        }
        var placeOwnerId: String = ""


        if(intent.hasExtra(Constants.EXTRA_PLACE_OWNER_ID)){
            placeOwnerId = intent.getStringExtra(Constants.EXTRA_PLACE_OWNER_ID)!!
        }

        if(FirestoreClassGuides().getCurrentGuideID() == placeOwnerId){
            binding.btnAddToCart.visibility = View.GONE
            binding.goToCart.visibility = View.GONE
        }else{
            binding.btnAddToCart.visibility = View.VISIBLE
        }

        getPlaceDetails()

        binding.btnAddToCart.setOnClickListener(this)
        binding.back.setOnClickListener(this)
        binding.goToCart.setOnClickListener(this)
    }

    private fun getPlaceDetails(){
        showProgressDialog("Please, wait...")
        FirestoreClassGuides().getPlaceDetails(this, mPlaceId)
    }

    fun placeExistsInCart(){
        hideProgressDialog()
        binding.btnAddToCart.visibility = View.GONE
        binding.goToCart.visibility = View.VISIBLE


    }

    fun placeDetailsSuccess(place: Place){
        mPlaceDetails = place
        //hideProgressDialog()

        GlideLoader(this@PlaceDetailsActivity).loadPlacePicture(
            place.image,
            binding.locationImage
        )

        binding.placeName.text = place.title
        binding.locationPrice.text = "${place.price} ₸"
        binding.locationDescription.text = place.description
        binding.locationPersonQuantity.text = "${place.person_quantity} adam"

        if(place.person_quantity.toInt() == 0){
            hideProgressDialog()

            binding.btnAddToCart.visibility = View.GONE
            binding.locationPersonQuantity.text = "Bos oryn jok"
            binding.locationPersonQuantity.setTextColor(
                ContextCompat.getColor(
                    this@PlaceDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            if(FirestoreClass().getCurrentUserID() == place.user_id){
                hideProgressDialog()
            }else{
                FirestoreClass().checkIfItemExistInCart(this, mPlaceId)
            }
        }


    }

    private fun addToCart(){
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mPlaceId,
            mPlaceDetails.title,
            mPlaceDetails.price,
            mPlaceDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog("Please, wait...")
        FirestoreClass().addCartItems(this, cartItem)
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@PlaceDetailsActivity,
            "Success adding to cart",
            Toast.LENGTH_LONG
        ).show()

        binding.btnAddToCart.visibility = View.GONE
        binding.goToCart.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id){
                R.id.back ->{
                    onBackPressed()
                }
                R.id.btn_add_to_cart ->{
                    addToCart()
                }
                R.id.go_to_cart ->{
                    startActivity(Intent(this@PlaceDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }
}
