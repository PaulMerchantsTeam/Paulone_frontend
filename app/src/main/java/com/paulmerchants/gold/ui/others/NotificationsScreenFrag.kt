package com.paulmerchants.gold.ui.others

import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.CreditScoreScreenBinding
import com.paulmerchants.gold.databinding.FragmentNotiificationBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationsScreenFrag :
    BaseFragment<FragmentNotiificationBinding>(CreditScoreScreenBinding::inflate) {

    override fun FragmentNotiificationBinding.initialize() {
        modifyHeaders()
    }

    override fun onStart() {
        super.onStart()


    }

    private fun modifyHeaders() {
        binding.headrNotification.titlePageTv.text = getString(R.string.notifications)
        binding.headrNotification.apply {
            endIconIv.setImageResource(R.drawable.phone_call)
            endIconIv.show()
        }
    }


}