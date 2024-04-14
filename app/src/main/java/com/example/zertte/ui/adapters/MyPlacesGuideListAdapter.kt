package com.example.zertte.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zertte.R
import com.example.zertte.model.Place
import com.example.zertte.ui.Fragments.FragmentPlacesGuide
import com.example.zertte.utils.GlideLoader

open class MyPlacesGuideListAdapter(
    private val context: Context,
    private val list: ArrayList<Place>,
    private val fragment: FragmentPlacesGuide
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
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

             holder.placeDelete.setOnClickListener {
                 fragment.deletePlace(model.place_id)
             }
        }
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val placeImage: ImageView = view.findViewById(R.id.place_image_guide)
        val placeName: TextView = view.findViewById(R.id.place_name_guide)
        val placePrice: TextView = view.findViewById(R.id.place_price_guide)
        val placeDelete: ImageView = view.findViewById(R.id.place_delete)
    }
}

