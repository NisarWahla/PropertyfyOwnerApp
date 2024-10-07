package com.dzinemedia.ownerpropertyfyapp.fragments.billsFragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.PendingBillsAdapter
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentPendingBillsBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel.Data
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
 * Use the [PendingBillsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PendingBillsFragment : Fragment() {
    private var isFragmentVisible: Boolean = false

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPendingBillsBinding
    private var pendingBillsAdapter: PendingBillsAdapter? = null
    private val pendingArrayList: ArrayList<Data> = ArrayList()
    private lateinit var sharedViewModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    companion object {
        fun newInstance(): PendingBillsFragment {
            val fragment = PendingBillsFragment()
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
            inflater, R.layout.fragment_pending_bills, container, false
        )
        initializeControls()
        return binding.root
    }
    private fun initializeControls() {
        sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]

        pendingBillsAdapter = PendingBillsAdapter(pendingArrayList)
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.pendingRecyclerview.layoutManager = layoutManager
        binding.pendingRecyclerview.adapter = pendingBillsAdapter
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getPendingBillsData(){
        sharedViewModel.getAllBillsData().observe(viewLifecycleOwner) {
            it?.let {
                pendingArrayList.clear()
                it.data?.let { data->
                    for (j in data.indices) {
                        if (data[j]?.paidStatus.equals("pending", ignoreCase = true)) {
                            data[j].let { it1 -> pendingArrayList.add(it1!!) }
                        }
                    }
                }
                if (pendingArrayList.size > 0) {
                    binding.noData.visibility = View.GONE
                } else {
                    binding.noData.visibility = View.VISIBLE
                }
                pendingBillsAdapter?.notifyDataSetChanged()
            }
        }
    }
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                getPendingBillsData()
            }
        }
    }
}