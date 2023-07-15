package com.paulmerchants.gold.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.ViewUtils
import com.paulmerchants.gold.MainNavGraphDirections
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseActivity
import com.paulmerchants.gold.databinding.ActivityMainBinding
import com.paulmerchants.gold.databinding.HeaderLayoutBinding
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.AuthViewModel
import com.paulmerchants.gold.viewmodels.CommonViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference


@AndroidEntryPoint
class MainActivity : BaseActivity<CommonViewModel, ActivityMainBinding>() {

    lateinit var navOption: NavOptions
    lateinit var navOptionLeft: NavOptions
    lateinit var navOptionTop: NavOptions
    lateinit var navController: NavController
    lateinit var secureFiles: SecureFiles

    companion object {
        lateinit var context: WeakReference<Context>
    }

    public override val mViewModel: CommonViewModel by viewModels()
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        context = WeakReference(this)
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        AppUtility.changeStatusBarWithReqdColor(this, R.color.splash_screen_two)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        if (AppUtility.isUsbDebuggingEnabled(this)) {
            Toast.makeText(this, "DEBUG_MODE_ENABLED", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "NO_DEBUG_MODE_ENABLED", Toast.LENGTH_SHORT).show()
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
                destination.id == R.id.billsAndMoreScreenFrag ||
//                destination.id == R.id.locateUsFrag ||
                destination.id == R.id.menuScreenFrag
            ) {
                binding.bottomNavigationView.show()
            } else {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeScreenFrag -> {
                    navController.navigate(R.id.homeScreenFrag, null, navOptionLeft)
                    true
                }

                R.id.goldLoanScreenFrag -> {
                    navController.navigate(MainNavGraphDirections.actionToGoldLoan())
                    true
                }

                R.id.billsAndMoreScreenFrag -> {
                    navController.navigate(
                        MainNavGraphDirections.actionToBillsAndMore(),
                        navOptionTop
                    )
                    true
                }

                R.id.locateUsFrag -> {
//                    navController.navigate(MainNavGraphDirections.actionToLocateUs())
                    startActivity(
                        Intent(this, MapActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    )
                    true
                }

                R.id.menuScreenFrag -> {
                    navController.navigate(
                        MainNavGraphDirections.actionToMenuScreen(),
                        navOptionTop
                    )
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    fun showQuickPayDialog() {

    }

    override fun onResume() {
        super.onResume()
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


    fun hideStatusBar() {
        this.window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun showStatusBar() {
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


}