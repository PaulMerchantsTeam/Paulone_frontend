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
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.MoreToComeAdapter
import com.paulmerchants.gold.adapter.UpcomingLoanAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants.DUE_LOAN_DATA
import com.paulmerchants.gold.databinding.DummyHomeScreenFragmentBinding
import com.paulmerchants.gold.model.other.MoreToComeModel
import com.paulmerchants.gold.model.responsemodels.PendingInterestDuesResponseData
import com.paulmerchants.gold.model.responsemodels.RespGetLoanOutStandingItem
import com.paulmerchants.gold.mylog.LogUtil.showLogD
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.changeStatusBarWithReqdColor
import com.paulmerchants.gold.utility.AppUtility.getTwoDigitAfterDecimal
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

/*   override fun onStart() {
        super.onStart()
        setupUI()
        handleInternetStatus()
        registerBackButtonHandler()
        setProfileUI()
    }

    private fun setupUI() {
        changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)
        navController = findNavController()
        secureFiles = SecureFiles()
        (activity as MainActivity).locationProvider.startLocationUpdates()
        setupBanner()
    }

    private fun handleInternetStatus() {
        if (InternetUtils.isNetworkAvailable(requireContext())) {
            (activity as MainActivity).commonViewModel.getUnderMaintenanceStatus()
        } else {
            lifecycleScope.launch { noInternetDialog() }
            binding.swiperefresh.isRefreshing = false
        }

        observeMaintenanceStatus()
    }

    private fun observeMaintenanceStatus() {
        (activity as MainActivity).commonViewModel.isUnderMainLiveData.observe(this) {
            it?.let { status ->
                when {
                    status.data.down && status.data.id == 1 -> navController.navigate(R.id.mainScreenFrag)
                    status.data.down && status.data.id == 2 -> {
                        navController.navigate(R.id.loginScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                        (activity as MainActivity).binding.underMainTimerParent.root.show()
                    }

                    else -> setupNetworkCallbackForDueLoans()
                }
            }
        }
    }

    private fun setupNetworkCallbackForDueLoans() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                lifecycleScope.launch {
                    binding.swiperefresh.isRefreshing = true
                    setupUpcomingDueLoans()
                }
            }

            override fun onLost(network: Network) {
                lifecycleScope.launch { noInternetDialog() }
            }
        }

        val networkRequest =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
    }

    private fun registerBackButtonHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.appCloseDialog)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setProfileUI() {
        val userName =
            AppSharedPref.getStringValue(Constants.CUSTOMER_NAME)?.substringBefore(" ") ?: "User"
        val firstLetter = AppSharedPref.getStringValue(Constants.CUSTOMER_NAME)?.first() ?: 'U'
        binding.searchProfileParent.userName.text = "Hey $userName"
        binding.searchProfileParent.firtLetterUser.text = firstLetter.toString()
        setupSearchProfile()
    }

    private fun setupSearchProfile() {
        binding.searchProfileParent.apply {
            searchView.setOnClickListener { clearSearchAnimation() }
            searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = clearSearchAnimation()

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    clearSearchAnimation()
                    if (s.isNullOrEmpty()) clearSearchAnimation()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun clearSearchAnimation() {
        (activity as MainActivity).commonViewModel.isStartAnim.postValue(false)
        binding.searchProfileParent.searchView.clearAnimation()
    }

    private fun setupBanner() {
        moreToComeAdapter.submitList(bannerList)
        binding.moreToCome.viewPagerMoreToCome.adapter = moreToComeAdapter
        TabLayoutMediator(
            binding.moreToCome.tabLayout,
            binding.moreToCome.viewPagerMoreToCome
        ) { _, _ -> }.attach()
    }

    private fun onPayDueClicked(dueLoan: GetPendingInrstDueRespItem) {
        if (InternetUtils.isNetworkAvailable(requireContext())) {
            (activity as MainActivity).commonViewModel.dueLoanSelected = dueLoan
            val bundle = Bundle().apply { putParcelable(DUE_LOAN_DATA, dueLoan) }
            navController.navigate(R.id.quickPayDialog, bundle)
        } else {
            noInternetDialog()
        }
    }

    private fun setupUpcomingDueLoans() {
        binding.shimmmerParent.showShimmer()
        (activity as MainActivity).commonViewModel.getPendingInterestDues(
            AppSharedPref,
            (activity as MainActivity).mLocation
        )
        (activity as MainActivity).commonViewModel.getLoanOutstanding(
            AppSharedPref,
            (activity as MainActivity).mLocation
        )

        (activity as MainActivity).commonViewModel.getPendingInterestDuesLiveData.observe(
            viewLifecycleOwner
        ) { pendingDueResp ->
            pendingDueResp?.let {
                setupDueLoansRecyclerView(it.pendingInterestDuesResponseData, it.currentDate)
                binding.swiperefresh.isRefreshing = false
                binding.shimmmerParent.hideShim()
            }
        }
    }

    private fun setupDueLoansRecyclerView(
        dueLoans: List<GetPendingInrstDueRespItem>,
        currentDate: String
    ) {
        if (dueLoans.isNotEmpty()) {
            upcomingLoanAdapter.submitList(dueLoans)
            binding.rvUpcomingDueLoans.adapter = upcomingLoanAdapter
            binding.rvUpcomingDueLoans.show()
        } else {
            binding.noIntHaveParent.root.show()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterObservers()
    }

    override fun onDestroyView() {
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        super.onDestroyView()
    }

    private fun unregisterObservers() {
        (activity as MainActivity).commonViewModel.apply {
            respPaymentUpdate.removeObservers(this@HomeScreenFrag)
            paymentData.removeObservers(this@HomeScreenFrag)
            tokenExpiredResp.removeObservers(this@HomeScreenFrag)
            isStartAnim.removeObservers(this@HomeScreenFrag)
        }
    }
}*/

@AndroidEntryPoint
class HomeScreenFrag :
    BaseFragment<DummyHomeScreenFragmentBinding>(DummyHomeScreenFragmentBinding::inflate) {

    private val upcomingLoanAdapter = UpcomingLoanAdapter(::onPayDueClicked)
    private val moreToComeAdapter = MoreToComeAdapter()
    private lateinit var secureFiles: SecureFiles
    private lateinit var navController: NavController
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    private companion object {
        const val TAG = "HomeScreenFrag"
        val bannerList = listOf(
            MoreToComeModel(
                R.drawable.one_place_to_iv,
                1,
                "One place to pay bills",
                "Rent, Electricity, Mobile Bill & more"
            ),
            MoreToComeModel(
                R.drawable.no_more_delay_pay,
                2,
                "No more delay in paying bills",
                "Get timely notifications of payments"
            )
        )
    }

    override fun DummyHomeScreenFragmentBinding.initialize() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

    }

    private fun observeViewModel() {
        binding.shimmmerParent.showShimmer()

        (activity as MainActivity).commonViewModel.isUnderMainLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.status_code == 200) {
//                    binding.swiperefresh.isRefreshing = false

                    if (it.data?.down == true && it.data.id == 1) {
                        findNavController().navigate(R.id.mainScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                    } else if (it.data?.down == true && it.data.id == 2) {
                        navController.popBackStack(R.id.homeScreenFrag, true)
                        findNavController().navigate(R.id.loginScreenFrag)

                        (activity as MainActivity).binding.bottomNavigationView.hide()
                        (activity as MainActivity).binding.underMainTimerParent.root.show()
                    } else if (it.data?.down == true) {
//
                        (activity as MainActivity).binding.underMainTimerParent.root.hide()
                        setUpNetworkCallbackFOrDueLoans()
                    } else {

                        setUpNetworkCallbackFOrDueLoans()
                    }
                }
            }
        }
        (activity as MainActivity).commonViewModel.getRespGetLoanOutStandingLiveData.observe(
            viewLifecycleOwner
        ) {

            if (it.status_code == 200) {
                it.data?.let {
                    for (i in it.get_loan_outstanding_response_data) {
                        i.current_date = it.current_date
                    }
                    setLoanOverView(it.get_loan_outstanding_response_data)
                    binding.swiperefresh.isRefreshing = false

                }
            } else if (it.status_code == 498) {
                (activity as MainActivity).commonViewModel.refreshToken(requireContext())
            }
            else{
                showLogD(it.message.toString())
            }


        }
        (activity as MainActivity).commonViewModel.refreshTokenLiveData.observe(
            viewLifecycleOwner
        ) {

            if (it.status_code == 200) {
                (activity as MainActivity).commonViewModel.getPendingInterestDues(

                    (activity as MainActivity).mLocation, requireContext()
                )
                (activity as MainActivity).commonViewModel.getLoanOutstanding(

                    (activity as MainActivity).mLocation, requireContext()
                )

            }



        }
        (activity as MainActivity).commonViewModel.getPendingInterestDuesLiveData.observe(
            viewLifecycleOwner
        ) {
            if (it.status_code == 200) {
                it?.let { gepPendingRespObj ->
                    binding.swiperefresh.isRefreshing = false
                    binding.shimmmerParent.hideShim()
                    (activity as MainActivity).commonViewModel.notZero =

                        gepPendingRespObj.data?.pending_interest_dues_response_data?.filter { getPendingInterestItem ->
                            getPendingInterestItem.payable_amount != 0.0
                        }
                    Log.i(
                        TAG,
                        "setUpComingDueLoans: ${(activity as MainActivity).commonViewModel.notZero}"
                    )
                    for (i in gepPendingRespObj?.data?.pending_interest_dues_response_data
                        ?: emptyList()) {
                        i.currentDate = gepPendingRespObj.data?.current_date.toString()

                    }

                    if ((activity as MainActivity).commonViewModel.notZero?.isNotEmpty() == true) {
                        upcomingLoanAdapter.submitList((activity as MainActivity).commonViewModel.notZero)
                        binding.rvUpcomingDueLoans.adapter = upcomingLoanAdapter
                        binding.noIntHaveParent.root.hide()
                        binding.rvUpcomingDueLoans.show()

                    } else {
                        binding.rvUpcomingDueLoans.hide()
                        binding.noIntHaveParent.root.show()
                    }

                }
            } else if (it.status_code == 498) {
                (activity as MainActivity).commonViewModel.refreshToken(requireContext())
            } else {
                showLogD(it.message.toString())
            }

        }

        if (view != null) {
            parentFragment?.viewLifecycleOwner?.let { lifecycleOwner ->
                (activity as MainActivity).commonViewModel.tokenExpiredResp.observe(lifecycleOwner) { resp ->
                    resp?.let {
                        showSnackBar(it.error_message)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        changeStatusBarWithReqdColor(requireActivity(), R.color.splash_screen_two)
        (activity as MainActivity).commonViewModel.getUnderMaintenanceStatus(requireContext())
        (activity as MainActivity).commonViewModel.getPendingInterestDues(

            (activity as MainActivity).mLocation, requireContext()
        )
        (activity as MainActivity).commonViewModel.getLoanOutstanding(

            (activity as MainActivity).mLocation, requireContext()
        )

        navController = findNavController()
        secureFiles = SecureFiles()
        (activity as MainActivity).locationProvider.startLocationUpdates()
        /*(activity as MainActivity).commonViewModel.isUnderMainLiveData.observe(this) {
            it?.let {
                if (it.statusCode == "200") {
                    if (it.data.down && it.data.id == 1) {
                        findNavController().navigate(R.id.mainScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                    } else if (it.data.down && it.data.id == 2) {
                        findNavController().navigate(R.id.loginScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                        (activity as MainActivity).binding.underMainTimerParent.root.show()
                    }
                    else if (!it.data.down){
//
                        (activity as MainActivity).binding.underMainTimerParent.root.hide()
                        setUpNetworkCallbackFOrDueLoans()
                    }
                        else {
//                        (activity as MainActivity).binding.bottomNavigationView.show()
//                        (activity as MainActivity).binding.underMainTimerParent.root.hide()
                        setUpNetworkCallbackFOrDueLoans()
                    }
                }
            }
        }*/

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) { // enabled by default
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    Log.d("TAG", "handleOnBackPressed: ..........pressed")
                    findNavController().navigate(R.id.appCloseDialog)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        setProfileUi()
        // Get the system service for connectivity
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        setUpBanner()
        /*if (view != null) {
            parentFragment?.viewLifecycleOwner?.let { lifecycleOwner ->
                (activity as MainActivity).commonViewModel.tokenExpiredResp.observe(lifecycleOwner) { resp ->
                    resp?.let {
                        showSnackBar(it.errorMessage)
                    }
                }
            }
        }*/

        binding.swiperefresh.setOnRefreshListener {

            if (InternetUtils.isNetworkAvailable(requireContext())) {
                Log.d(com.paulmerchants.gold.ui.TAG, "onAvailable: ...........internet")

                (activity as MainActivity).commonViewModel.getUnderMaintenanceStatus(requireContext())

                binding.swiperefresh.isRefreshing = false

            } else {
                lifecycleScope.launch {
                    noInternetDialog()
                }
                binding.swiperefresh.isRefreshing = false
            }
        }

    }


    private fun setUpNetworkCallbackFOrDueLoans() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Network connection is available, perform actions here
                // For example:
                // fetchData()
                Log.d(TAG, "onAvailable: ...........internet")
                lifecycleScope.launch {
                    binding.swiperefresh.isRefreshing = true

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

        // Unregister the network callback to avoid memory leaks
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        parentFragment?.viewLifecycleOwner?.let { lOwner ->
            (activity as MainActivity).commonViewModel.respPaymentUpdate.removeObservers(lOwner)
            (activity as MainActivity).commonViewModel.paymentData.removeObservers(lOwner)
            (activity as MainActivity).commonViewModel.tokenExpiredResp.removeObservers(lOwner)
            (activity as MainActivity).commonViewModel.isStartAnim.removeObservers(lOwner)
        }
        super.onDestroyView()
    }

    private fun setUpBanner() {
        moreToComeAdapter.submitList(bannerList)
        binding.moreToCome.viewPagerMoreToCome.adapter = moreToComeAdapter
        TabLayoutMediator(
            binding.moreToCome.tabLayout,
            binding.moreToCome.viewPagerMoreToCome
        ) { _, _ ->

        }.attach()
    }

    private fun onPayDueClicked(dueLoans: PendingInterestDuesResponseData) {
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


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ...............")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ...........")
    }


    private fun setLoanOverView(resp: List<RespGetLoanOutStandingItem>) {
        var totalAmount = 0.0
        binding.shimmerCardLoanOverView.hideShim()
        binding.apply {
            loanOverViewCardParent.youHaveTotalLoanTv.text =
                "You are having ${resp.size} active loans totalling interest due up to"
            for (i in resp) {
                i.payable_amount?.let {
                    totalAmount += i.payable_amount


                }
            }
            Log.d(TAG, "setLoanOverView: ......${totalAmount}")

            loanOverViewCardParent.totalLoanAmountTv.text =
                "INR ${getTwoDigitAfterDecimal(totalAmount).toFloat()}"
            loanOverViewCardParent.viewLoanBtn.setOnClickListener {
                findNavController().navigate(R.id.goldLoanScreenFrag)

            }
            loanOverViewCardParent.youHaveTotalLoanTv.show()
        }

        binding.loanOverViewCardParent.root.show()
    }


}
