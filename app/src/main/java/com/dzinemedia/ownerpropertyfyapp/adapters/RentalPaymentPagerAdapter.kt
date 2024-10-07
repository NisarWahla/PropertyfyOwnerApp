package com.dzinemedia.ownerpropertyfyapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments.OverdueFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments.PartialFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments.ReceivedFragment
import com.dzinemedia.ownerpropertyfyapp.fragments.rentalHistoryDetailsFragments.UpcomingFragment

class RentalPaymentPagerAdapter: FragmentStateAdapter {

    constructor(fragmentIntence: FragmentActivity) : super(fragmentIntence)
    constructor(fragmentIntence: Fragment) : super(fragmentIntence)

    private val mFragmentList = mutableListOf<Fragment>()
    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    fun addFrag(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    fun removeFrag(index: Int) {
        mFragmentList.removeAt(index)
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }
}