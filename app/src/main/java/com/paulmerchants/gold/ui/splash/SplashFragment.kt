package com.paulmerchants.gold.ui.splash

import android.util.Log
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants.LOGIN_DONE
import com.paulmerchants.gold.common.Constants.SIGNUP_DONE
import com.paulmerchants.gold.common.Constants.SPLASH_SCRN_VISITED
import com.paulmerchants.gold.databinding.SplashFragmentBinding
import com.paulmerchants.gold.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.hideViewGrp
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashFragmentBinding>(SplashFragmentBinding::inflate) {
    private val TAG = javaClass.name
    private val splashViewModel: SplashViewModel by viewModels()

    override fun SplashFragmentBinding.initialize() {
        AppUtility.changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)

        if (AppSharedPref.getBooleanValue(SIGNUP_DONE) || AppSharedPref.getBooleanValue(LOGIN_DONE)) {
            findNavController().popBackStack(R.id.splashFragment, true)
            findNavController().navigate(
                R.id.homeScreenFrag,
                null,
                (activity as MainActivity).navOption
            )
        } else if (AppSharedPref.getBooleanValue(SPLASH_SCRN_VISITED)) {
            findNavController().popBackStack(R.id.splashFragment, true)
            findNavController().navigate(R.id.phoenNumVerifiactionFragment)
        } else {
            binding.mainSplash.show()
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Main) {
            animateOne()
        }
        binding.nextBtn.setOnClickListener {
            setIntroForNextCounter(splashViewModel.counter)
        }
//        splashViewModel.getLogin()

    }

    private fun setIntroForNextCounter(counter: Int) {
        AppUtility.changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)
        Log.d(TAG, "setIntroForNextCounter: $counter")
        if (counter == 3) {
            binding.apply {
                personIv.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(), R.anim.slide_rotate
                    )
                )
                lifecycleScope.launch {
                    delay(600)
                    AppSharedPref.putBoolean(SPLASH_SCRN_VISITED, true)
                    findNavController().navigate(
                        R.id.phoenNumVerifiactionFragment,
                        null,
                        (activity as MainActivity).navOption
                    )
                }
                return
            }
        }
        splashViewModel.setValue()
        updateTopCounter()
        when (counter) {
            0 -> {
                binding.apply {

                }

            }

            1 -> {
                binding.apply {
                    personIv.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(), R.anim.slide_rotate
                        )
                    )
                    lifecycleScope.launch {
                        delay(600)
                        headingTv.text = getString(R.string.your_payment_checklist)
                        descPageTv.text = getString(R.string.register_all_the_detail)
                        headingTvSupport.text = getString(R.string.sorted)
                        circleBtmIv.setBackgroundResource(R.drawable.cirlce_yellow)
                        personIv.setBackgroundResource(R.drawable.online_payment_person_plant)
                        personIv.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.slide_in_left
                            )
                        )

                        nextBtn.apply {

                            setBackgroundColor(
                                getColor(
                                    requireContext(),
                                    R.color.yellow_main
                                )
                            )
                            setTextColor(getColor(requireContext(), R.color.splash_screen_three))
                            setStrokeColorResource(R.color.splash_screen_three)


                        }
                    }


                }
            }

            2 -> {
                binding.apply {
                    personIv.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(), R.anim.slide_rotate
                        )
                    )
                    lifecycleScope.launch {
                        delay(600)
                        headingTv.text = getString(R.string.payment_is_equal)
                        descPageTv.text = getString(R.string.we_dont_just_ask)
                        headingTvSupport.text = getString(R.string.rewards)
                        circleBtmIv.setBackgroundResource(R.drawable.circle_sky_blue)
                        personIv.setBackgroundResource(R.drawable.person_box)
                        personIv.startAnimation(
                            AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.slide_in_left
                            )
                        )
                        nextBtn.apply {
                            text = getString(R.string.proceed)
                            setBackgroundColor(
                                getColor(
                                    requireContext(),
                                    R.color.splash_screen_three
                                )
                            )
                            setTextColor(getColor(requireContext(), R.color.splash_screen_two))
                            setStrokeColorResource(R.color.splash_screen_two)
                        }
                    }

                }
            }
        }

    }

    private fun updateTopCounter() {
        binding.countTv.text = "${splashViewModel.counter}/3"
    }

    private suspend fun animateOne() {
//        AppAnimation.scaler(binding.imageView)
        delay(1000)
        animateSecondScreen(R.color.splash_screen_three, R.color.white)
        delay(1000)
        animateSecondScreen(R.color.splash_screen_two, R.color.splash_screen_one)
        delay(1000)
        showFirstPageIntro()
    }

    private fun showFirstPageIntro() {
        AppUtility.changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)
        binding.apply {
            mainSplash.hideViewGrp()
            introMainPage.show()
        }
        splashViewModel.setValue()
    }

    private fun animateSecondScreen(colorBg: Int, tvColor: Int) {
        AppUtility.changeStatusBarWithReqdColor(requireActivity(), colorBg)
        lifecycleScope.launch(Dispatchers.Main) {
            binding.apply {
                mainSplash.setBackgroundColor(
                    getColor(
                        requireContext(), colorBg
                    )
                )
                poweredByTv.setTextColor(
                    getColor(
                        requireContext(), tvColor
                    )
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()

    }
}