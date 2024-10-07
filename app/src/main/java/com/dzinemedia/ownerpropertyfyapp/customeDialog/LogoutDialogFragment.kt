package com.dzinemedia.ownerpropertyfyapp.customeDialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.LogoutCallback
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.android.material.textview.MaterialTextView

class LogoutDialogFragment() : DialogFragment() {
    private var listener: LogoutCallback? = null

    constructor(callback: LogoutCallback) : this() {
        listener = callback
    }

    companion object {
        const val TAG = "ExceedDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.logout_dialog, null)

        // Access views in the custom layout
        val btnCancel = view.findViewById<MaterialTextView>(R.id.btn_cancel)
        val btnConfirm = view.findViewById<MaterialTextView>(R.id.btn_confirm)
        btnCancel.setOnClickListener(DebounceClickHandler {
            dismiss()
        })
        btnConfirm.setOnClickListener(DebounceClickHandler {
            listener?.logout(this)
        })
        builder.setView(view)
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.logout_dialog_round)
    }
}