package com.dzinemedia.ownerpropertyfyapp.activities

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.adapters.MainPagerAdapter
import com.dzinemedia.ownerpropertyfyapp.databinding.ActivityMainBinding
import com.dzinemedia.ownerpropertyfyapp.models.responseModels.loginResponseModel.LoginModel
import com.dzinemedia.ownerpropertyfyapp.utility.SharedPreferencesHelper
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    val mainDelay: Long = 300
    private var jsonModel: LoginModel? = null
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageChanged(position)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val viewPagerAdapter = MainPagerAdapter(this)
        binding.viewPager2.adapter = viewPagerAdapter
        initializeControls()
        setViewClickListener()
        binding.viewPager2.setCurrentItem(1, false)
    }

    private fun initializeControls() {
        //selectHomeTab()
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )

        // Set the ActionBarDrawerToggle as the DrawerListener
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.drawerAddedLayout.drawerHeader.setOnClickListener(DebounceClickHandler {
            closeDrawer()
        })
        val loginModel = SharedPreferencesHelper.getInstance(binding.root.context).getLoginModel()
        Log.i(TAG, "initializeControls: $loginModel")
        jsonModel = Gson().fromJson(loginModel, LoginModel::class.java)

        binding.drawerAddedLayout.profileName.text = jsonModel?.data?.name
        binding.drawerAddedLayout.profileEmail.text = jsonModel?.data?.email
        if (jsonModel?.data?.image != null) {
            if (jsonModel?.data?.image!!.isNotEmpty()) {
                Glide.with(this).load(jsonModel?.data?.image!![0].toString())
                    .placeholder(R.drawable.profile_img_icon)
                    .into(binding.drawerAddedLayout.profileImg)
            }
        }

    }

    fun updateDrawerCredentials(name: String, email: String, image: String) {
        binding.drawerAddedLayout.profileName.text = name
        binding.drawerAddedLayout.profileEmail.text = email
        Glide.with(this).load(image)
            .placeholder(R.drawable.profile_img_icon)
            .into(binding.drawerAddedLayout.profileImg)
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun setViewClickListener() {
        binding.viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
        binding.relativeOne.setOnClickListener(DebounceClickHandler {
            loadProfilePage()
        })
        binding.relativeTwo.setOnClickListener(DebounceClickHandler {
            selectHomeTab()
        })
        binding.relativeThree.setOnClickListener(DebounceClickHandler {
            loadNotificationPage()
        })
        binding.drawerAddedLayout.profileLayout.setOnClickListener(DebounceClickHandler {
            closeDrawer()
            binding.relativeOne.performClick()
        })
        binding.drawerAddedLayout.notiLayout.setOnClickListener(DebounceClickHandler {
            closeDrawer()
            binding.relativeThree.performClick()
        })
    }

    private fun onPageChanged(position: Int) {
        when (position) {
            0 -> {
                loadProfilePage()
            }
            1 -> {
                selectHomeTab()
            }
            2 -> {
                loadNotificationPage()
            }
        }
    }

    private fun loadProfilePage() {
        binding.moveHomeLayout.animate()
            .x(getViewCoordinates(binding.viewFour, binding.moveHomeLayout).first)
            .setDuration(300).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    unSelectedAllTabs()
                    binding.moveCopySimpleHomeText.text =
                        binding.root.context.getString(R.string.profile)
                    binding.moveCopyFab.setImageResource(R.drawable.profile_small_icon)
                    binding.copyFabLayout.visibility = View.GONE
                    binding.copyFabNotification.visibility = View.GONE
                    binding.notificationCountAbove.visibility = View.GONE


                    binding.copySimpleProfileIcon.animate().alpha(0f).setDuration(mainDelay / 2)
                        .start()
                    binding.copySimpleHomeIcon.animate().alpha(1f).setDuration(mainDelay / 2)
                        .start()
                    binding.copySimpleNotificationIcon.animate().alpha(1f)
                        .setDuration(mainDelay / 2).start()
                    binding.copySimpleNotificationLayout.animate().alpha(1f)
                        .setDuration(mainDelay / 2).start()

                    binding.moveHomeLayout.alpha = 1f
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.moveHomeLayout.alpha = 0f
                    animationEndProfile()
                    binding.viewPager2.currentItem = 0
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            }).start()
    }

    private fun loadNotificationPage() {
        binding.moveHomeLayout.animate()
            .x(getViewCoordinates(binding.viewSix, binding.moveHomeLayout).first)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    unSelectedAllTabs()
                    binding.moveCopySimpleHomeText.text =
                        binding.root.context.getString(R.string.notification)
                    binding.moveCopyFab.setImageResource(R.drawable.notification_icon)

                    binding.copySimpleProfileIcon.animate().alpha(1f).setDuration(mainDelay / 2)
                        .start()
                    binding.copySimpleHomeIcon.animate().alpha(1f).setDuration(mainDelay / 2)
                        .start()
                    binding.copySimpleNotificationIcon.animate().alpha(0f)
                        .setDuration(mainDelay / 2).start()
                    binding.copySimpleNotificationLayout.animate().alpha(0f)
                        .setDuration(mainDelay / 2).start()

                    binding.moveHomeLayout.alpha = 1f
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.moveHomeLayout.alpha = 0f
                    animationEndNotification()
                    binding.viewPager2.currentItem = 2
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            }).setDuration(300).start()
    }
    private fun selectHomeTab() {
        binding.moveHomeLayout.animate()
            .x(getViewCoordinates(binding.viewFive, binding.moveHomeLayout).first)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    unSelectedAllTabs()
                    binding.moveCopyFab.setImageResource(R.drawable.home_small_icon)
                    binding.moveCopySimpleHomeText.text =
                        binding.root.context.getString(R.string.home)
                    binding.copyFabLayout.visibility = View.GONE
                    binding.copyFabNotification.visibility = View.GONE
                    binding.notificationCountAbove.visibility = View.GONE

                    binding.copySimpleHomeIcon.animate().alpha(0f).setDuration(mainDelay / 2)
                        .start()
                    binding.copySimpleNotificationIcon.animate().alpha(1f)
                        .setDuration(mainDelay / 2).start()
                    binding.copySimpleNotificationLayout.animate().alpha(1f)
                        .setDuration(mainDelay / 2).start()
                    binding.copySimpleProfileIcon.animate().alpha(1f).setDuration(mainDelay / 2)
                        .start()
                    binding.moveHomeLayout.alpha = 1f
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.moveHomeLayout.alpha = 0f
                    animationEndHome()
                    binding.viewPager2.currentItem = 1
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            }).setDuration(300).start()
    }

    private fun animationEndNotification() {
        //for notification
        binding.copySimpleNotificationText.visibility = View.VISIBLE
        binding.copyFabNotification.visibility = View.VISIBLE
        binding.copyFabLayout.visibility = View.VISIBLE
        binding.copyFabNotification.visibility = View.VISIBLE
        binding.notificationCountAbove.visibility = View.VISIBLE
        binding.copyNotificationCurve.visibility = View.VISIBLE
        binding.copyNotificationSimple.visibility = View.GONE
        //for profile
        setProfileTab()
        //for home
        setHomeTab()
    }

    private fun animationEndHome() {
        //for home
        binding.copySimpleHomeText.visibility = View.VISIBLE
        binding.copyFab.visibility = View.VISIBLE
        binding.copyHomeCurve.visibility = View.VISIBLE
        binding.copyHomeSimple.visibility = View.GONE
        //for profile
        setProfileTab()
        //for notification
        setNotificationTab()
    }

    fun animationEndProfile() {
        //for profile
        binding.copyFabProfile.visibility = View.VISIBLE
        binding.copyProfileCurve.visibility = View.VISIBLE
        binding.copyProfileSimple.visibility = View.GONE
        binding.copySimpleProfileText.visibility = View.VISIBLE

        //for home
        setHomeTab()
        //for notification
        setNotificationTab()
    }

    private fun getViewCoordinates(view: View, widthOfView: View): Pair<Float, Float> {
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        val x = location[0] - widthOfView.width / 2f
        val y = location[1] + widthOfView.height / 2f

        return Pair(x, y)
    }

    private fun unSelectedAllTabs() {
        //for profile
        setProfileTab()
        //for home
        setHomeTab()
        //for notification
        setNotificationTab()
    }

    private fun setProfileTab() {
        //for profile
        binding.copySimpleProfileIcon.visibility = View.VISIBLE
        binding.copySimpleProfileText.visibility = View.GONE
        binding.copyFabProfile.visibility = View.GONE
        binding.copyProfileCurve.visibility = View.GONE
        binding.copyProfileSimple.visibility = View.VISIBLE
    }

    private fun setHomeTab() {
        //for home
        binding.copySimpleHomeIcon.visibility = View.VISIBLE
        binding.copySimpleHomeText.visibility = View.GONE
        binding.copyFab.visibility = View.GONE
        binding.copyHomeCurve.visibility = View.GONE
        binding.copyHomeSimple.visibility = View.VISIBLE
    }

    private fun setNotificationTab() {
        //for notification
        binding.copySimpleNotificationIcon.visibility = View.VISIBLE
        binding.copySimpleNotificationLayout.visibility = View.VISIBLE
        binding.copySimpleNotificationText.visibility = View.GONE
        binding.copyFabNotification.visibility = View.GONE
        binding.copyFabLayout.visibility = View.VISIBLE
        binding.copyNotificationCurve.visibility = View.GONE
        binding.copyNotificationSimple.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
    }
}