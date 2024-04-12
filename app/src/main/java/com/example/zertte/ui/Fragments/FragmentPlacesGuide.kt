package com.example.zertte.ui.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.zertte.R
import com.example.zertte.ui.activities.guide.AddPlaceActivity
import com.example.zertte.ui.activities.guide.SettingsActivityGuide

class FragmentPlacesGuide: Fragment() {

    private lateinit var addImg : ImageView
    private lateinit var settingsGuide : ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_places_guide, container, false)

        addImg = root.findViewById(R.id.addPlace)
        settingsGuide = root.findViewById(R.id.settingsGuide)

        addImg.setOnClickListener {
            startActivity(Intent(activity, AddPlaceActivity::class.java))
        }

        settingsGuide.setOnClickListener {
            startActivity(Intent(activity, SettingsActivityGuide::class.java))
        }

        return root
    }
}