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
import com.dzinemedia.ownerpropertyfyapp.adapters.VacantFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.VacantFragmentItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentVacantBinding
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
 * Use the [VacantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VacantFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var isVacantFragmentVisible: Boolean = false
    private lateinit var binding: FragmentVacantBinding
    private var vacantFragmentAdapter: VacantFragmentAdapter? = null
    private val vacantList: ArrayList<Data> = ArrayList()

    companion object {
        fun newInstance(): VacantFragment {
            val fragment = VacantFragment()
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
            inflater, R.layout.fragment_vacant, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        vacantFragmentAdapter =
            VacantFragmentAdapter(vacantList, object : VacantFragmentItemCallback {
                override fun vacantFragmentItemClick(position: Int, ownerPropertyModel: Data) {
                    val intent = Intent(requireActivity(), PropertyDetailActivity::class.java)
                    intent.putExtra("propertyId", ownerPropertyModel.id)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left
                    )
                }
            })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.vacantRecycler.layoutManager = layoutManager
        binding.vacantRecycler.adapter = vacantFragmentAdapter
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isVacantFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                (activity as MyPropertyActivity).getOwnerPropertyModel()
                    ?.let {
                        getVacantFrgData(it)
                    }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getVacantFrgData(ownerPropertiesModel: OwnerPropertiesModel) {
        if (activity != null && isVacantFragmentVisible) {
            vacantList.clear()
            if (ownerPropertiesModel.data?.size!! > 0) {
                for (i in 0 until ownerPropertiesModel.data?.size!!) {
                    val propertyStatus = ownerPropertiesModel.data?.get(i)?.propertyStatus?.toInt()
                    if (propertyStatus == 0) {
                        ownerPropertiesModel.data!![i]?.let { vacantList.add(it) }
                    }
                }
            }
            if (vacantList.size > 0) {
                binding.noData.visibility = View.GONE
            } else {
                binding.noData.visibility = View.VISIBLE
            }
            vacantFragmentAdapter?.notifyDataSetChanged()
        }
    }
}