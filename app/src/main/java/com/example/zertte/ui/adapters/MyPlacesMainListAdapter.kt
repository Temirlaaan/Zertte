package com.example.zertte.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zertte.R
import com.example.zertte.model.Place
import com.example.zertte.ui.activities.PlaceDetailsActivity
import com.example.zertte.utils.Constants
import com.example.zertte.utils.GlideLoader

open class MyPlacesMainListAdapter(
    private val context: Context,
    private val list: ArrayList<Place>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_popular_main,
                parent,
                false
            )
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
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

            holder.itemView.setOnClickListener {
                val intent = Intent(context, PlaceDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PLACE_ID, model.place_id)
                intent.putExtra(Constants.EXTRA_PLACE_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

     class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val placeImage: ImageView = view.findViewById(R.id.place_image_main)
        val placeName: TextView = view.findViewById(R.id.place_name_main)
        val placePrice: TextView = view.findViewById(R.id.place_price_main)
    }

    interface OnClickListener{
        fun onClick(position: Int, place: Place)
    }
}