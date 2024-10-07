package com.dzinemedia.ownerpropertyfyapp.fragments.tenantFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.MyTenantActivity
import com.dzinemedia.ownerpropertyfyapp.activities.MyTenantDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.TenantAllFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.TenantAllFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentAllTenantBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.Data
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.myTenantsModel.MyTenantsModel
import com.dzinemedia.ownerpropertyfyapp.utility.Constants
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
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
 * Use the [AllTenantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllTenantFragment : Fragment() {
    private var isAllTenantFragmentVisible: Boolean = false

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAllTenantBinding
    private var tenantAllFragmentAdapter: TenantAllFragmentAdapter? = null
    private val allTenantFragmentList: ArrayList<Data> = ArrayList()
    private val TAG = "AllTenantFragment"

    companion object {
        fun newInstance(): AllTenantFragment {
            val fragment = AllTenantFragment()
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
            inflater, R.layout.fragment_all_tenant, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        tenantAllFragmentAdapter =
            TenantAllFragmentAdapter(
                allTenantFragmentList,
                this,
                object : TenantAllFragmentItemCallback {
                    override fun allTenantItemClick(position: Int, allTenantModel: Data) {
                        val intent = Intent(requireActivity(), MyTenantDetailActivity::class.java)
                        intent.putExtra("tenantId", allTenantModel.id)
                        /*if (allTenantModel.rentals != null) {
                            if (allTenantModel.rentals!!.isNotEmpty()) {
                                intent.putExtra("propertyId", allTenantModel.rentals!![0]?.propertyId)
                                Log.i(TAG, "allTenantItemClick: ${allTenantModel.rentals!![0]?.propertyId}")
                            }
                        }*/
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            requireActivity(),
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                    }

                })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.tenantAllRecyclerview.layoutManager = layoutManager
        binding.tenantAllRecyclerview.adapter = tenantAllFragmentAdapter
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isAllTenantFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                Log.i(Constants.tag, "setMenuVisibility: AllFragment setMenuVisibilityCalled")
                (activity as MyTenantActivity).getAllTenants()
                    ?.let { getAllTenantsFrgData(it) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getAllTenantsFrgData(myAllTenantsModel: MyTenantsModel) {
        if (activity != null && isAllTenantFragmentVisible) {
            allTenantFragmentList.clear()
            allTenantFragmentList.addAll(myAllTenantsModel.data as ArrayList<Data>)
            if (allTenantFragmentList.size > 0) {
                binding.noData.visibility = View.GONE
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            tenantAllFragmentAdapter?.notifyDataSetChanged()
        }
    }

    fun takePermission(phoneNumber: String) {
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

    fun checkPermissionBehave(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            binding.root.context,
            Manifest.permission.CALL_PHONE
        )

        return result1 == PackageManager.PERMISSION_GRANTED
    }
}