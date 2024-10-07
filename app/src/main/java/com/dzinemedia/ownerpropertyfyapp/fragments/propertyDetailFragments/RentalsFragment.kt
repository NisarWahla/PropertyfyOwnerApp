package com.dzinemedia.ownerpropertyfyapp.fragments.propertyDetailFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.PropertyDetailActivity
import com.dzinemedia.ownerpropertyfyapp.activities.RentalContractDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.RentalsFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RentalItemClick
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RentalRowClick
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentRentalsBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.OwnerPropertyOverviewModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyOverview.RentalContract
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
 * Use the [RentalsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RentalsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRentalsBinding
    private var rentalsFragmentAdapter: RentalsFragmentAdapter? = null
    private val rentalsArrayList: ArrayList<RentalContract> = ArrayList()
    private var isRentalsFragmentVisible: Boolean = false
    private var ownerPropertyOverviewModel: OwnerPropertyOverviewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        fun newInstance(): RentalsFragment {
            val fragment = RentalsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_rentals, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        rentalsFragmentAdapter = RentalsFragmentAdapter(rentalsArrayList, object : RentalRowClick {
            override fun rentalsClick(position: Int, rentalContract: RentalContract) {
                val intent = Intent(requireActivity(), RentalContractDetailActivity::class.java)
                intent.putExtra("propertyId", rentalContract.propertyId)
                intent.putExtra("rentalId", rentalContract.id)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.rentalsRecyclerview.layoutManager = layoutManager
        binding.rentalsRecyclerview.adapter = rentalsFragmentAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getRentalsFrgData(ownerPropertyOverviewModel: OwnerPropertyOverviewModel) {
        if (activity != null && isRentalsFragmentVisible) {
            this.ownerPropertyOverviewModel = ownerPropertyOverviewModel
            rentalsArrayList.clear()
            rentalsArrayList.addAll(ownerPropertyOverviewModel.data?.appSection?.rentalContracts as ArrayList<RentalContract>)
            rentalsFragmentAdapter?.notifyDataSetChanged()
        }

    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isRentalsFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                (activity as PropertyDetailActivity).getOwnerPropertiesOverviewModel()
                    ?.let { getRentalsFrgData(it) }
            }
        }
    }
    fun updateData(ownerPropertyOverviewModel: OwnerPropertyOverviewModel) {
        if (isRentalsFragmentVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                getRentalsFrgData(ownerPropertyOverviewModel)
            }
        }

    }
}