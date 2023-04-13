package com.paulmerchants.gold.ui.others

import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.EditProfileLayoutBinding
import com.paulmerchants.gold.utility.showCustomDialogOTPVerify
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileScreenFrag :
    BaseFragment<EditProfileLayoutBinding>(EditProfileLayoutBinding::inflate) {

    override fun EditProfileLayoutBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        handleClicks()
    }

    private fun handleClicks() {
        binding.verifyPanCardBtn.setOnClickListener {
            showCustomDialogOTPVerify(requireContext())
        }
        binding.hearderEditProf.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.confirmBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
    }


}