package com.example.zertte.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zertte.databinding.TabFragmentPikirlerBinding
import com.example.zertte.databinding.TabFragmentTolygyrakBinding

class TabFragmentPikirler: Fragment() {
    private var _binding: TabFragmentPikirlerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabFragmentPikirlerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}