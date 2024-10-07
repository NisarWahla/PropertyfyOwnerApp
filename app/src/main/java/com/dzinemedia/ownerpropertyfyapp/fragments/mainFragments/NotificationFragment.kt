package com.dzinemedia.ownerpropertyfyapp.fragments.mainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzinemedia.ownerpropertyfyapp.R
import com.dzinemedia.ownerpropertyfyapp.activities.MainActivity
import com.dzinemedia.ownerpropertyfyapp.adapters.NotificationAdapter
import com.dzinemedia.ownerpropertyfyapp.databinding.FragmentNotificationBinding
import com.dzinemedia.ownerpropertyfyapp.models.NotificationModel
import com.dzinemedia.tenantpropertyapp.utility.DebounceClickHandler

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentNotificationBinding
    private var notificationAdapter: NotificationAdapter? = null
    private val notificationList: ArrayList<NotificationModel> = ArrayList()

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
            inflater, R.layout.fragment_notification, container, false
        )
        setViewClickListener()
        notificationAdapter = NotificationAdapter(notificationList)
        val layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.notificationsRecyclerview.layoutManager = layoutManager
        binding.notificationsRecyclerview.adapter = notificationAdapter
        return binding.root
    }

    private fun setViewClickListener() {
        binding.menuIcon.setOnClickListener(DebounceClickHandler {
            (activity as MainActivity).openDrawer()
        })
    }
}