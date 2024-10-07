package com.dzinemedia.ownerpropertyfyapp.fragments.billsFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.BillsPaidDetailActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.PaidBillsAdapter
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.PaidBillsItemCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentPaidBillsBinding
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
 * Use the [PaidBillsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaidBillsFragment : Fragment() {
    private var isFragmentVisible: Boolean = false

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPaidBillsBinding
    private var paidBillsAdapter: PaidBillsAdapter? = null
    private val paidArrayList: ArrayList<Data> = ArrayList()
    private lateinit var sharedViewModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        fun newInstance(): PaidBillsFragment {
            val fragment = PaidBillsFragment()
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
            inflater, R.layout.fragment_paid_bills, container, false
        )
        initializeControls()
        return binding.root
    }
    private fun initializeControls() {
        sharedViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]

        paidBillsAdapter = PaidBillsAdapter(paidArrayList, object : PaidBillsItemCallback {
            override fun paidBillsItemClick(position: Int, billsModel: Data) {
                val intent = Intent(requireActivity(), BillsPaidDetailActivity::class.java)
                intent.putExtra("bmId", billsModel.bmId)
                intent.putExtra("refNo", billsModel.refNo.toString())
                intent.putExtra("paymentMethod", billsModel.paymentMethod)
                intent.putExtra("amountPaid", billsModel.amount)
                intent.putExtra("paidStatus", billsModel.paidStatus)
                intent.putExtra("paidDate", billsModel.paidDate)
                intent.putExtra("billCategory", billsModel.billCategory)
                if (billsModel.tenantId != null) {
                    intent.putExtra("payer", billsModel.tenantId?.name)
                } else {
                    intent.putExtra("payer", "")
                }
                if (billsModel.uploadReceipt != null) {
                    if (billsModel.uploadReceipt!!.isNotEmpty()) {
                        intent.putExtra(
                            "PaymentReceipt",
                            billsModel.uploadReceipt?.get(0).toString()
                        )
                    } else {
                        intent.putExtra("PaymentReceipt", "")
                    }
                } else {
                    intent.putExtra("PaymentReceipt", "")
                }
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
        })
        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.paidRecyclerview.layoutManager = layoutManager
        binding.paidRecyclerview.adapter = paidBillsAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getPaidBillsData() {
        sharedViewModel.getAllBillsData().observe(viewLifecycleOwner) {
            it?.let {
                paidArrayList.clear()
                it.data?.let { data ->
                    for (j in data.indices) {
                        if (data[j]?.paidStatus.equals("paid", ignoreCase = true)) {
                            data[j].let { it1 -> paidArrayList.add(it1!!) }
                        }
                    }
                }
                if (paidArrayList.size > 0) {
                    binding.noData.visibility = View.GONE
                } else {
                    binding.noData.visibility = View.VISIBLE
                }
                paidBillsAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        isFragmentVisible = menuVisible
        if (menuVisible) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                getPaidBillsData()
            }
        }
    }
}