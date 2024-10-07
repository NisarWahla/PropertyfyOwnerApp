package com.dzinemedia.ownerpropertyfyapp.fragments.tenantDetailFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentInformationTenantBinding
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentOverviewTenantBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.TenantOverviewModel
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.ShareViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.permissionx.guolindev.PermissionX
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
 * Use the [InformationTenantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InformationTenantFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = "InformationTenantFragme"
    private lateinit var binding: FragmentInformationTenantBinding
    private var isInfoFragmentVisible = false
    private var phoneNumber: String? = null
    private lateinit var sharedViewModel: ShareViewModel

    companion object {
        fun newInstance(): InformationTenantFragment {
            val fragment = InformationTenantFragment()
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
            inflater, R.layout.fragment_information_tenant, container, false
        )
        initializeControls()
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.btnCallNow.setOnClickListener(DebounceClickHandler {
            if (checkPermissionBehave()) {
                Utils.makePhoneCall(binding.root.context, phoneNumber.toString())
            } else {
                takePermission(phoneNumber.toString())
            }
        })
    }

    private fun initializeControls() {
        sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        sharedViewModel.getOwnerTenantPropertiesOverview().observe(viewLifecycleOwner) {
            it?.let {
                getInfoFrgData(it)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getInfoFrgData(tenantOverviewModel: TenantOverviewModel) {
        if (activity != null && isInfoFragmentVisible) {
            Log.i(TAG, "getInfoFrgData: ${tenantOverviewModel.message}")
            binding.tenantName.text = tenantOverviewModel.data?.name
            binding.addressProperty.text = tenantOverviewModel.data?.rentalDetails?.selectedProperty?.address
            binding.phoneValue.text =
                tenantOverviewModel.data?.phone1
            binding.emailValue.text = tenantOverviewModel.data?.email
            binding.countryValue.text = tenantOverviewModel.data?.country
            phoneNumber = tenantOverviewModel.data?.phone1
        }
    }

    private fun takePermission(phoneNumber: String) {
        PermissionX.init(this).permissions(
            Manifest.permission.CALL_PHONE
        ).onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                getString(R.string.core_fundamental),
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                getString(R.string.need_to_allow_necessary_permission),
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                Utils.makePhoneCall(binding.root.context, phoneNumber)
            } else {
                Log.i(TAG, "takePermission: Permission denied!")
            }
        }
    }

    private fun checkPermissionBehave(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            binding.root.context,
            Manifest.permission.CALL_PHONE
        )

        return result1 == PackageManager.PERMISSION_GRANTED
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isInfoFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                sharedViewModel.refreshObserverResult()
            }
        }
    }
}