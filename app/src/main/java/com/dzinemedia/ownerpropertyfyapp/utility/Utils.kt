package com.dzinemedia.ownerpropertyfyapp.utility

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment.Companion.TAG
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun makePhoneCall(context: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        context.startActivity(intent)
    }
    fun getValidatedNumber(context: Context, number: String, region: String): String {
        var finalInputNumber = number.replace(" ","")
        finalInputNumber = finalInputNumber.replace(")","")
        finalInputNumber = finalInputNumber.replace("(","")
        finalInputNumber = finalInputNumber.replace("-","")
        finalInputNumber = finalInputNumber.replace(",","")
        val phoneUtil = PhoneNumberUtil.createInstance(context)
        var phoneNumber: Phonenumber.PhoneNumber? = null

        try {
            phoneNumber = phoneUtil.parse(finalInputNumber, region)
        } catch (e: Exception) {
            Log.e("Constants.tag", "error during parsing a number", e)
        }

        return if (phoneNumber != null && phoneUtil.isValidNumber(phoneNumber)) {
            phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } else {
            if (finalInputNumber.startsWith("+")) {
                finalInputNumber
            } else if (finalInputNumber.startsWith("0")) {
                finalInputNumber.substring(1)
            } else {
                finalInputNumber
            }
        }
    }

    fun togglePasswordVisibility(editText: EditText, imageview: ImageView) {
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            // If the password is currently hidden, show it
            editText.transformationMethod = null
            imageview.setBackgroundResource(0)
            imageview.setImageResource(R.drawable.ic_opened_eye)
        } else {
            // If the password is currently shown, hide it
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            imageview.setBackgroundResource(0)
            imageview.setImageResource(R.drawable.ic_closed_eye)
        }
        // Move the cursor to the end of the text
        editText.setSelection(editText.text.length)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    fun changeDateFormat(originalDate: String): String? {
        Log.i(TAG, "changeDateFormat: $originalDate")
        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val newFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = originalFormat.parse(originalDate)
        return date?.let { newFormat.format(it) }
    }
    fun getCurrentTimeStatus(context: Context): String {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hourOfDay) {
            in 6..11 -> context.getString(R.string.good_moring) // 6:00 AM to 11:59 AM
            in 12..16 -> context.getString(R.string.good_noon)   // 12:00 PM to 4:59 PM
            in 17..20 -> context.getString(R.string.good_after_noon) // 5:00 PM to 8:59 PM
            else -> context.getString(R.string.good_night)       // 9:00 PM to 5:59 AM
        }
    }
    fun preventDoubleTap(v: View?) {
        if (v != null) {
            v.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                v.isEnabled = true
            }, 300)
        }
    }
    fun convertDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        return try {
            val date = inputFormat.parse(inputDate)
            if (date != null) {
                outputFormat.format(date)
            } else {
                "Invalid Date"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Invalid Date"
        }
    }
    fun getDateStatus(givenDate: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val dateToCompare: Date =
            sdf.parse(givenDate) ?: currentDate // Use current date if parsing fails

        return when {
            currentDate.after(dateToCompare) -> "Overdue"
            currentDate.before(dateToCompare) -> "Pending"
            else -> "Pending"
        }
    }
    fun calculateDifferenceBetweenDates(
        startDateString: String?,
        endDateString: String?,
        dateFormat: String
    ): Triple<Int, Int, Int> {
        // Define the date format
        val formatter = DateTimeFormatter.ofPattern(dateFormat)

        // Parse the given date strings
        val startDate = LocalDate.parse(startDateString, formatter)
        val endDate = LocalDate.parse(endDateString, formatter)

        // Calculate the period between the start date and the end date
        val period = Period.between(startDate, endDate)

        // Extract years, months, and days from the period
        val years = period.years
        val months = period.months
        val days = period.days

        return Triple(years, months, days)
    }


    fun getDateDifference(startDate: String?, endDate: String?, dateFormat: String): String {
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        val startDateObj = sdf.parse(startDate)
        val endDateObj = sdf.parse(endDate)

        // Calculate the difference in milliseconds
        val differenceInMillis = endDateObj.time - startDateObj.time

        // Convert milliseconds to days
        val differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24)
        //QA fahad want exact date that's

        return getMonthOrYearOrDays(differenceInDays.toInt())
    }
    private fun getMonthOrYearOrDays(days: Int): String {
        return when {
            days > 365 -> {
                val years = days / 365
                "$years years"
            }
            days > 30 -> {
                val months = days / 30
                "$months months"
            }
            else -> {
                "$days days"
            }
        }
    }
   /* fun isPdfCorrupted(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            val pdfReader = PdfReader(file)
            val pdfDocument = PdfDocument(pdfReader)
            pdfDocument.close()
            false
        } catch (e: IOException) {
            true
        } catch (e: com.itextpdf.kernel.exceptions.PdfException) {
            true
        }
    }*/
}