package com.example.zertte.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zertte.R
import com.example.zertte.model.CartItem
import com.example.zertte.network.Firestore.FirestoreClass
import com.example.zertte.ui.activities.user.CartListActivity
import com.example.zertte.utils.Constants
import com.example.zertte.utils.GlideLoader

class CartItemsListAdapter(
    private val context: Context,
    private val list: ArrayList<CartItem>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cart_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            GlideLoader(context).loadPlacePicture(model.image, holder.placeImage)

            holder.placeName.text = model.title
            holder.placePrice.text = "${model.price} ₸"
            holder.howMuch.text = model.cart_quantity

            if(model.cart_quantity == "0"){
                holder.removeItem.visibility = View.GONE
                holder.addItem.visibility = View.GONE

                holder.howMuch.text = "Bos oryn jok"

                holder.howMuch.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            }else{
                holder.removeItem.visibility = View.VISIBLE
                holder.addItem.visibility = View.VISIBLE

                holder.howMuch.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarSuccess
                    )
                )
            }

            holder.placeDelete.setOnClickListener {
                when(context){
                    is CartListActivity ->{
                        context.showProgressDialog("Please, wait...")

                    }
                }
                FirestoreClass().removeItemFromCart(context, model.id)
            }

            holder.removeItem.setOnClickListener{
                if (model.cart_quantity == "1"){
                    FirestoreClass().removeItemFromCart(context, model.id)
                }else{

                    val cartQuantity: Int = model.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    //Show the progress dialog.

                    if(context is CartListActivity){
                        context.showProgressDialog("Please, wait...")
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }
            }

            holder.addItem.setOnClickListener{
                val cartQuantity: Int = model.cart_quantity.toInt()

                if(cartQuantity < model.stock_quantity.toInt()){
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    if(context is CartListActivity){
                        context.showProgressDialog("Please, wait...")
                    }
                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }else{
                    if(context is CartListActivity){
                        context.showErrorSnackBar(
                            "Berilgen adam sany ote kop",
                        true
                        )
                    }
                }
            }

        }
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val placeImage: ImageView = view.findViewById(R.id.place_image_cart)
        val placeName: TextView = view.findViewById(R.id.place_name_cart)
        val placePrice: TextView = view.findViewById(R.id.place_price_cart)
        val placeDelete: ImageView = view.findViewById(R.id.place_delete_cart)

        val removeItem: ImageButton = view.findViewById(R.id.ib_remove_cart_item)
        val addItem: ImageButton = view.findViewById(R.id.ib_add_cart_item)
        val howMuch: TextView = view.findViewById(R.id.how_much)
    }
}