package com.dzinemedia.ownerpropertyfyapp.fragments.mainFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.*
import com.dzinemedia.ownerpropertyfyapp.adaptersCallback.LogoutCallback
import com.dzinemedia.ownerpropertyfyapp.apiInterface.RetrofitClient
import com.dzinemedia.ownerpropertyfyapp.customeDialog.LogoutDialogFragment
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentProfileBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.repository.TenantRepository
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.ownerpropertyfyapp.utility.Utils
import com.dzinemedia.ownerpropertyfyapp.viewModel.TenantViewModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: TenantViewModel
    private var url: String? = ""
    private var jsonModel: LoginModel? = null
    private val TAG = "ProfileFragment"

    private var updateActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result
                result?.let {
                    val loginModel =
                        SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
                    jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
                    binding.txtNameValue.text = jsonModel?.data?.name
                    binding.txtEmailValue.text = jsonModel?.data?.email
                    binding.txtPhoneValue.text = jsonModel?.data?.phone1.toString()
                    binding.txtIdValue.text = jsonModel?.data?.uploadId.toString()
                    binding.txtIdValue.text = jsonModel?.data?.profileId
                    if (jsonModel?.data?.uploadPassport!!.isNotEmpty()) {
                        url = jsonModel?.data?.uploadPassport?.get(0)
                        if (url?.contains(".pdf") == true) {
                            Log.i(TAG, "PDF file: ")
                            binding.passportImg.setBackgroundResource(0)
                            binding.passportImg.setImageDrawable(null)
                            binding.passportImg.setBackgroundResource(R.drawable.pdf_passport_icon)
                        } else {
                            binding.passportImg.setBackgroundResource(0)
                            binding.passportImg.setImageDrawable(null)
                            Glide.with(binding.root.context)
                                .load(jsonModel?.data?.uploadPassport?.get(0))
                                .placeholder(R.drawable.img_emp)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        return false
                                    }

                                }).into(binding.passportImg)
                        }
                    }
                    binding.imageProgress.visibility = View.VISIBLE
                    Glide.with(binding.root.context).load(jsonModel?.data?.getUserImage())
                        .placeholder(R.drawable.profile_img_icon)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.imageProgress.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.imageProgress.visibility = View.GONE
                                return false
                            }

                        }).into(binding.profileImg)
                    jsonModel?.data?.name?.let {
                        jsonModel?.data?.email?.let { it1 ->
                            jsonModel?.data?.getUserImage()?.let { it2 ->
                                (activity as MainActivity).updateDrawerCredentials(
                                    it, it1, it2
                                )
                            }
                        }
                    }
                }
            }
        }

    private var updatePasswordResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result
                result?.let {

                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        initializedControls()
        setViewClickListener()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initializedControls() {
        val apiInterface = RetrofitClient.create()
        val repository = TenantRepository(apiInterface)
        viewModel = ViewModelProvider(
            this, TenantViewModel.TenantViewModalFactory(repository)
        )[TenantViewModel::class.java]
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)
        binding.txtNameValue.text = jsonModel?.data?.name
        binding.txtEmailValue.text = jsonModel?.data?.email
        binding.txtPhoneValue.text = jsonModel?.data?.phone1.toString()
        binding.txtIdValue.text = jsonModel?.data?.profileId
        if (jsonModel?.data?.uploadPassport!!.isNotEmpty()) {
            url = jsonModel?.data?.uploadPassport?.get(0)
            if (url?.contains(".pdf") == true) {
                Log.i(TAG, "initializedControls: PDF")
            } else {
                Glide.with(binding.root.context).load(url).placeholder(R.drawable.img_emp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).into(binding.passportImg)
            }
        }
        binding.imageProgress.visibility = View.VISIBLE
        Glide.with(binding.root.context).load(jsonModel?.data?.getUserImage())
            .placeholder(R.drawable.profile_img_icon).diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.imageProgress.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.imageProgress.visibility = View.GONE
                    return false
                }

            })
            .into(binding.profileImg)
    }

    private fun setViewClickListener() {
        binding.changePasswordLayout.setOnClickListener(DebounceClickHandler {
            val intent = Intent(requireActivity(), OTPActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.editProfile.setOnClickListener(DebounceClickHandler {
            val intent = Intent(binding.root.context, UpdateProfile::class.java)
            updateActivityResultLauncher.launch(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.changePasswordLayout.setOnClickListener(DebounceClickHandler {
            val intent = Intent(binding.root.context, ChangePasswordActivity::class.java)
            updatePasswordResultLauncher.launch(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        })
        binding.logoutLayout.setOnClickListener(DebounceClickHandler {
            val logoutDialog = LogoutDialogFragment(object : LogoutCallback {
                override fun logout(view: LogoutDialogFragment) {
                    view.dismiss()
                    logout()
                }
            })
            logoutDialog.show(childFragmentManager, logoutDialog.tag)
        })
        binding.passportImg.setOnClickListener(DebounceClickHandler {
            val intent = Intent(binding.root.context, PDFViewerActivity::class.java)
            intent.putExtra("url", url)
            Log.i(TAG, "setViewClickListener: $url")
            val options = ActivityOptionsCompat.makeCustomAnimation(
                binding.root.context, R.anim.slide_in_right, R.anim.slide_out_left
            )
            startActivity(intent, options.toBundle())
        })
        binding.menuIcon.setOnClickListener(DebounceClickHandler {
            (activity as MainActivity).openDrawer()
        })
    }

    private fun logout() {
        viewModel.loading.observe(viewLifecycleOwner) { isProgress ->
            if (isProgress) {
                binding.progressApi.visibility = View.VISIBLE
            } else {
                binding.progressApi.visibility = View.GONE
            }
        }
        viewModel.logoutLiveData.observe(viewLifecycleOwner) {
            if (it.success == true) {
                val txtMessage = it.message
                Utils.showToast(binding.root.context, txtMessage.toString())
                SharedPreferencesHelper.getInstance(binding.root.context).clearAllPref()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                Utils.showToast(binding.root.context, it.message.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            jsonModel?.data?.token?.let { t ->
                jsonModel?.data?.id?.let { tenantId ->
                    viewModel.logout(t, tenantId.toInt())
                }
            }
        }
    }
}