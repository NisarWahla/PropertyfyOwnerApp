package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.customeDialog.PassportBottomSheetDialog

interface PassportBottomSheetCallback {
    fun onGalleryClicked(view: PassportBottomSheetDialog)
    fun onCameraClicked(view: PassportBottomSheetDialog)
    fun onSelectPdfDocument(view: PassportBottomSheetDialog)
    fun onViewDocument(view: PassportBottomSheetDialog)
}