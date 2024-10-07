package com.dzinemedia.ownerpropertyfyapp.customeDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.PassportBottomSheetCallback
import com.dzinemedia.ownerpropertyfyapp.databinding.PassportBottomSheetBinding
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PassportBottomSheetDialog() : BottomSheetDialogFragment() {
    private lateinit var binding: PassportBottomSheetBinding
    private var listener: PassportBottomSheetCallback? = null

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialogTheme
    }

    constructor(callback: PassportBottomSheetCallback) : this() {
        listener = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.passport_bottom_sheet, container, false)
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
        binding.selectPDF.setOnClickListener(DebounceClickHandler {
            listener?.onSelectPdfDocument(this)
        })
        binding.cancelButton.setOnClickListener(DebounceClickHandler {
            listener?.onViewDocument(this)
        })
    }
}