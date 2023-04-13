package com.paulmerchants.gold.ui.others

import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.ProfileLayoutBinding
import com.paulmerchants.gold.databinding.QuickPayPopupBinding
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.setServicesUi
import dagger.hilt.android.AndroidEntryPoint

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
            findNavController().navigate(
                R.id.editProfileScreenFrag,
                null,
                (activity as MainActivity).navOption
            )
        }
    }

    private fun settingUi() {
        binding.profileSettingsRv.setServicesUi(requireContext())
    }

}