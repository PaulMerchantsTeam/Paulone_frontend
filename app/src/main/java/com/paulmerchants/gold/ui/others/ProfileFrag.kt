package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.ProfileLayoutBinding
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.setServicesUi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFrag : BaseFragment<ProfileLayoutBinding>(ProfileLayoutBinding::inflate) {

    override fun ProfileLayoutBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        handlesClicks()
        settingUi()
    }

    private fun handlesClicks() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.editProfileBtn.setOnClickListener {
binding.toolbar.hide()
            binding.appBarCollaps.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
           binding.backProfileIv.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.slide_zoom_out))
            lifecycleScope.launch{
                delay(300)
                findNavController().navigate(
                    R.id.editProfileScreenFrag,
                    null
                )
            }

        }
    }

    private fun settingUi() {
        binding.profileSettingsRv.setServicesUi(requireContext(),::onMenuServiceClicked, ::onMenuServiceClickedTwo,::onMenuServiceClickedTitle )
    }

    private fun onMenuServiceClicked(menuServices: MenuServices) {
         var headerValue: Int? = null
        when (menuServices.serviceId) {
            100 -> {
                headerValue = 100

                val changePinBundle = Bundle().apply {
                    putInt("ProfileChangePin",headerValue)
                }

                findNavController().navigate(R.id.phoenNumVerifiactionFragment,changePinBundle)
            }
            101 -> {
                findNavController().navigate(R.id.menuScreenFrag)
            }
            103 -> {
               //Need Ui for your spent
            }
            104 -> {
//playStore
            }
            105 -> {
                findNavController().navigate(R.id.phoenNumVerifiactionFragment)
                findNavController().navigateUp()
//                AppSharedPref.clearSharedPref()
            }
            else -> {

            }
        }
    }
    private fun onMenuServiceClickedTwo(menuServices: MenuServices) {
        when (menuServices.serviceId) {
            104 -> {
findNavController().navigate(R.id.raiseComplaintFrag)
            }
        }
    }
    private fun onMenuServiceClickedTitle(menuServices: MenuServices) {
        when (menuServices.serviceId) {
            105 -> {
                Log.d("TAG", "onMenuServiceClickedTitle: logoutClicked")
                findNavController().navigate(R.id.phoenNumVerifiactionFragment)
//                findNavController().navigateUp()
                AppSharedPref.clearSharedPref()
            }
        }
    }




}