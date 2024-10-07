package com.dzinemedia.ownerpropertyfyapp.customeDialog

import android.app.Dialog
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.PasswordCallback
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

class PasswordDialogFragment() : DialogFragment() {
    private var listener: PasswordCallback? = null

    constructor(callback: PasswordCallback) : this() {
        listener = callback
    }

    companion object {
        const val TAG = "ExceedDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.set_pdf_pswrd, null)

        // Access views in the custom layout
        val etText = view.findViewById<EditText>(R.id.et_enter_pswrd)
        val btnShowPassword = view.findViewById<ImageView>(R.id.iv_enter_pswrd_show)
        val btnHidePassword = view.findViewById<ImageView>(R.id.iv_enter_pswrd_hide)
        val btnCancel = view.findViewById<TextView>(R.id.tv_cancel)
        val btnDone = view.findViewById<TextView>(R.id.tv_done)
        btnCancel.setOnClickListener(DebounceClickHandler {
            dismiss()
        })
        btnDone.setOnClickListener(DebounceClickHandler {
            listener?.enterPassword(this,etText)
        })
        btnShowPassword.setOnClickListener(DebounceClickHandler{
            btnShowPassword.visibility = View.GONE
            btnHidePassword?.visibility = View.VISIBLE
            etText?.transformationMethod = HideReturnsTransformationMethod()
            etText?.setSelection(etText.text.length)
        })
        btnHidePassword.setOnClickListener(DebounceClickHandler{
            btnShowPassword?.visibility = View.VISIBLE
            btnHidePassword.visibility = View.GONE
            etText?.transformationMethod = PasswordTransformationMethod()
            etText?.setSelection(etText.text.length)
        })

        builder.setView(view)
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.logout_dialog_round)
    }
}