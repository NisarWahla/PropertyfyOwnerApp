package com.dzinemedia.ownerpropertyfyapp.customeDialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.android.material.textview.MaterialTextView

class InternetDialogFragment() : DialogFragment() {
    private var listener: InternetCallback? = null

    constructor(callback: InternetCallback) : this() {
        listener = callback
    }

    companion object {
        const val TAG = "ExceedDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.internet_dialog, null)

        // Access views in the custom layout
        val btnTryAgain = view.findViewById<MaterialTextView>(R.id.btnTryAgain)
        btnTryAgain.setOnClickListener(DebounceClickHandler {
            listener?.tryAgainClick(this)
        })
        builder.setView(view)
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.logout_dialog_round)
    }
}