package com.dzinemedia.ownerpropertyfyapp.adaptersCallback

import android.widget.EditText
import com.dzinemedia.ownerpropertyfyapp.customeDialog.PasswordDialogFragment

interface PasswordCallback {
    fun enterPassword(view: PasswordDialogFragment, editText: EditText)
}