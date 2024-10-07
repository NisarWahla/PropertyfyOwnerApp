package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import com.dzinemedia.ownerpropertyfyapp.customeDialog.ProfileBottomSheetDialog

interface EditProfileBottomSheetCallback {
    fun onGalleryClicked(view: ProfileBottomSheetDialog)
    fun onCameraClicked(view: ProfileBottomSheetDialog)
    fun onView(view: ProfileBottomSheetDialog)
}