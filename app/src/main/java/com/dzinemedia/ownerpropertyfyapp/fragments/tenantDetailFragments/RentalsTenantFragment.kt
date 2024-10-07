package com.dzinemedia.ownerpropertyfyapp.fragments.tenantDetailFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.RentalContractDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.TenantRentalsFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RentalItemClick
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentRentalsTenantBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.RentalContract
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.TenantOverviewModel
import com.dzinemedia.ownerpropertyfyapp.viewModel.ShareViewModel
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
 * Use the [RentalsTenantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RentalsTenantFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRentalsTenantBinding
    private var tenantRentalsAdapter: TenantRentalsFragmentAdapter? = null
    private val rentalsArrayList: ArrayList<RentalContract> = ArrayList()
    private var isRentalsFragmentVisible: Boolean = false
    private lateinit var sharedViewModel: ShareViewModel


    companion object {
        fun newInstance(): RentalsTenantFragment {
            val fragment = RentalsTenantFragment()
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
            inflater, R.layout.fragment_rentals_tenant, container, false
        )
        initializeControls()
        return binding.root
    }
    private fun initializeControls() {
        sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        tenantRentalsAdapter = TenantRentalsFragmentAdapter(rentalsArrayList, object : RentalItemClick {
            override fun rentalsClick(position: Int, rentalContractModel: RentalContract) {
                val intent = Intent(requireActivity(), RentalContractDetailActivity::class.java)
                intent.putExtra("propertyId", rentalContractModel.propertyId)
                intent.putExtra("rentalId", rentalContractModel.id)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.rentalsTenantRecyclerview.layoutManager = layoutManager
        binding.rentalsTenantRecyclerview.adapter = tenantRentalsAdapter
        sharedViewModel.getOwnerTenantPropertiesOverview().observe(viewLifecycleOwner) {
            it?.let {
                getRentalsFrgData(it)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getRentalsFrgData(tenantOverviewModel: TenantOverviewModel) {
        if (activity != null && isRentalsFragmentVisible) {
            rentalsArrayList.clear()
            rentalsArrayList.addAll(tenantOverviewModel.data?.rentalContract as ArrayList<RentalContract>)
            tenantRentalsAdapter?.notifyDataSetChanged()
        }

    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isRentalsFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                sharedViewModel.refreshObserverResult()
            }
        }
    }
}