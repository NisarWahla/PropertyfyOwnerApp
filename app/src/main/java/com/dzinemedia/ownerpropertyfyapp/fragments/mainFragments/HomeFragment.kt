package com.dzinemedia.ownerpropertyfyapp.fragments.mainFragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.BillsActivity
import com.dzinemedia.ownerpropertyfyapp.activities.MainActivity
import com.dzinemedia.ownerpropertyfyapp.activities.MyPropertyActivity
import com.dzinemedia.ownerpropertyfyapp.activities.MyTenantActivity
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.RetrofitMessageCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentHomeBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var viewModel: TenantViewModel
    private val TAG = "HomeFragment"

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var jsonModel: LoginModel? = null
    private lateinit var binding: FragmentHomeBinding

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
            inflater, R.layout.fragment_home, container, false
        )
        initializeControls()
        setViewClickListeners()
        return binding.root
    }

    private fun setViewClickListeners() {
        binding.addRelLayout.propertiesRelative.setOnClickListener(DebounceClickHandler {
            val intent = Intent(requireActivity(), MyPropertyActivity::class.java)
            startActivity(intent)
        })
        binding.addRelLayout.myTenantRelative.setOnClickListener(DebounceClickHandler {
            val intent = Intent(requireActivity(), MyTenantActivity::class.java)
            startActivity(intent)
        })
        binding.addRelLayout.billsRelative.setOnClickListener(DebounceClickHandler {
            val intent = Intent(requireActivity(), BillsActivity::class.java)
            startActivity(intent)
        })
        binding.menuIcon.setOnClickListener(DebounceClickHandler {
            (activity as MainActivity).openDrawer()
        })
    }

    private fun initializeControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            requireActivity(),
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        jsonModel?.data?.token?.let { token ->
            jsonModel?.data?.id?.let { ownerId ->
                getDashboardData(token, ownerId.toInt())
            }
        }
    }

    private fun showProgress() {
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.originalLayout.visibility = View.GONE
    }

    private fun hideProgress() {
        binding.shimmerLayout.visibility = View.GONE
        binding.originalLayout.visibility = View.VISIBLE
    }

    private fun getDashboardData(token: String, ownerId: Int) {
        viewModel.loading.observe(viewLifecycleOwner) { isProgress ->
            if (isProgress) {
                showProgress()
            } else {
                hideProgress()
            }
        }
        viewModel.dashboardApiLiveData.observe(viewLifecycleOwner) {
            if (it.success == true) {
                binding.txtPropertyValue.text = it.data?.properties.toString()
                binding.txtRentedValue.text = it.data?.rented.toString()
                binding.txtVacantValue.text = it.data?.vacant.toString()
                binding.txtSalesValue.text = it.data?.forSale.toString()
                binding.aedValue.text = it.data?.totalBalance.toString()
                binding.aedTwoValue.text = it.data?.availableBalance.toString()
                binding.txtGoodMorning.text = Utils.getCurrentTimeStatus(binding.root.context)
                binding.txtAllen.text = jsonModel?.data?.name.toString()
                it.data?.let { data ->
                    setPieChart(data.properties.toInt(), data.rented.toInt(), data.vacant.toInt(), data.forSale.toInt())
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getDashboardData(token, ownerId, object : RetrofitMessageCallback {
                override fun retrofitErrorMessage(message: String) {
                    Utils.showToast(binding.root.context, message)
                }
            })
        }

    }

    private fun setPieChart(properties: Int?, rented: Int?, vacant: Int?, forSale: Int?) {
        val entries = ArrayList<PieEntry>()
        val fRented: Float? = rented?.toFloat()
        val fVacant: Float? = vacant?.toFloat()
        val fForSale: Float? = forSale?.toFloat()
        entries.add(PieEntry(fRented!!, "Rented", 0f))
        entries.add(PieEntry(fVacant!!, "Vacant", 1f))
        entries.add(PieEntry(fForSale!!, "For Sale", 2f))

        val hexColors = listOf(
            "#1B84FF",
            "#F82859",
            "#00C6AE"
        )

        // Convert hex colors to RGB
        val colors = hexColors.map { Color.parseColor(it) }

        val dataSet = PieDataSet(entries.toMutableList(), "")
        dataSet.colors = colors
        dataSet.setDrawIcons(false)
        //set slice thickness
        dataSet.selectionShift = 3f
        dataSet.setDrawValues(false)
        //dataSet.form = Legend.LegendForm.NONE

        val x = 80f
        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.centerText = "$properties \nunits"
        binding.pieChart.setCenterTextSize(10f)
        binding.pieChart.holeRadius = x
        binding.pieChart.centerTextRadiusPercent = 0f
        binding.pieChart.setHoleColor(android.R.color.transparent)
        binding.pieChart.setDrawEntryLabels(false)
        val legend: Legend = binding.pieChart.legend
        legend.isEnabled = false
        legend.form = Legend.LegendForm.NONE

        binding.pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    // Handle the selected slice, e.g., display a message or perform an action
                    var label = ""
                    if (e is PieEntry) {
                        label = e.label
                    }
                    val value = e.y
                    val message = "$label: $value"
                    Utils.showToast(requireActivity(), message)
                }
            }

            override fun onNothingSelected() {
                // Handle when nothing is selected, if needed
            }
        })
        binding.pieChart.invalidate()
    }
}