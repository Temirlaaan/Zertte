package com.example.zertte

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.zertte.details.tabs.TabFragmentPikirler
import com.example.zertte.details.tabs.TabFragmentSuretter
import com.example.zertte.details.tabs.TabFragmentTolygyrak

class ViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        TabFragmentTolygyrak(),
        TabFragmentSuretter(),
        TabFragmentPikirler()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}