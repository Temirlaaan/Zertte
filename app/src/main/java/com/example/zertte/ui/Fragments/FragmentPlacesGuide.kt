package com.example.zertte.ui.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zertte.databinding.FragmentPlacesGuideBinding
import com.example.zertte.model.Place
import com.example.zertte.network.Firestore.FirestoreClassGuides
import com.example.zertte.ui.adapters.MyPlacesGuideListAdapter
import com.example.zertte.ui.activities.guide.AddPlaceActivity
import com.example.zertte.ui.activities.guide.SettingsActivityGuide

class FragmentPlacesGuide: BaseFragment() {
    private lateinit var binding: FragmentPlacesGuideBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPlacesGuideBinding.inflate(inflater, container, false)

        binding.addPlace.setOnClickListener {
            startActivity(Intent(activity, AddPlaceActivity::class.java))
        }

        binding.settingsGuide.setOnClickListener {
            startActivity(Intent(activity, SettingsActivityGuide::class.java))
        }

        return binding.root
    }

    override fun onResume(){
        super.onResume()
        getPlaceListFromFireStore()
    }

    private fun getPlaceListFromFireStore(){

        showProgressDialog("Please wait...")

        FirestoreClassGuides().getPlacesList(this@FragmentPlacesGuide)
    }

    fun deletePlace(placeID: String){
        showAlertDialogToDeletePlace(placeID)
    }

    private fun showAlertDialogToDeletePlace(placeID: String){

        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle("Delete")
        builder.setMessage("Are you sure you want to delete the place?")

        builder.setPositiveButton("Yes"){dialogInterface, _ ->
            showProgressDialog("Please wait...")
            FirestoreClassGuides().deletePlace(this@FragmentPlacesGuide, placeID)

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun placeDeleteSuccess(){
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            "You place was deleted successfully",
            Toast.LENGTH_SHORT
        ).show()

        getPlaceListFromFireStore()
    }


    fun successPlacesListFromFireStore(placesList: ArrayList<Place>) {

        hideProgressDialog()

        if (placesList.size > 0) {
            binding.rvMyPlaceItems.visibility = View.VISIBLE
            binding.tvNoPlacesFound.visibility = View.GONE

            binding.rvMyPlaceItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyPlaceItems.setHasFixedSize(true)

            val adapterPlaces = MyPlacesGuideListAdapter(requireActivity(), placesList, this)
            binding.rvMyPlaceItems.adapter = adapterPlaces
        } else {
            binding.rvMyPlaceItems.visibility = View.GONE
            binding.tvNoPlacesFound.visibility = View.VISIBLE
        }
    }
}