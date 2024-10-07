package com.dzinemedia.ownerpropertyfyapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.EditProfileBottomSheetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.InternetCallback
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.PassportBottomSheetCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.InternetDialogFragment
import com.dzinemedia.ownerpropertyfyapp.customeDialog.PassportBottomSheetDialog
import com.dzinemedia.ownerpropertyfyapp.customeDialog.ProfileBottomSheetDialog
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityUpdateProfileBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson
import com.itextpdf.text.exceptions.BadPasswordException
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.permissionx.guolindev.PermissionX
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class UpdateProfile : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var viewModel: TenantViewModel
    private var token: String? = null
    private var tenantId: Int? = null
    private var jsonModel: LoginModel? = null
    private val TAG = "UpdateProfile"
    private var url: String? = ""
    private var profilePicUrl: String? = ""
    private var filePassport: File? = null
    private var isProfileOrPassport: String = "profile"
    private var fileProfile: File? = null
    private var sPhone: String = ""

    private val openDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    val newFile = uriToFile(uri)
                    val newUrl = newFile.absolutePath
                    try {
                        val pdfReader = PdfReader(newUrl)
                        val textFromPdfFilePageOne =
                            PdfTextExtractor.getTextFromPage(pdfReader, 1)
                        println(textFromPdfFilePageOne)
                        filePassport = newFile
                        url = filePassport?.absolutePath
                        binding.passportImg.setBackgroundResource(0)
                        binding.passportImg.setImageDrawable(null)
                        binding.passportImg.setBackgroundResource(R.drawable.pdf_passport_icon)
                        Log.i(TAG, ": ${filePassport!!.name}")
                    } catch (ex: BadPasswordException) {
                        filePassport = newFile
                        url = filePassport?.absolutePath
                        binding.passportImg.setBackgroundResource(0)
                        binding.passportImg.setImageDrawable(null)
                        binding.passportImg.setBackgroundResource(R.drawable.pdf_passport_icon)
                        Log.i(TAG, ": ${filePassport!!.name}")
                    } catch (ex: Exception) {
                        Utils.showToast(this, "Selected PDF file is corrupted!")
                    }
                }
            }
        }

    @SuppressLint("Recycle")
    private fun uriToFile(uri: Uri): File {
        val contentResolver = contentResolver
        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val file = File(cacheDir, contentResolver.getFileName(uri))
        fileDescriptor?.let {
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        }
        return file
    }

    private fun ContentResolver.getFileName(uri: Uri): String {
        var name = ""
        val returnCursor = this.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    var resultCallbackForGallery: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val selectedImage = data!!.data
            beginCrop(selectedImage!!)
        }
    }

    var resultCallbackForCrop: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val cropResult = CropImage.getActivityResult(data)
            Log.i(TAG, "cropResult: $cropResult")
            if (isProfileOrPassport == "profile") {
                fileProfile = cropResult.uri.path?.let { File(it) }
                profilePicUrl = fileProfile?.absolutePath
                Glide.with(this@UpdateProfile).load(cropResult.uri)
                    .placeholder(R.drawable.profile_img_icon)
                    .into(binding.profileImg)
            } else {
                filePassport = cropResult.uri.path?.let { File(it) }
                url = filePassport?.absolutePath
                Glide.with(this@UpdateProfile).load(cropResult.uri)
                    .placeholder(R.drawable.pdf_passport_icon)
                    .into(binding.passportImg)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile)
        initializeControls()
        setViewClickListener()
    }


    private fun initializeControls() {
        binding.countyCodePicker.registerPhoneNumberTextView(binding.txtPhoneValue)
        binding.countyCodePicker.registeredPhoneNumberTextView.setHintTextColor(Color.parseColor("#AFAFAF"))
        binding.countyCodePicker.dialogTextColor = Color.parseColor("#1A1A1A")
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this,
            TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        token = jsonModel?.data?.token
        tenantId = jsonModel?.data?.id?.toInt()
        binding.etNameValue.setText(jsonModel?.data?.name)
        binding.txtEmailValue.setText(jsonModel?.data?.email)
        sPhone = "${jsonModel?.data?.phone1}"
        if (sPhone.isNotEmpty() && !(sPhone.equals("null"))) {
            Log.i(TAG, "initializeControls: $sPhone")
            binding.countyCodePicker.fullNumber = sPhone
        }
        binding.txtIdValue.text = jsonModel?.data?.profileId
        if (jsonModel?.data?.uploadPassport!!.isNotEmpty()) {
            url = jsonModel?.data?.uploadPassport?.get(0)
            if (url?.contains(".pdf") == true) {
                Log.i(TAG, "PDF file: ")
            } else {
                Glide.with(binding.root.context).load(jsonModel?.data?.uploadPassport!![0])
                    .placeholder(R.drawable.img_emp).into(binding.passportImg)
            }
        }
        profilePicUrl = jsonModel?.data?.getUserImage()
        Glide.with(binding.root.context).load(jsonModel?.data?.getUserImage())
            .placeholder(R.drawable.profile_img_icon).into(binding.profileImg)

    }

    private fun beginCrop(source: Uri) {
        val intentCrop: Intent = if (isProfileOrPassport == "profile") {
            CropImage.activity(source).setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1).getIntent(this@UpdateProfile)
        } else {
            CropImage.activity(source).setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(16, 9).getIntent(this@UpdateProfile)
        }
        resultCallbackForCrop.launch(intentCrop)
    }

    private fun showProgress() {
        binding.progressApi.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressApi.visibility = View.GONE
    }

    private fun updateProfile(token: String, tenantId: Int) {
        val cc = binding.countyCodePicker.selectedCountryCodeWithPlus
        val phoneNumber = cc + Utils.getValidatedNumber(
            this,
            binding.txtPhoneValue.text.toString(),
            binding.countyCodePicker.selectedCountryCodeWithPlus
        )
        Log.i(TAG, "updateProfile: $phoneNumber")
        val email = binding.txtEmailValue.text.toString()
        val name = binding.etNameValue.text.toString()
        if (emailAndPasswordValidation(email, name, phoneNumber)) {
            viewModel.loading.observe(this) { isProgress ->
                if (isProgress) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
            viewModel.loginApiLiveData.observe(this) {
                if (it.success == true) {
                    it.message?.let { it1 -> Utils.showToast(this@UpdateProfile, it1) }
                    val loginModel = Gson().toJson(it)
                    SharedPreferencesHelper.getInstance(this).saveLoginModel(loginModel)
                    setResult(Activity.RESULT_OK)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.updateProfile(
                    token,
                    tenantId,
                    name,
                    email,
                    phoneNumber,
                    fileProfile,
                    filePassport
                )
            }
        }
    }

    private fun emailAndPasswordValidation(
        email: String,
        name: String,
        phoneNumber: String
    ): Boolean {
        if (TextUtils.isEmpty(name)) {
            binding.etNameValue.error =
                binding.root.context.getString(R.string.please_enter_your_name)
            binding.etNameValue.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.txtPhoneValue.error =
                binding.root.context.getString(R.string.please_enter_your_phone_no)
            binding.txtPhoneValue.requestFocus()
            return false
        }
        if (!(binding.countyCodePicker.isValid)) {
            binding.txtPhoneValue.error =
                binding.root.context.getString(R.string.please_enter_valid_phone_no)
            binding.txtPhoneValue.requestFocus()
            return false
        }
        return true
    }

    private fun setViewClickListener() {
        binding.copyProfileSimple.setOnClickListener(DebounceClickHandler {
            isProfileOrPassport = "profile"
            val bottomSheetFragment =
                ProfileBottomSheetDialog(object : EditProfileBottomSheetCallback {
                    override fun onGalleryClicked(view: ProfileBottomSheetDialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val intent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            resultCallbackForGallery.launch(intent)
                            view.dismiss()
                        } else {
                            if (checkPermissionBehave()) {
                                val intent = Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                                resultCallbackForGallery.launch(intent)
                                view.dismiss()
                            } else {
                                takePermission()
                            }
                        }
                    }

                    override fun onCameraClicked(view: ProfileBottomSheetDialog) {
                        openCameraIntent()
                        view.dismiss()
                    }

                    override fun onView(view: ProfileBottomSheetDialog) {
                        view.dismiss()
                        val intent = Intent(binding.root.context, PDFViewerActivity::class.java)
                        intent.putExtra("url", profilePicUrl)
                        Log.i(TAG, "setViewClickListener: $profilePicUrl")
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            binding.root.context,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                    }

                })
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        })
        binding.plusIcon.setOnClickListener(DebounceClickHandler {
            isProfileOrPassport = "passport"
            val passportBottomSheetFragment =
                PassportBottomSheetDialog(object : PassportBottomSheetCallback {
                    override fun onGalleryClicked(view: PassportBottomSheetDialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val intent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            resultCallbackForGallery.launch(intent)
                            view.dismiss()
                        } else {
                            if (checkPermissionBehave()) {
                                val intent = Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                                resultCallbackForGallery.launch(intent)
                                view.dismiss()
                            } else {
                                takePermission()
                            }
                        }
                    }

                    override fun onCameraClicked(view: PassportBottomSheetDialog) {
                        openCameraIntent()
                        view.dismiss()
                    }

                    override fun onSelectPdfDocument(view: PassportBottomSheetDialog) {
                        selectFiles("application/pdf")
                        view.dismiss()
                    }

                    override fun onViewDocument(view: PassportBottomSheetDialog) {
                        view.dismiss()
                        val intent = Intent(binding.root.context, PDFViewerActivity::class.java)
                        intent.putExtra("url", url)
                        Log.i(TAG, "setViewClickListener: $url")
                        val options = ActivityOptionsCompat.makeCustomAnimation(
                            binding.root.context,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                    }

                })
            passportBottomSheetFragment.show(
                supportFragmentManager,
                passportBottomSheetFragment.tag
            )
        })
        binding.btnUpdateProfile.setOnClickListener(DebounceClickHandler {
            hideKeyboard(it)
            token?.let { t ->
                tenantId?.let { id ->
                    if (Utils.isNetworkAvailable(this)) {
                        updateProfile(t, id)
                    } else {
                        showInternetDialog()
                    }
                }
            }
        })
        binding.back.setOnClickListener(DebounceClickHandler {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
    }

    private fun selectFiles(type: String) {
        val uri = Uri.parse(Environment.getRootDirectory().toString() + "/")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setDataAndType(uri, type)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            openDocumentLauncher.launch(
                Intent.createChooser(
                    intent,
                    getString(R.string.select_file)
                )
            )
        } catch (ex: ActivityNotFoundException) {
            // Handle the case where no file manager is available
            // Replace StringUtils.getInstance().showSnackbar with your own snackbar or toast
            Log.e("MainActivity", "Please install a file manager")
        }
    }

    private fun showInternetDialog() {
        val internetDialog =
            InternetDialogFragment(object : InternetCallback {
                override fun tryAgainClick(internetDialog: InternetDialogFragment) {
                    if (Utils.isNetworkAvailable(this@UpdateProfile)) {
                        internetDialog.dismiss()
                    } else {
                        showInternetDialog()
                    }
                }
            })
        internetDialog.show(supportFragmentManager, internetDialog.tag)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    var imageFilePath: String? = null
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.ENGLISH
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        imageFilePath = image.absolutePath
        return image
    }


    var resultCallbackForCamera: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.resultCode == Activity.RESULT_OK) {
            val matrix = Matrix()
            try {
                val exif = ExifInterface(imageFilePath!!)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val selectedImage = Uri.fromFile(File(imageFilePath))
            beginCrop(selectedImage)
        }
    }

    private fun openCameraIntent() {
        val pictureIntent = Intent(
            MediaStore.ACTION_IMAGE_CAPTURE
        )
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: Exception) {
            }
            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    binding.root.context.applicationContext,
                    "$packageName.fileprovider",
                    photoFile
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                resultCallbackForCamera.launch(pictureIntent)
            }
        }
    }

    private fun takePermission() {
        PermissionX.init(this).permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                getString(R.string.core_fundamental),
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                getString(R.string.need_to_allow_necessary_permission),
                getString(R.string.ok),
                getString(R.string.cancel)
            )
        }.request { allGranted, grantedList, deniedList ->
            if (allGranted) {

            } else {
                Log.i(TAG, "takePermission: Permission denied")
            }
        }
    }

    private fun checkPermissionBehave(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            binding.root.context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return result1 == PackageManager.PERMISSION_GRANTED
    }
}