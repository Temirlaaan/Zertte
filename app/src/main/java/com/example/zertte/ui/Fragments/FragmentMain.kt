package com.example.zertte.ui.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zertte.databinding.FragmentMainBinding
import com.example.zertte.model.Place
import com.example.zertte.network.Firestore.FirestoreClass
import com.example.zertte.ui.activities.PlaceDetailsActivity
import com.example.zertte.ui.activities.user.SettingsActivity
import com.example.zertte.ui.adapters.MyPlacesMainListAdapter
import com.example.zertte.utils.Constants
import com.example.zertte.utils.GlideLoader


class FragmentMain: BaseFragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        getMainItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(inflater, container, false)

        val context = requireContext()

        val sharedPreferences =
            context.getSharedPreferences(Constants.ZERTTE_PREFERENCES, Context.MODE_PRIVATE)

        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        val firstName = username.split(" ").first()

        val userImage = sharedPreferences.getString(Constants.IMAGE, "")!!

        GlideLoader(context).loadUserPicture(userImage, binding.profilePhoto)


        binding.name.text = firstName

        binding.profilePhoto.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
        }

        return binding.root
    }

    fun successMainItemsList(mainItemsList: ArrayList<Place>){
        hideProgressDialog()

        if(mainItemsList.size > 0){
            binding.rvMainItems.visibility = View.VISIBLE

            binding.rvMainItems.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            binding.rvMainItems.setHasFixedSize(true)

            val adapterPlaces = MyPlacesMainListAdapter(requireActivity(), mainItemsList)
            binding.rvMainItems.adapter = adapterPlaces

            adapterPlaces.setOnClickListener(object: MyPlacesMainListAdapter.OnClickListener{
                override fun onClick(position: Int, place: Place){
                    val intent = Intent(context, PlaceDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PLACE_ID, place.place_id)
                    intent.putExtra(Constants.EXTRA_PLACE_OWNER_ID, place.place_id)
                    startActivity(intent)
                }
            } )
        }else{
            binding.rvMainItems.visibility = View.GONE
        }
    }

    private fun getMainItemsList(){
        showProgressDialog("Please, wait...")

        FirestoreClass().getMainItemsList(this@FragmentMain)
    }
}