package com.dzinemedia.ownerpropertyfyapp.fragments.propertyDetailFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.ManagementDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.ManagementFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RentalItemClick
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentManagementBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.RentalContract

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManagementFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentManagementBinding
    private var managementFragmentAdapter: ManagementFragmentAdapter? = null
    private val managementArrayList: ArrayList<RentalContract> = ArrayList()

    companion object {
        fun newInstance(): ManagementFragment {
            val fragment = ManagementFragment()
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
            inflater, R.layout.fragment_management, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        managementFragmentAdapter = ManagementFragmentAdapter(managementArrayList, object : RentalItemClick {
            override fun rentalsClick(position: Int, rentalContract: RentalContract) {
                val intent = Intent(requireActivity(), ManagementDetailActivity::class.java)
                startActivity(intent)
            }
        })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.managementRecyclerview.layoutManager = layoutManager
        binding.managementRecyclerview.adapter = managementFragmentAdapter
    }
}