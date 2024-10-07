package com.dzinemedia.ownerpropertyfyapp.fragments.propertyDetailFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.PropertyDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.AmenitiesChipAdapter
import com.dzinemedia.ownerpropertyfyapp.adapters.FurnishingChipAdapter
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentInfoBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.OwnerPropertyOverviewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {
    private var isAllFragmentVisible: Boolean = false

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentInfoBinding
    private val amenitiesList: ArrayList<Any> = ArrayList()
    private val furnishingList: ArrayList<Any> = ArrayList()

    companion object {
        fun newInstance(): InfoFragment {
            val fragment = InfoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_info, container, false
        )
        initializeControls()
        setViewClickListener()
        return binding.root
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isAllFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                (activity as PropertyDetailActivity).getOwnerPropertiesOverviewModel()
                    ?.let { getInfoFrgData(it) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    fun getInfoFrgData(ownerPropertyOverviewModel: OwnerPropertyOverviewModel) {
        if (activity != null && isAllFragmentVisible) {
            binding.addressValue.text = ownerPropertyOverviewModel.data?.address
            binding.paymentIdValue.text = ownerPropertyOverviewModel.data?.mpId
            binding.txtBedroomsValue.text =
                ownerPropertyOverviewModel.data?.propertyInfo?.bedrooms.toString()
            binding.txtParkValue.text =
                ownerPropertyOverviewModel.data?.propertyInfo?.parkings.toString()
            binding.txtWashValue.text =
                ownerPropertyOverviewModel.data?.propertyInfo?.bathrooms.toString()
            binding.areaValue.text =
                "${ownerPropertyOverviewModel.data?.areaType.toString()} sq.ft"
            amenitiesList.clear()
            amenitiesList.addAll(ownerPropertyOverviewModel.data?.propertyAmenities as ArrayList<Any>)
            if (amenitiesList.size > 0) {
                AmenitiesChipAdapter(
                    binding.root.context,
                    binding.amenitiesChipGroup,
                    amenitiesList
                ).displayInitialItems()
            }
            furnishingList.clear()
            furnishingList.addAll(ownerPropertyOverviewModel.data?.furnishDetails as ArrayList<Any>)
            if (furnishingList.size > 0) {
                FurnishingChipAdapter(
                    binding.root.context,
                    binding.furnishingChipGroup,
                    furnishingList
                ).displayInitialItems()
            }
        }
    }

    private fun setViewClickListener() {

    }

    private fun initializeControls() {


    }
}