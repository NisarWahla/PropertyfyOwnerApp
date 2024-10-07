package com.dzinemedia.ownerpropertyfyapp.fragments.propertyDetailFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.UtilityBillsFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentUtilityBillsBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UtilityBillsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UtilityBillsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentUtilityBillsBinding
    private var utilityBillsFragmentAdapter: UtilityBillsFragmentAdapter? = null
    private val utilityBillsList: ArrayList<NotificationModel> = ArrayList()

    companion object {
        fun newInstance(): UtilityBillsFragment {
            val fragment = UtilityBillsFragment()
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
            inflater, R.layout.fragment_utility_bills, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        utilityBillsFragmentAdapter = UtilityBillsFragmentAdapter(utilityBillsList)
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.utilityRecyclerview.layoutManager = layoutManager
        binding.utilityRecyclerview.adapter = utilityBillsFragmentAdapter
    }
}