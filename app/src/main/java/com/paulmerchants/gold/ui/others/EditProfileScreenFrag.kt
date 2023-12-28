package com.paulmerchants.gold.ui.others

import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.EditProfileLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
//            showCustomDialogOTPVerify(context=requireContext())
        }
        binding.hearderEditProf.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.confirmBtn.setOnClickListener {
            binding.userImageIv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_zoom_in))
            lifecycleScope.launch {
                delay(300)
                findNavController().navigateUp()

            }
        }
    }

    override fun onResume() {
        super.onResume()
    }


}