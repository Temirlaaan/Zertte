package com.example.zertte.ui.activities.user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zertte.R
import com.example.zertte.databinding.ActivityCartListBinding
import com.example.zertte.model.CartItem
import com.example.zertte.model.Place
import com.example.zertte.network.Firestore.FirestoreClass
import com.example.zertte.ui.activities.BaseActivity
import com.example.zertte.ui.adapters.CartItemsListAdapter

class CartListActivity : BaseActivity(), View.OnClickListener  {

    private lateinit var binding: ActivityCartListBinding
    private lateinit var mPlacesLists: ArrayList<Place>
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener(this)
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>){
        hideProgressDialog()

        for(place in mPlacesLists){
            for(cartItem in cartList){
                if(place.place_id == cartItem.place_id){

                    cartItem.stock_quantity = place.person_quantity

                    if(place.person_quantity.toInt() == 0){
                        cartItem.cart_quantity = place.person_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if(mCartListItems.size > 0){
            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.checkoutCardView.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
            binding.rvCartItemsList.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, cartList)
            binding.rvCartItemsList.adapter = cartListAdapter

            var subTotal: Double = 0.0
            for(item in mCartListItems){
                val availableQuantity = item.stock_quantity.toInt()
                if(availableQuantity > 0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }


            }
            binding.tvSubtotalPrice.text = "${subTotal} ₸"
            binding.tvShippingPrice.text = "0.0 ₸"

            if(subTotal > 0){
                binding.checkoutCardView.visibility = View.VISIBLE

                val total = subTotal + 10
                binding.tvTotalPrice.text = "$total ₸"
            }else{
                binding.checkoutCardView.visibility = View.GONE
            }
        }else{
            binding.rvCartItemsList.visibility = View.GONE
            binding.checkoutCardView.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    fun successPlacesListFromFireStore(placesList: ArrayList<Place>){
        hideProgressDialog()

        mPlacesLists = placesList

        getCartItemsList()
    }

    private fun getPlaceList(){
        showProgressDialog("Please, wait...")
        FirestoreClass().getAllPlacesList(this@CartListActivity)
    }

    private fun getCartItemsList(){
        //showProgressDialog("Please, wait...")
        FirestoreClass().getCartList(this@CartListActivity)
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemsList()
    }

    override fun onResume() {
        super.onResume()
        //
        getPlaceList()
    }

    fun itemRemoveSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@CartListActivity,
            "Removed successfully",
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
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