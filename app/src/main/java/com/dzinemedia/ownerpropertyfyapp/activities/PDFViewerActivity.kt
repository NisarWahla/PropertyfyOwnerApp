package com.dzinemedia.ownerpropertyfyapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.DownloadCompleted
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.PasswordCallback
import com.dzinemedia.ownerpropertyfyapp.customeDialog.PasswordDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityPdfviewerBinding
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.itextpdf.text.exceptions.BadPasswordException
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class PDFViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfviewerBinding
    private val TAG = "PDFViewerActivity"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdfviewer)
        if (intent.hasExtra("url")) {
            var url = intent.getStringExtra("url")
            if (url?.endsWith(".pdf", true) == true) {
                try {
                    binding.pdfView.visibility = View.VISIBLE
                    binding.paymentImg.visibility = View.GONE
                    binding.progressBarApi.visibility = View.VISIBLE

                    if (url.contains("http://") || url.contains("https://")) {
                        CoroutineScope(Dispatchers.Main).launch {
                            downloadPdfFile(url!!, object : DownloadCompleted {
                                override fun downloadCompleted(file: File) {
                                    try {
                                        url = file.path
                                        Log.i(TAG, "onCreate: $url")
                                        val pdfReader = PdfReader(file.path)
                                        val textFromPdfFilePageOne =
                                            PdfTextExtractor.getTextFromPage(pdfReader, 1)
                                        println(textFromPdfFilePageOne)
                                        binding.pdfView.fromFile(file).onLoad {
                                            binding.progressBarApi.visibility = View.GONE
                                        }.load()
                                    } catch (ex: BadPasswordException) {
                                        badPasswordException(url)
                                    } catch (ex: Exception) {
                                        Log.i(TAG, "downloadCompleted: ${ex.message}")
                                    }
                                }

                            })
                        }
                    } else {
                        val pdfReader = PdfReader(url)
                        val textFromPdfFilePageOne =
                            PdfTextExtractor.getTextFromPage(pdfReader, 1)
                        println(textFromPdfFilePageOne)
                        binding.pdfView.fromFile(url?.let { File(it) }).onLoad {
                            binding.progressBarApi.visibility = View.GONE
                        }.load()
                    }
                } catch (ex: BadPasswordException) {
                    Log.i(TAG, "onCreate: ${ex.message}")
                    badPasswordException(url)
                } catch (ex: Exception) {
                    Log.i(TAG, "onCreate: ${ex.message}")
                }
            } else {
                binding.pdfView.visibility = View.GONE
                binding.paymentImg.visibility = View.VISIBLE
                Glide.with(binding.root.context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.paymentImg)
            }
        }
        setViewClickListener()
    }

    private fun badPasswordException(url: String?) {
        val passwordDialog = PasswordDialogFragment(object : PasswordCallback {
            override fun enterPassword(
                view: PasswordDialogFragment,
                editText: EditText
            ) {
                if (editText.text.toString() == "") {
                    Utils.showToast(
                        binding.root.context,
                        binding.root.context.getString(R.string.enter_password)
                    )
                } else {
                    binding.pdfView.fromFile(url?.let { File(it) })
                        .password(editText.text.toString()).onError { t ->
                            // Handle the error, e.g., incorrect password
                            if (t is com.shockwave.pdfium.PdfPasswordException) {
                                // Incorrect password
                                Utils.showToast(
                                    binding.root.context,
                                    binding.root.context.getString(R.string.incorrect_password)
                                )
                            }
                        }.onLoad {
                            view.dismiss()
                            binding.progressBarApi.visibility = View.GONE
                        }.load()
                }
            }

        })
        passwordDialog.show(supportFragmentManager, passwordDialog.tag)
    }

    private suspend fun downloadPdfFile(url: String, listener: DownloadCompleted) {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw IOException("Failed to download file: $response")
            }

            val inputStream: InputStream? = response.body?.byteStream()
            val outputFile = File(filesDir, "/temp")
            if (!outputFile.exists()) {
                outputFile.mkdirs()
            }
            val newOutputFile = File(outputFile, "/sample.pdf")
            val outputStream = FileOutputStream(newOutputFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input?.copyTo(output)
                }
            }
            listener.downloadCompleted(newOutputFile)
        }
    }

    private fun setViewClickListener() {
        binding.ivBack.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
    }
}