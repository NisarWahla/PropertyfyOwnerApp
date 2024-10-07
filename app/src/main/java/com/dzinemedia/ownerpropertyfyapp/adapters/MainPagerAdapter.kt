package com.dzinemedia.ownerpropertyfyapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dzinemedia.ownerpropertyfyapp.fragments.mainFragments.HomeFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.mainFragments.NotificationFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.mainFragments.ProfileFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment()
            1 -> HomeFragment()
            2 -> NotificationFragment()
            else -> HomeFragment()
        }
    }
}