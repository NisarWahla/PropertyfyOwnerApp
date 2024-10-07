package com.dzinemedia.ownerpropertyfyapp.fragments.myPropertyFragments

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
import com.dzinemedia.ownerpropertyfyapp.activities.MyPropertyActivity
import com.dzinemedia.ownerpropertyfyapp.activities.PropertyDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.SaleFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.SaleFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentSaleBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.Data
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.OwnerPropertiesModel
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
 * Use the [SaleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SaleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var isRentedFragmentVisible: Boolean = false
    private lateinit var binding: FragmentSaleBinding
    private var saleFragmentAdapter: SaleFragmentAdapter? = null
    private val saleList: ArrayList<Data> = ArrayList()

    companion object {
        fun newInstance(): SaleFragment {
            val fragment = SaleFragment()
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
            inflater, R.layout.fragment_sale, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        saleFragmentAdapter =
            SaleFragmentAdapter(saleList, object : SaleFragmentItemCallback {
                override fun saleFragmentItemClick(position: Int, ownerPropertyModel: Data) {
                    val intent = Intent(requireActivity(), PropertyDetailActivity::class.java)
                    intent.putExtra("propertyId", ownerPropertyModel.id)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                }
            })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.rentedRecycler.layoutManager = layoutManager
        binding.rentedRecycler.adapter = saleFragmentAdapter
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isRentedFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                (activity as MyPropertyActivity).getOwnerPropertyModel()
                    ?.let {
                        getSaleFrgData(it)
                    }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getSaleFrgData(ownerPropertiesModel: OwnerPropertiesModel) {
        if (activity != null && isRentedFragmentVisible) {
            saleList.clear()
            if (ownerPropertiesModel.data?.size!! > 0) {
                for (i in 0 until ownerPropertiesModel.data?.size!!) {
                    val propertyStatus = ownerPropertiesModel.data?.get(i)?.propertyStatus?.toInt()
                    val propertyFor = ownerPropertiesModel.data?.get(i)?.propertyFor
                    if ((propertyFor.equals(
                            "Rent&sale",
                            true
                        ) || propertyFor.equals("Sale", true))
                    ) {
                        ownerPropertiesModel.data!![i]?.let { saleList.add(it) }
                    }
                }
            }
            if (saleList.size > 0) {
                binding.noData.visibility = View.GONE
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            saleFragmentAdapter?.notifyDataSetChanged()
        }
    }
}