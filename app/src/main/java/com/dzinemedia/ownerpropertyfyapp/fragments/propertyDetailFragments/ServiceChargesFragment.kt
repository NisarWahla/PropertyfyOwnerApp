package com.dzinemedia.ownerpropertyfyapp.fragments.propertyDetailFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.ServiceChargesFragmentAdapter
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentServiceChargesBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ServiceChargesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServiceChargesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentServiceChargesBinding
    private var serviceChargesFragmentAdapter: ServiceChargesFragmentAdapter? = null
    private val serviceChargesList: ArrayList<NotificationModel> = ArrayList()

    companion object {
        fun newInstance(): ServiceChargesFragment {
            val fragment = ServiceChargesFragment()
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
            inflater, R.layout.fragment_service_charges, container, false
        )
        initializeControls()
        return binding.root
    }

    private fun initializeControls() {
        serviceChargesFragmentAdapter = ServiceChargesFragmentAdapter(serviceChargesList)
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.serviceChargesRecyclerview.layoutManager = layoutManager
        binding.serviceChargesRecyclerview.adapter = serviceChargesFragmentAdapter
    }
}