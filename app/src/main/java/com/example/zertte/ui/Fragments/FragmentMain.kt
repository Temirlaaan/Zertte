package com.example.zertte.ui.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.zertte.R
import com.example.zertte.databinding.FragmentMainBinding
import com.example.zertte.model.User
import com.example.zertte.ui.activities.SettingsActivity
import com.example.zertte.utils.Constants


class FragmentMain: Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mUserDetails: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMainBinding.bind(view)

        val context = requireContext()

        val sharedPreferences =
            context.getSharedPreferences(Constants.ZERTTE_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        val firstName = username.split(" ").first() // Получить только имя

        binding.name.text = firstName

        binding.profilePhoto.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding when the view is destroyed
    }
}