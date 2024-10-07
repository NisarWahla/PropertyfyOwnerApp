package com.dzinemedia.ownerpropertyfyapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.billsModel.BillsModel
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.ownerPropertyRentalOverview.RentalPlan
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.tenantOverviewModel.TenantOverviewModel

class ShareViewModel : ViewModel() {
    private val rentalPlanList: MutableLiveData<ArrayList<RentalPlan>?> = MutableLiveData()
    private val billsList: MutableLiveData<BillsModel?> = MutableLiveData()
    private val tenantOverviewModel: MutableLiveData<TenantOverviewModel?> = MutableLiveData()

    fun setRentalPlanList(value: ArrayList<RentalPlan>?) {
        rentalPlanList.postValue(value)
    }

    fun getRentalPlanList(): LiveData<ArrayList<RentalPlan>?> {
        return rentalPlanList
    }

    fun setAllBillsData(value: BillsModel?) {
        billsList.postValue(value)
    }

    fun getAllBillsData(): LiveData<BillsModel?> {
        return billsList
    }

    fun setOwnerTenantPropertiesOverview(value: TenantOverviewModel?) {
        tenantOverviewModel.postValue(value)
    }

    fun getOwnerTenantPropertiesOverview(): LiveData<TenantOverviewModel?> {
        return tenantOverviewModel
    }

    fun refreshObserverResult() {
        tenantOverviewModel.value?.let { property ->
            tenantOverviewModel.postValue(property)
        }
    }

}