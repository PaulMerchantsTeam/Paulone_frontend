package com.paulmerchants.gold.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.paulmerchants.gold.MainNavGraphDirections
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseActivity
import com.paulmerchants.gold.databinding.ActivityMainBinding
import com.paulmerchants.gold.databinding.HeaderLayoutBinding
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.noInternetDialog
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.CommonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference
import java.security.MessageDigest


@AndroidEntryPoint
class MainActivity : BaseActivity<CommonViewModel, ActivityMainBinding>() {
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE
    lateinit var navOption: NavOptions
    lateinit var navOptionLeft: NavOptions
    lateinit var navOptionTop: NavOptions
    lateinit var navController: NavController
    lateinit var secureFiles: SecureFiles
    val commonViewModel: CommonViewModel by viewModels()
    var amount: Double? = null   //will assign dynamically...
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    companion object {
        lateinit var context: WeakReference<Context>
    }

    public override val mViewModel: CommonViewModel by viewModels()
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        context = WeakReference(this)
        AppSharedPref.start(this)
        appUpdateManager = AppUpdateManagerFactory.create(this)
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        AppUtility.changeStatusBarWithReqdColor(this, R.color.splash_screen_two)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        checkForAppUpdate()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.registerListener(installUpdateListener)
        }
        secureFiles = SecureFiles()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.battery_main_nav_graph) as NavHostFragment
        navController = navHostFragment.navController


        navOption = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        navOptionLeft = NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.slide_in_left).setPopEnterAnim(R.anim.slide_in_right)
            .setPopExitAnim(R.anim.slide_out_right).build()

        navOptionTop = NavOptions.Builder().setEnterAnim(R.anim.slide_in_bottom)
            .setExitAnim(R.anim.slide_out_bottom).setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right).build()
        binding.bottomNavigationView.itemIconTintList = null
        binding.bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("TAG", "onCreate:${destination.displayName} ")


            if (
                destination.id == R.id.mainScreenFrag ||
                destination.id == R.id.homeScreenFrag ||
//                destination.id == R.id.goldLoanScreenFrag ||
//                destination.id == R.id.billsAndMoreScreenFrag ||
//                destination.id == R.id.locateUsFrag ||
                destination.id == R.id.menuScreenFrag
            ) {
                binding.bottomNavigationView.show()
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }
        binding.bottomNavigationView.setOnItemSelectedListener {
            val homeDestinationId = R.id.homeScreenFrag
            val currentBackStackEntry = navController.currentBackStackEntry
            val backStackIds = currentBackStackEntry?.destination?.id
            when (it.itemId) {
                R.id.homeScreenFrag -> {
                    if (backStackIds != null && backStackIds == homeDestinationId) {
                        // If the home destination is already on the back stack, pop the back stack
                        navController.popBackStack(homeDestinationId, false)
                    } else {
                        // If the home destination is not on the back stack, navigate to it
                        navController.navigate(homeDestinationId)
                    }
//                    navController.apply {
//                        navigate(R.id.homeScreenFrag)
//                    }
                    true


                }

                R.id.goldLoanScreenFrag -> {
                    navController.navigate(MainNavGraphDirections.actionToGoldLoan())
                    true
                }

//                R.id.billsAndMoreScreenFrag -> {
//                    navController.navigate(
//                        MainNavGraphDirections.actionToBillsAndMore(),
//                        navOptionTop
//                    )
//                    true
//                }

                R.id.locateUsFrag -> {
//                    navController.navigate(MainNavGraphDirections.actionToLocateUs())
                    startActivity(
                        Intent(this, MapActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    )
                    true
                }

//                R.id.menuScreenFrag -> {
//                    navController.navigate(
//                        MainNavGraphDirections.actionToMenuScreen(),
//                        navOptionTop
//                    )
//                    true
//                }

                else -> {
                    false
                }
            }
        }
//        Log.e(
//            TAG,
//            "onCreate: ........${
//                intent?.getBooleanExtra(IS_SHOW_TXN, false)
//            }",
//        )

//        if (intent?.getBooleanExtra(IS_SHOW_TXN, false) == true) {
//            navController.navigate(R.id.transactionFrag)
//        }
        binding.underMainParent.closeBtn.setOnClickListener {
            finish()
        }
        setUpNetworkCallbackFOrDueLoans()
        commonViewModel.isUnderMainLiveData.observe(this) {
            it?.let {
                if (it.statusCode == "200") {
                    if (it.data.down) {
                        showUnderMainTainPage()
                    } else {

                    }
                }
            }
        }

        if (AppUtility.isUsbDebuggingEnabled(this)) {
            "Please turn off the debug mode".showSnackBar()
            return
        } else {
            Log.i("TAG", "NO_DEBUG_MODE_ENABLED")
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
                    commonViewModel.getUnderMaintenanceStatus()
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


    private fun showUnderMainTainPage() {
        binding.bottomNavigationView.hide()
        binding.batteryMainNavGraph.hide()
        binding.underMainParent.root.show()
    }

    fun changeHeader(hBinding: HeaderLayoutBinding, title: String, endIcon: Int) {
        hBinding.apply {
            titlePageTv.text = title
            if (endIcon != 0) {
                endIconIv.setImageResource(endIcon)
                endIconIv.show()
            } else {
                endIconIv.hide()
            }
        }

    }


//    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
//        Log.i(
//            TAG,
//            "onPaymentSuccess: ......$p0........${p1?.orderId}.....${p1?.paymentId}------${p1?.signature}"
//        )
//        commonViewModel.paymentData.postValue(StatusPayment(true, p1))
//        updatePaymentStatusToServer(StatusPayment("captured", p1))
//    }

//    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
//        Log.i(
//            TAG,
//            "onPaymentError: -----------$p0.---$p1...${p2?.orderId}.....${p2?.paymentId}------${p2?.signature}"
//        )
//      commonViewModel.paymentData.postValue(StatusPayment(false, p2))
//        updatePaymentStatusToServer(StatusPayment("not_captured", p2))
//    }

//    private fun updatePaymentStatusToServer(statusData: StatusPayment) {
//        Log.d(TAG, "updatePaymentStatusToServer: $amount....$statusData")
//        if (amount != null) {
//            amount?.let {
//                commonViewModel.updatePaymentStatus(
//                    appSharedPref = appSharedPref,
//                    status = statusData.status,
//                    razorpay_payment_id = statusData.paymentData?.paymentId.toString(),
//                    razorpay_order_id = statusData.paymentData?.orderId.toString(),
//                    razorpay_signature = statusData.paymentData?.signature.toString(),
//                    custId = appSharedPref?.getStringValue(Constants.CUSTOMER_ID).toString(),
//                    amount = amount,
//                    contactCount = 0, description = "desc_payment"
//                )
//            }
//        } else {
//            "Amount: Some thing went wrong".showSnackBar()
//        }
//    }

    private val installUpdateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(applicationContext, "Download Successful.", Toast.LENGTH_SHORT).show()
//            lifecycleScope.launch {
//                delay(5.seconds)
            appUpdateManager.completeUpdate()
//            }
        }
    }

    override fun onResume() {
        super.onResume()
//        if (updateType == AppUpdateType.IMMEDIATE) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    AppUpdateType.IMMEDIATE,
                    this,
                    123
                )
            }
//            }
        }
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailabe = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
//                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailabe && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateType,
                    this,
                    123
                )
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: ....$requestCode...$resultCode")
        when {
            requestCode == 123 && resultCode == 0 -> {
                checkForAppUpdate()
            }

            requestCode == 123 && resultCode == -1 -> {
                //Downloaded status
            }

            requestCode == Activity.RESULT_CANCELED -> {
                checkForAppUpdate()
            }

            else -> {
                checkForAppUpdate()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.unregisterListener(installUpdateListener)
        }
    }
    fun verifyChecksum(file: File, expectedChecksum: String): Boolean {
        val actualChecksum = generateChecksum(file)
        return actualChecksum == expectedChecksum
    }

    private fun generateChecksum(file: File): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val inputStream = file.inputStream()
        val byteArray = ByteArray(8192)
        var bytesRead = inputStream.read(byteArray)
        while (bytesRead != -1) {
            messageDigest.update(byteArray, 0, bytesRead)
            bytesRead = inputStream.read(byteArray)
        }
        inputStream.close()
        val digestBytes = messageDigest.digest()
        return digestBytes.joinToString("") { "%02x".format(it) }
    }
}


const val TAG = "MAIN_ACTIVITY"