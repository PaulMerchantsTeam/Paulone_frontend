package com.paulmerchants.gold.ui

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.itextpdf.xmp.XMPDateTimeFactory.getCurrentDateTime
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.MainNavGraphDirections
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseActivity
import com.paulmerchants.gold.databinding.ActivityMainBinding
import com.paulmerchants.gold.databinding.HeaderLayoutBinding
import com.paulmerchants.gold.location.LocationProvider
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
import java.lang.ref.WeakReference
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

const val TAG = "MAIN_ACTIVITY"

@AndroidEntryPoint
class MainActivity : BaseActivity<CommonViewModel, ActivityMainBinding>() {
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE
    lateinit var navOption: NavOptions
    private lateinit var navOptionLeft: NavOptions
    private lateinit var navOptionTop: NavOptions
    private lateinit var navController: NavController
    private lateinit var secureFiles: SecureFiles
    val commonViewModel: CommonViewModel by viewModels()
    var amount: Double? = null   //will assign dynamically...
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var connectivityManager: ConnectivityManager
    lateinit var locationProvider: LocationProvider
    var mLocation: Location? = null


    companion object {
        lateinit var context: WeakReference<Context>

    }

    public override val mViewModel: CommonViewModel by viewModels()
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val resultCode = result.resultCode
            when (resultCode) {
                123 -> {
                    checkForAppUpdate()
                }

                -1 -> {
                    //Downloaded status
                }

                Activity.RESULT_CANCELED -> {
                    checkForAppUpdate()
                }

                else -> {
                    checkForAppUpdate()
                }
            }
        }
    private fun isAutomaticDateTimeEnabled(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
    }

    private fun showDateTimeSettingsDialog() {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.setting_change_dialog, null)

        // Create the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevent dialog dismissal on back press
            .create()

        // Get references to the buttons
        val openSettingsButton: Button = dialogView.findViewById(R.id.button_open_settings)
        val exitAppButton: Button = dialogView.findViewById(R.id.button_exit_app)

        // Set onClickListener for the "Open Settings" button
        openSettingsButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
            dialog.dismiss() // Dismiss the dialog
            finish() // Close the app after redirecting
        }

        // Set onClickListener for the "Exit App" button
        exitAppButton.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
            finish() // Close the app
        }

        // Show the dialog
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        context = WeakReference(this)
        AppSharedPref.start(this)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        AppUtility.changeStatusBarWithReqdColor(this, R.color.splash_screen_two)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        checkForAppUpdate()

        appUpdateManager.registerListener(installUpdateListener)

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
        updateLocation()
        if (!isAutomaticDateTimeEnabled(this)) {
            showDateTimeSettingsDialog()
        } else {
            // Proceed with the app



            binding.bottomNavigationView.itemIconTintList = null
            binding.bottomNavigationView.setupWithNavController(navController)


            navController.addOnDestinationChangedListener { _, destination, _ ->


                if (
                    destination.id == R.id.mainScreenFrag ||
                    destination.id == R.id.homeScreenFrag ||
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
                        true
                    }

                    R.id.goldLoanScreenFrag -> {
                        navController.navigate(MainNavGraphDirections.actionToGoldLoan())
                        true
                    }


                    R.id.locateUsFrag -> {

                        startActivity(
                            Intent(this, MapActivity::class.java),
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                        )
                        true
                    }


                    else -> {
                        false
                    }
                }
            }
            binding.underMainParent.closeBtn.setOnClickListener {
                finish()
            }



            setUpNetworkCallbackFOrDueLoans()
            commonViewModel.isUnderMainLiveData.observe(this) {
                it?.let {
                    if (it.statusCode == "200" && it.data.down && it.data.id == 1) {
                        showUnderMainTainPage()
                    } else if (it.statusCode == "200" && it.data.down && it.data.id == 2) {
                        it.data.endTime?.let { endTime ->
                            showUnderMainTainTimerPage(
                                endTime

                            )
                        }
                        navController.navigate(R.id.loginScreenFrag)
                        navController.popBackStack()


                    } else if (!it.data.down) {
                        binding.batteryMainNavGraph.show()
                        binding.underMainTimerParent.root.hide()
                        binding.underMainParent.root.hide()

                    }

                }
            }
            if (!BuildConfig.DEBUG) {
                if (AppUtility.isUsbDebuggingEnabled(this)) {
                    "Please turn off the debug mode".showSnackBar()
                    return
                } else {
                    Log.i("TAG", "NO_DEBUG_MODE_ENABLED")
                }
            }


        }

    }
    private fun setUpNetworkCallbackFOrDueLoans() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Network connection is available, perform actions here
                // For example:
//                if (!BuildConfig.DEBUG) {
                Log.d(TAG, "onAvailable: ...........internet")
                lifecycleScope.launch {
                    commonViewModel.getUnderMaintenanceStatus()
                }
//                }

            }

            override fun onLost(network: Network) {
                // Network connection is lost, handle accordingly
                // For example:

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

    fun checkForDownFromRemoteConfig() {
        commonViewModel.checkForDownFromRemoteConfig()
    }


    fun showUnderMainTainPage() {
        binding.bottomNavigationView.hide()
        binding.batteryMainNavGraph.hide()
        binding.underMainParent.root.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showUnderMainTainTimerPage(endTime: String ) {
        binding.bottomNavigationView.hide()
        binding.batteryMainNavGraph.hide()
        binding.underMainTimerParent.root.show()

            startDailyCountdown(endTime )

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun startDailyCountdown(endTime: String = "2024-09-20 14:40:30" ) {

        val endTimeFormat = AppUtility.getHourMinuteSecond(endTime)


        val targetTime = LocalTime.of(
            endTimeFormat?.first!!, endTimeFormat.second,
            endTimeFormat.third
        )


        // Get the current time (India Standard Time)
        val currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))


        // Get the time today when the countdown should end (today's 3:00:00 PM)
        var targetISTTime = currentTime.with(targetTime)


        // If the current time is already past 3:00 PM, set the target to tomorrow at 3:00 PM
        if (currentTime.isAfter(targetISTTime)) {
            targetISTTime = targetISTTime.plusDays(1)
        }

        // Calculate the difference between now and the target time in milliseconds
       
        val millisUntilTarget = ChronoUnit.MILLIS.between(currentTime, targetISTTime)

        // Start the countdown timer from now until the target time
        object : CountDownTimer(millisUntilTarget, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                // Calculate hours, minutes, and seconds remaining
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                // Update your TextView with the countdown time
                if (hours.toInt() == 0) {
                    binding.underMainTimerParent.timerTextView.text =
                        String.format("%02d:%02d", minutes, seconds)

                } else {
                    binding.underMainTimerParent.timerTextViewBg.text =
                        "88:88:88"
                    binding.underMainTimerParent.timerTextView.text =
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)


                }

            }

            override fun onFinish() {
                // Reset or refresh your UI, or restart the countdown for the next day if needed
                binding.underMainTimerParent.timerTextView.text = "00:00"
                setUpNetworkCallbackFOrDueLoans()

//                navController.clearBackStack(R.id.splashFragment)

            }
        }.start()
//        }
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


    private val installUpdateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(applicationContext, "Download Successful.", Toast.LENGTH_SHORT).show()

            appUpdateManager.completeUpdate()

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()


        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        locationProvider.stopLocationUpdates()
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailabe = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailabe && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }


    private fun updateLocation() {
        locationProvider = LocationProvider(this, object : LocationProvider.LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.e(
                    TAG,
                    "onLocationChanged: .....${location.latitude}-----${location.longitude}",
                )
                mLocation = location
            }

        }, this)
        locationProvider.startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()

        appUpdateManager.unregisterListener(installUpdateListener)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationProvider.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                locationProvider.startLocationUpdates()
            } else {
                Log.e(TAG, "onRequestPermissionsResult: ............no permission....")
//                locationProvider.startLocationUpdates()
                // Permission denied, handle accordingly
                // Display a message informing the user about the necessity of location permission
                // Encourage them to grant the permission or provide alternative functionality
            }
        }
    }
}


