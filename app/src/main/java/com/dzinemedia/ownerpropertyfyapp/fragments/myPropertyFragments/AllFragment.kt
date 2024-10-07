package com.dzinemedia.ownerpropertyfyapp.fragments.myPropertyFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.MyPropertyActivity
import com.dzinemedia.ownerpropertyfyapp.activities.PropertyDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.AllFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.AllFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentAllBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.Data
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerProperties.OwnerPropertiesModel
import com.dzinemedia.ownerpropertyfyapp.utility.Constants
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
 * Use the [AllFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllFragment : Fragment() {
    private var isAllFragmentVisible: Boolean = false

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAllBinding
    private var allFragmentAdapter: AllFragmentAdapter? = null
    private val allFragmentList: ArrayList<Data> = ArrayList()

    companion object {
        fun newInstance(): AllFragment {
            val fragment = AllFragment()
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
            inflater, R.layout.fragment_all, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        allFragmentAdapter =
            AllFragmentAdapter(allFragmentList, object : AllFragmentItemCallback {
                override fun allFragmentItemClick(position: Int, ownerPropertyModel: Data) {
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
        binding.supportRecyclerview.layoutManager = layoutManager
        binding.supportRecyclerview.adapter = allFragmentAdapter
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isAllFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                    Log.i(Constants.tag, "setMenuVisibility: AllFragment setMenuVisibilityCalled")
                (activity as MyPropertyActivity).getOwnerPropertyModel()
                    ?.let { getAllFrgData(it) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getAllFrgData(ownerPropertiesModel: OwnerPropertiesModel) {
        if (activity != null && isAllFragmentVisible) {
            allFragmentList.clear()
            allFragmentList.addAll(ownerPropertiesModel.data as ArrayList<Data>)
            if (allFragmentList.size > 0) {
                binding.noData.visibility = View.GONE
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            allFragmentAdapter?.notifyDataSetChanged()
        }
    }
}