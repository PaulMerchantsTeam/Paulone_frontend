package com.paulmerchants.gold.ui.splash

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants.IS_USER_EXIST
import com.paulmerchants.gold.common.Constants.LOGIN_WITH_MPIN
import com.paulmerchants.gold.common.Constants.OTP_VERIFIED
import com.paulmerchants.gold.common.Constants.SIGNUP_DONE
import com.paulmerchants.gold.common.Constants.SPLASH_SCRN_VISITED
import com.paulmerchants.gold.databinding.SplashFragmentBinding
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.noInternetDialog
import com.paulmerchants.gold.utility.InternetUtils
import com.paulmerchants.gold.utility.hideViewGrp
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt


@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashFragmentBinding>(SplashFragmentBinding::inflate) {
    private val TAG = javaClass.name
    private val splashViewModel: SplashViewModel by viewModels()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback


    override fun SplashFragmentBinding.initialize() {

        AppUtility.changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)
        Log.d(
            TAG,
            "initialize: ..1.......${
                AppSharedPref.getBooleanValue(
                    LOGIN_WITH_MPIN
                )
            }"
                    + "\ninitialize: ......2...${
                AppSharedPref.getBooleanValue(
                    SIGNUP_DONE
                )
            }"
                    + "\ninitialize: .....3....${
                AppSharedPref.getBooleanValue(OTP_VERIFIED)
            }"
                    + "\ninitialize: ......4...${
                AppSharedPref.getBooleanValue(
                    SPLASH_SCRN_VISITED
                )
            }"
        )

        if (AppSharedPref.getBooleanValue(LOGIN_WITH_MPIN) || AppSharedPref.getBooleanValue(SIGNUP_DONE)
        ) {
            findNavController().popBackStack(R.id.splashFragment, true)
            findNavController().navigate(
                R.id.loginScreenFrag,
                null,
                (activity as MainActivity).navOption
            )
        } else if (AppSharedPref.getBooleanValue(OTP_VERIFIED) && AppSharedPref.getBooleanValue(
                IS_USER_EXIST
            )
        ) {
            findNavController().popBackStack(R.id.splashFragment, true)
            findNavController().navigate(
                R.id.loginScreenFrag,
                null,
                (activity as MainActivity).navOption
            )
        } else if (AppSharedPref.getBooleanValue(SPLASH_SCRN_VISITED) == true) {
            findNavController().popBackStack(R.id.splashFragment, true)
            findNavController().navigate(R.id.phoenNumVerifiactionFragment)
        } else {
            binding.mainSplash.show()
        }
    }

    override fun onStart() {
        super.onStart()
        val hashedPassword = BCrypt.hashpw(BuildConfig.PASSWORD, BCrypt.gensalt(12)).trim()
        val hashedUserName = BCrypt.hashpw(BuildConfig.USERNAME, BCrypt.gensalt(12)).trim()
       val checkpw = BCrypt.checkpw(BuildConfig.PASSWORD, hashedPassword)

        Log.d(TAG, "onStart:$hashedPassword  $hashedUserName $checkpw")
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        binding.nextBtn.setOnClickListener {
            setIntroForNextCounter(splashViewModel.counter)
        }
//        splashViewModel.getLogin()
        setUpNetworkCallback()
        if (!InternetUtils.isNetworkAvailable(requireContext())) {
            lifecycleScope.launch {
                noInternetDialog()
            }
        }
    }

    private fun setIntroForNextCounter(counter: Int) {
        AppUtility.changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)
        Log.d(TAG, "setIntroForNextCounter: $counter")
        if (counter == 3) {
            binding.apply {
//                personIv.startAnimation(
//                    AnimationUtils.loadAnimation(
//                        requireContext(), R.anim.slide_rotate
//                    )
//                )
                lifecycleScope.launch {
                    delay(600)
                    AppSharedPref.putBoolean(SPLASH_SCRN_VISITED, true)
                    val homeDestinationId = R.id.phoenNumVerifiactionFragment
                    val currentBackStackEntry = findNavController().currentBackStackEntry
                    val backStackIds = currentBackStackEntry?.destination?.id
                    if (backStackIds != null && backStackIds == homeDestinationId) {
                        // If the home destination is already on the back stack, pop the back stack
                        findNavController().popBackStack(homeDestinationId, false)
                    } else {
                        // If the home destination is not on the back stack, navigate to it
                        findNavController().navigate(homeDestinationId)
                    }
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
//        try {
//            AppUtility.progressBarAlert(requireContext())
////            splashViewModel.getLogin2((activity as MainActivity).mLocation)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
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

    private fun setUpNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Network connection is available, perform actions here
                // For example:
                // fetchData()
                Log.d(TAG, "onAvailable: ...........internet")
                lifecycleScope.launch(Dispatchers.Main) {
                    animateOne()
                }
            }

            override fun onLost(network: Network) {
                // Network connection is lost, handle accordingly
                // For example:
                // showNoInternetMessage()
                Log.d(TAG, "onLost: ..................")
                lifecycleScope.launch {
                    noInternetDialog()
                }
            }
        }

        // Register the network callback
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the network callback to avoid memory leaks
        if (this::connectivityManager.isInitialized) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
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