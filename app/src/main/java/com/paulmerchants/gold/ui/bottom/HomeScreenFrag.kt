package com.paulmerchants.gold.ui.bottom

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.MoreToComeAdapter
import com.paulmerchants.gold.adapter.PrePaidCardAdapter
import com.paulmerchants.gold.adapter.UpcomingLoanAdapter
import com.paulmerchants.gold.adapter.UpcomingLoanNewuserAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants.DUE_LOAN_DATA
import com.paulmerchants.gold.common.AppNameGlideModule
import com.paulmerchants.gold.common.GlideApp
import com.paulmerchants.gold.databinding.DummyHomeScreenFragmentBinding
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.MoreToComeModel
import com.paulmerchants.gold.model.PrepaidCardModel
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.changeStatusBarWithReqdColor
import com.paulmerchants.gold.utility.AppUtility.hideShim
import com.paulmerchants.gold.utility.AppUtility.noInternetDialog
import com.paulmerchants.gold.utility.AppUtility.showShimmer
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.InternetUtils
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch



@AndroidEntryPoint
class HomeScreenFrag :
    BaseFragment<DummyHomeScreenFragmentBinding>(DummyHomeScreenFragmentBinding::inflate) {

    private val upcomingLoanAdapter = UpcomingLoanAdapter(::onPayDueClicked)
    private val upcomingNewUserAdapter = UpcomingLoanNewuserAdapter()
    private val prePaidCardAdapter = PrePaidCardAdapter(::onClicked)
    private lateinit var secureFiles: SecureFiles
    lateinit var navController: NavController
    private val moreToComeAdapter = MoreToComeAdapter()
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    private val bannerList = listOf(
        MoreToComeModel(
            R.drawable.one_place_to_iv,
            1,
            "One place\n to pay bills",
            "Rent, Electricity, Mobile Bill & more"
        ),
        MoreToComeModel(
            R.drawable.no_more_delay_pay,
            2,
            "No more delay\n in paying bills",
            "Get timely notifications of payments"
        ),
    )
    private val TAG = "HomeScreenFrag"
    override fun DummyHomeScreenFragmentBinding.initialize() {
        changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)
        navController = findNavController()
        secureFiles = SecureFiles()
        (activity as MainActivity).locationProvider.startLocationUpdates()

//        setUpComingOurServices()

    }

    override fun onStart() {
        super.onStart()
//        (activity as MainActivity).commonViewModel.getFestDetailsForHeaderHomePage()
        (activity as MainActivity).commonViewModel.isUnderMainLiveData.observe(this) {
            it?.let {
                if (it.statusCode == "200") {
                    if (it.data.down) {
                        findNavController().navigate(R.id.mainScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                    } else {
                        setUpNetworkCallbackFOrDueLoans()
                    }
                }
            }
        }

//        (activity as MainActivity).commonViewModel.festivalLiveData.observe(viewLifecycleOwner) {
//            it?.let {
//                if (it.data?.isNotEmpty() == true) {
//                    if (it.data[0].showAtCurrentDate == true) {
//                        Log.e(TAG, "onStart: .,.,,,,,<${it.data.get(0).imageUrl}")
//                        val imageUrl = it.data[0].imageUrl
//                        GlideApp.with(requireContext())
//                            .load(imageUrl)
//                            .into(binding.searchProfileParent.customImageView)
////                        Glide.with(requireContext()).load(imageUrl)
////                            .into(binding.searchProfileParent.customImageView)
////                        AppNameGlideModule.with()
////                            .load(imageUrl)
////                            .centerCrop()
////                            .placeholder(R.color.splash_screen_one)
////                            .into(binding.searchProfileParent.customImageView)
//                    }
//                }
//            }
//        }
//        AppUtility.addDrawableGradient(
//            requireContext(), binding.searchProfileParent.parentTop, intArrayOf(
//                // Define your gradient colors here
//                requireContext().getColor(R.color.safron),
//                requireContext().getColor(R.color.white),
//                requireContext().getColor(R.color.pantone)
//            )
//        )
        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) { /* enabled by default */
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    Log.d("TAG", "handleOnBackPressed: ..........pressed")
                    findNavController().navigate(R.id.appCloseDialog)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        Log.d(
            TAG,
            "onStart: .......MOBILE-----....${
                AppSharedPref.getStringValue(
                    Constants.CUST_MOBILE,
                )
            }-------------------$.${
                AppSharedPref.getStringValue(
                    Constants.CUSTOMER_NAME,
                )
            }"
        )

        setProfileUi()
        // Get the system service for connectivity
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Set up the network callback

//        setUpComingDueLoans()

//        startAnimationOnIcon()
//        setUiOnHomeSweetHomeBills()
//        handleRechargeAndBillUi()
//        setPrepaidCardUi()
//        setAddCardView()
        setUpBanner()

        (activity as MainActivity).commonViewModel.tokenExpiredResp.observe(viewLifecycleOwner) {
            it?.let {
                showSnackBar(it.errorMessage)
            }
        }

//        (activity as MainActivity).commonViewModel.paymentData.observe(viewLifecycleOwner) {
//            Log.d(TAG, "ojnnnnnnn: /............$it")
//            it?.let {
//                if (it.status) {
//                    updatePaymentStatusToServer(it)
//                }
//            }
//        }

//        (activity as MainActivity).commonViewModel.respPaymentUpdate.observe(viewLifecycleOwner) {
//            it?.let {
//                if (it.status == "200") {
//                    Log.d(TAG, "ojnnnnnn: /.................$it")
////                    (activity as MainActivity).commonViewModel.getPendingInterestDues()
//                }
//            }
//        }
        binding.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                Log.i(TAG, "onRefresh: ....called")

                if (InternetUtils.isNetworkAvailable(requireContext())) {
                    (activity as MainActivity).commonViewModel.getUnderMaintenanceStatus()
                } else {
                    lifecycleScope.launch {
                        noInternetDialog()
                    }
                    binding.swiperefresh.isRefreshing = false
                }
            }

        })
    }


    private fun setUpNetworkCallbackFOrDueLoans() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Network connection is available, perform actions here
                // For example:
                // fetchData()
                Log.d(TAG, "onAvailable: ...........internet")
                lifecycleScope.launch {
                    setUpComingDueLoans()
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

        connectivityManager.registerNetworkCallback(
            networkRequest,
            networkCallback as ConnectivityManager.NetworkCallback
        )
    }

    //Remove Observer...
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ..................")
        (activity as MainActivity).commonViewModel.respPaymentUpdate.removeObservers(this)
        (activity as MainActivity).commonViewModel.paymentData.removeObservers(this)
        (activity as MainActivity).commonViewModel.tokenExpiredResp.removeObservers(this)
        (activity as MainActivity).commonViewModel.isStartAnim.removeObservers(this)

        (activity as MainActivity).commonViewModel.respPaymentUpdate.postValue(null)
        (activity as MainActivity).commonViewModel.tokenExpiredResp.postValue(null)
        (activity as MainActivity).commonViewModel.paymentData.postValue(null)
        (activity as MainActivity).commonViewModel.isStartAnim.postValue(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the network callback to avoid memory leaks
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
    }
//    private fun showHideLoadinf() {
//        binding.shimmmerParent.startShimmer()
//        binding.shimmmerParent.hideShimmer()
//        binding.shimmmerParent.hide()
//        binding.rvUpcomingDueLoans.show()
//    }


    private fun setUpBanner() {
        moreToComeAdapter.submitList(bannerList)
        binding.moreToCome.viewPagerMoreToCome.adapter = moreToComeAdapter
        TabLayoutMediator(
            binding.moreToCome.tabLayout,
            binding.moreToCome.viewPagerMoreToCome
        ) { tab, position ->

        }.attach()
    }

    private fun onPayDueClicked(dueLoans: GetPendingInrstDueRespItem) {
        if (InternetUtils.isNetworkAvailable(requireContext())) {
            (activity as MainActivity).commonViewModel.dueLoanSelected = dueLoans
            val bundle = Bundle().apply {
                putParcelable(DUE_LOAN_DATA, dueLoans)
            }
//        val quickPayDialog = QuickPayDialog()
//        quickPayDialog.setPaymentResultListener(this)

            findNavController().navigate(
                R.id.quickPayDialog, bundle
            )
        } else {
            noInternetDialog()
        }

    }

    /**
     * if (InternetUtils.isNetworkAvailable(requireContext())) {
     *   } else {
     *   noInternetDialog()
     *   }
     */

    private fun onClicked(prepaidCardModel: PrepaidCardModel) {
        findNavController().navigate(R.id.pcFrag)
    }

    private fun setPrepaidCardUi() {
        val prepaidCard1 = PrepaidCardModel(1, 4, "Prithvi Kumar")
        val prepaidCard2 = PrepaidCardModel(2, 4, "Swati")
        val prepaidCard3 = PrepaidCardModel(3, 4, "Arjun S Narayanan")
        prePaidCardAdapter.submitList(listOf(prepaidCard1, prepaidCard2, prepaidCard3))
        binding.prepaidCardRv.adapter = prePaidCardAdapter
    }

    private fun setProfileUi() {
        val userFirstName =
            AppUtility.getFirstName(
                AppSharedPref.getStringValue(
                    Constants.CUSTOMER_NAME
                )
            )
        Log.d(TAG, "setProfileUi: ...............$userFirstName")
        binding.searchProfileParent.userName.text = "Hey ${
            AppSharedPref.getStringValue(
                Constants.CUSTOMER_NAME,
            )?.substringBefore(" ")
        }"
        binding.searchProfileParent.firtLetterUser.text = "${
            AppSharedPref.getStringValue(
                Constants.CUSTOMER_NAME,
            )?.first() ?: "U"
        }"
//        (activity as MainActivity).commonViewModel.isStartAnim.observe(viewLifecycleOwner) {
//            it?.let {
//                if (it) {
//                    animateHintEditText()
//                }
//            }
//        }

        binding.searchProfileParent.apply {
            searchView.setOnClickListener {
                (activity as MainActivity).commonViewModel.isStartAnim.postValue(false)
                binding.searchProfileParent.searchView.clearAnimation()
            }

            searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    (activity as MainActivity).commonViewModel.isStartAnim.postValue(false)
                    binding.searchProfileParent.searchView.clearAnimation()

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    (activity as MainActivity).commonViewModel.isStartAnim.postValue(false)
                    binding.searchProfileParent.searchView.clearAnimation()
                    p0?.let { char ->
                        Log.d("TAG", "onTextChanged: text = $char")
                        if (char.isNotEmpty()) {
                            (activity as MainActivity).commonViewModel.isStartAnim.postValue(false)
                            binding.searchProfileParent.searchView.clearAnimation()

//                            searchView.setCompoundDrawablesWithIntrinsicBounds(
//                                0,
//                                0,
//                                0,
//                                0
//                            )
                        } else {
                            (activity as MainActivity).commonViewModel.isStartAnim.postValue(false)
                            binding.searchProfileParent.searchView.clearAnimation()

                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    Log.d("TAG", "afterTextChanged: $p0")
                }
            })
        }
        profileHandle()
    }

    private fun profileHandle() {
        binding.searchProfileParent.profileIv.setOnClickListener {
            if (InternetUtils.isNetworkAvailable(requireContext())) {
                findNavController().navigate(
                    R.id.profileFrag
                )
            } else {
                noInternetDialog()
            }

        }
        binding.searchProfileParent.notImage.setOnClickListener {
            if (InternetUtils.isNetworkAvailable(requireContext())) {
                findNavController().navigate(R.id.transactionFrag)
            } else {
                noInternetDialog()
            }

        }
    }

    /* private fun handleRechargeAndBillUi() {
         */
    /**
     * initially hide ---home_sweet_home, financial_Security,transit_window ,back_top
     * |-----flow----|
     * on ... more click
     * hide own parent icon  and change behaviour too
     * show---home_sweet_home, financial_Security,transit_window ,back_top
     *
     *//*
        binding.allPaymnetActionParent.moreParent.setOnClickListener {
            binding.allPaymnetActionParent.billsNRechargerParent.hide()
            binding.allPaymnetActionParent.forMoreParentAllAction.show()
            binding.allPaymnetActionParent.backToNormalAction.show()

        }
        binding.allPaymnetActionParent.backToNormalAction.setOnClickListener {
            binding.allPaymnetActionParent.apply {
                billsNRechargerParent.show()
                forMoreParentAllAction.hide()
                backToNormalAction.hide()

            }


        }

        binding.allPaymnetActionParent.homeLoanParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.HomeLoan.type), findNavController())
        }

        binding.allPaymnetActionParent.personalLoanParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.PersonalLoan.type), findNavController())

        }
        binding.allPaymnetActionParent.creditCardParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.CreditCard.type), findNavController())

        }
        binding.allPaymnetActionParent.dthServiceActionParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.DthService.type), findNavController())

        }
        binding.allPaymnetActionParent.dthServiceActionParent2.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.DthService.type), findNavController())

        }
        binding.allPaymnetActionParent.electricityParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Electricity.type), findNavController())

        }
        binding.allPaymnetActionParent.boradBandParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Broadband.type), findNavController())

        }
        binding.allPaymnetActionParent.MobileParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Mobile.type), findNavController())

        }
        binding.allPaymnetActionParent.mobileParent2.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MobileRecharge.type), findNavController())

        }
        binding.allPaymnetActionParent.mobPostPaidParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MobilePostpaid.type), findNavController())

        }
        binding.allPaymnetActionParent.ottParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.OttWorld.type), findNavController())

        }
        binding.allPaymnetActionParent.insuranceParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Insurance.type), findNavController())

        }
        binding.allPaymnetActionParent.muncipalTaxParent.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
//            AppUtility.onBillClicked(ActionItem(BbpsType.MunicipalTax.type), findNavController())

        }
        binding.allPaymnetActionParent.fastTagParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.FastTag.type), findNavController())

        }
        binding.allPaymnetActionParent.challanTraffitParent.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
//            AppUtility.onBillClicked(ActionItem(BbpsType.Challan.type), findNavController())

        }
        binding.allPaymnetActionParent.metroCardParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MetroCard.type), findNavController())

        }

    }*/

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ...............")
//        binding.searchProfileParent.searchView.show()
//        (activity as MainActivity).commonViewModel.isStartAnim.postValue(true)
//
//        binding.searchProfileParent.searchView.startAnimation(AnimationUtils.loadAnimation(
//            requireContext(), R.anim.slide_down_to_mid
//        ).apply {
//            this.start()
//        })

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ...........")
//        (activity as MainActivity).commonViewModel.isStartAnim.postValue(false)
//        binding.searchProfileParent.searchView.clearAnimation()
//        binding.searchProfileParent.searchView.startAnimation(AnimationUtils.loadAnimation(
//            requireContext(), R.anim.slide_down_to_mid
//        ).apply {
//            this.cancel()
//        })

    }

    /*    private fun updatePaymentStatusToServer(statusData: StatusPayment) {
            Log.d(TAG, "updatePaymentStatusToServer: ....$statusData")
            (activity as MainActivity).commonViewModel.updatePaymentStatus(
                AppSharedPref,
                status = statusData.status,
                razorpay_payment_id = statusData.paymentData?.paymentId.toString(),
                razorpay_order_id = statusData.paymentData?.orderId.toString(),
                razorpay_signature = statusData.paymentData?.signature.toString(),
                custId = AppSharedPref.getStringValue(Constants.CUSTOMER_ID)
                    .toString(),
                amount = 100.00,
                contactCount = 0,
                description = "test___"
            )
            }
        */


    /* private fun animateHintEditText() {
         val strList = listOf(
             getString(R.string.search_fr_bills),
             getString(R.string.search_fr_credit),
             getString(R.string.search_fr_upcoming_dues),
             getString(R.string.search_for_loans),
         )
         lifecycleScope.launchWhenResumed {
             delay(1000)
             binding.searchProfileParent.searchView.hint = strList[0]
             binding.searchProfileParent.searchView.startAnimation(
                 AnimationUtils.loadAnimation(
                     requireContext(), R.anim.slide_down_to_mid
                 )
             )
             delay(1000)
             binding.searchProfileParent.searchView.startAnimation(
                 AnimationUtils.loadAnimation(
                     requireContext(), R.anim.slide_mid_to_up
                 )
             )
             delay(1000)
             binding.searchProfileParent.searchView.hint = strList[1]
             binding.searchProfileParent.searchView.startAnimation(
                 AnimationUtils.loadAnimation(
                     requireContext(), R.anim.slide_down_to_mid
                 )
             )
             delay(1000)
             binding.searchProfileParent.searchView.startAnimation(
                 AnimationUtils.loadAnimation(
                     requireContext(), R.anim.slide_mid_to_up
                 )
             )
             delay(1000)
             binding.searchProfileParent.searchView.hint = strList[2]
             binding.searchProfileParent.searchView.startAnimation(
                 AnimationUtils.loadAnimation(
                     requireContext(), R.anim.slide_down_to_mid
                 )
             )
             delay(1000)
             binding.searchProfileParent.searchView.startAnimation(
                 AnimationUtils.loadAnimation(
                     requireContext(), R.anim.slide_mid_to_up
                 )
             )


             delay(1000)
             binding.searchProfileParent.searchView.hint = strList[3]
             binding.searchProfileParent.searchView.startAnimation(
                 AnimationUtils.loadAnimation(
                     requireContext(), R.anim.slide_down_to_mid
                 )
             )
             delay(1000)
             binding.searchProfileParent.searchView.startAnimation(
                 AnimationUtils.loadAnimation(
                     requireContext(), R.anim.slide_mid_to_up
                 )
             )

             delay(1000)
             (activity as MainActivity).commonViewModel.isStartAnim.postValue(true)
         }

     }*/


    /*   private fun startAnimationOnIcon() {
           binding.allPaymnetActionParent.apply {
               goldIv.startCustomAnimation(R.drawable.anim_gold_icon)
               dthIV.startCustomAnimation(R.drawable.anim_dth_service_icon)
               elecIv.startCustomAnimation(R.drawable.anim_elec_icon)
               boradBandIv.startCustomAnimation(R.drawable.anim_broadband_icon)
               mobileIv.startCustomAnimation(R.drawable.anim_mobile_icon)
               mob2Iv.startCustomAnimation(R.drawable.anim_mobile_icon)
               mobPostIv.startCustomAnimation(R.drawable.anim_post_paid_icon)
               ottIv.startCustomAnimation(R.drawable.anim_ott_icon)
               dthIV2.startCustomAnimation(R.drawable.anim_dth_service_icon)
               isuranceIv.startCustomAnimation(R.drawable.anim_insurance_icon)
               mucipalIv.startCustomAnimation(R.drawable.anim_munciple_tax)
               fastIv.startCustomAnimation(R.drawable.anim_fastag_icon)
               challanIv.startCustomAnimation(R.drawable.anim_challan_icon)
               metroCardIv.startCustomAnimation(R.drawable.anim_metro_card)

           }
       }*/

    /*  private fun setUpComingOurServices() {
          val ourServices1 = OurServices(
              R.drawable.gold_loan_hand, getString(R.string.gold_n_loans), R.color.yellow_main
          )
          val ourServices2 = OurServices(
              R.drawable.hand_prepaid_card, getString(R.string.prepaid_n_cards), R.color.green_main
          )
          val ourServices3 = OurServices(
              R.drawable.hand_invoice, getString(R.string.bills_n_payment), R.color.sky_blue_main
          )
          val ourServices4 = OurServices(
              R.drawable.hand_digital_gold, getString(R.string.digital_n_gold), R.color.orange_main
          )
          val serviceList = listOf(ourServices1, ourServices2, ourServices3, ourServices4)
          upcomingNewUserAdapter.submitList(serviceList)
          binding.rvUpcomingDueLoans.adapter = upcomingNewUserAdapter

      }
  */
    private fun setUpComingDueLoans() {
        binding.shimmmerParent.showShimmer()
//        val currentDate = AppUtility.getCurrentDate()
//        Log.d(TAG, "setUpComingDueLoans: ..currentDate..$currentDate")
//        Log.d(
//            TAG, "setUpComingDueLoans: ..currentDate..${
//                secureFiles.encryptKey(
//                    currentDate, BuildConfig.SECRET_KEY_GEN
//                )
//            }"
//        )
//        val encDate = secureFiles.encryptKey(
//            currentDate, BuildConfig.SECRET_KEY_GEN
//        )
//        if (!(activity as MainActivity).commonViewModel.isCalled) {
        (activity as MainActivity).commonViewModel.getPendingInterestDues(AppSharedPref,(activity as MainActivity).mLocation)
        (activity as MainActivity).commonViewModel.getLoanOutstanding(AppSharedPref,(activity as MainActivity).mLocation)

//            (activity as MainActivity).commonViewModel.isCalled = true
//        }
        (activity as MainActivity).commonViewModel.getPendingInterestDuesLiveData.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                (activity as MainActivity).commonViewModel.notZero =
                    it.pendingInterestDuesResponseData.filter {
                        it.payableAmount != 0.0
                    }
                Log.i(
                    TAG,
                    "setUpComingDueLoans: ${(activity as MainActivity).commonViewModel.notZero}"
                )
                for (i in it.pendingInterestDuesResponseData) {
                    i.currentDate = it.currentDate
                }
                if ((activity as MainActivity).commonViewModel.notZero.isNotEmpty()) {
                    upcomingLoanAdapter.submitList((activity as MainActivity).commonViewModel.notZero)
                    binding.rvUpcomingDueLoans.adapter = upcomingLoanAdapter
                    binding.rvUpcomingDueLoans.show()
                } else {
                    binding.noIntHaveParent.root.show()
                }
                binding.swiperefresh.isRefreshing = false
//                setLoanOverView()
                binding.shimmmerParent.hideShim()

            }
        }

        (activity as MainActivity).commonViewModel.getRespGetLoanOutStandingLiveData.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                for (i in it.getLoanOutstandingResponseData) {
                    i.currentDate = it.currentDate
                }
                setLoanOverView(it.getLoanOutstandingResponseData)
            }
        }
//        val dueLoans1 = DueLoans(1, 4, 6000)
//        val dueLoans2 = DueLoans(2, 4, 6000)
//        val dueLoans3 = DueLoans(3, 4, 6000)
//        val dueLoans4 = DueLoans(4, 4, 6000)
//        val list = listOf(dueLoans1, dueLoans2, dueLoans3, dueLoans4)
//        upcomingLoanAdapter.submitList(list)
//        binding.rvUpcomingDueLoans.adapter = upcomingLoanAdapter
    }

    /* private fun setUiOnHomeSweetHomeBills() {
         binding.allPaymnetActionParent.homeSweetHomBillsRv.setUiOnHomeSweetHomeBills(
             requireContext(), ::onBillClicked
         )
     }*/

    private fun setLoanOverView(resp: List<RespGetLoanOutStandingItem>) {
        var totalAmount = 0.0
        binding.apply {
//            loanOverViewCardParent.cardParent.setBackgroundColor(R.drawable.new_user_card_grad)

//            loanOverViewCardParent.viewLoanBtn.text = getString(R.string.apply_now)
//            loanOverViewCardParent.viewLoanBtn.setOnClickListener {
//                findNavController().navigate(R.id.applyLoanForNewUser)
//            }            android:text="You are having 4 active loans totalling upto"
            loanOverViewCardParent.youHaveTotalLoanTv.text =
                "You are having ${resp.size} active loans totalling interest due up to"
            for (i in resp) {
                i.payableAmount?.let {
                    totalAmount += i.payableAmount
                }
            }
//            if (totalAmount == 0) {
//
//            }
            Log.d(TAG, "setLoanOverView: ......${totalAmount}")
            loanOverViewCardParent.totalLoanAmountTv.text = "INR $totalAmount"
            loanOverViewCardParent.viewLoanBtn.setOnClickListener {
                findNavController().navigate(R.id.goldLoanScreenFrag)

            }
//            loanOverViewCardParent.renewLoansTv.show()
            loanOverViewCardParent.youHaveTotalLoanTv.show()
        }
        binding.shimmerCardLoanOverView.hideShim()
        binding.loanOverViewCardParent.root.show()
    }

    /*    private fun setAddCardView() {
            binding.apply {
                addCardBtn.setOnClickListener {
                    findNavController().navigate(R.id.addUpiCard)
                }
            }
        }*/
    /*

        private fun onBillClicked(actionItem: ActionItem) {
            AppUtility.onBillClicked(actionItem, findNavController())
        }
    */


}
