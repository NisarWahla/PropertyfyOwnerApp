package com.dzinemedia.ownerpropertyfyapp.customeDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.EditProfileBottomSheetCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.EditBottomSheetBinding
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileBottomSheetDialog() : BottomSheetDialogFragment() {
    private lateinit var binding: EditBottomSheetBinding
    private var listener: EditProfileBottomSheetCallback? = null

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialogTheme
    }

    constructor(callback: EditProfileBottomSheetCallback) : this() {
        listener = callback
    }

    /*companion object {
        fun newInstance(callback: EditProfileBottomSheetCallback): ProfileBottomSheetDialog {
            val fragment = ProfileBottomSheetDialog(callback)
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_bottom_sheet, container, false)
        initializedControls()
        return binding.root
    }

    private fun initializedControls() {
        binding.galleryButton.setOnClickListener(DebounceClickHandler {
            listener?.onGalleryClicked(this)
        })
        binding.cameraButton.setOnClickListener(DebounceClickHandler {
            listener?.onCameraClicked(this)
        })
        binding.viewButton.setOnClickListener(DebounceClickHandler{
            listener?.onView(this)
        })
    }
}