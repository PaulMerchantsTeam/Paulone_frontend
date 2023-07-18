package com.paulmerchants.gold.utility

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Resources.Theme
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.*
import com.paulmerchants.gold.databinding.DialogPinRestSuccessBinding
import com.paulmerchants.gold.databinding.OtpFillLayoutBinding
import com.paulmerchants.gold.databinding.OtpFillLayoutDialogBinding
import com.paulmerchants.gold.enums.BbpsType
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.MenuServices
import com.paulmerchants.gold.model.Notifications


fun ImageView.startCustomAnimation(drawable: Int) {
    val animationDrawable: AnimationDrawable
    this.apply {
        setBackgroundResource(drawable)
        animationDrawable = background as AnimationDrawable
        animationDrawable.start()
    }
}

fun RecyclerView.setUiOnHomeSweetHomeBills(context: Context, onBillClicked: (ActionItem) -> Unit) {
    val homeSweetBillsAdapter = HomeSweetBillsAdapter(onBillClicked)
    val actionItem1 = ActionItem(
        BbpsType.Electricity.type,
        R.drawable.anim_elec_icon,
        context.getString(R.string.electricity)
    )
    val actionItem2 =
        ActionItem(
            BbpsType.Broadband.type,
            R.drawable.anim_broadband_icon,
            context.getString(R.string.broadband)
        )
    val actionItem3 =
        ActionItem(
            BbpsType.Education.type,

            R.drawable.anim_education,
            context.getString(R.string.education)
        )
    val actionItem4 =
        ActionItem(
            BbpsType.GasCylinder.type,
            R.drawable.anim_cylinder_gas,
            context.getString(R.string.gas_cylinder)
        )
    val actionItem5 =
        ActionItem(
            BbpsType.Apartment.type,
            R.drawable.anim_apartment,
            context.getString(R.string.apartment)
        )
    val actionItem6 =
        ActionItem(
            BbpsType.GasPipeLine.type,
            R.drawable.anim_gas_pipe_line,
            context.getString(R.string.gas_pipline)
        )
    val actionItem7 = ActionItem(
        BbpsType.HomeRent.type,
        R.drawable.anim_home_rent_icon,
        context.getString(R.string.homerent)
    )
    val actionItem8 =
        ActionItem(
            BbpsType.WaterBill.type,
            R.drawable.anim_water_icon,
            context.getString(R.string.water)
        )
    val actionItem9 =
        ActionItem(
            BbpsType.LandLineBill.type,
            R.drawable.anim_landline_icon,
            context.getString(R.string.landline)
        )
    val actionItem10 = ActionItem(
        BbpsType.CableTvBill.type,
        R.drawable.anim_cable_tv_icon,
        context.getString(R.string.cabletv)
    )
    val list = listOf(
        actionItem1,
        actionItem2,
        actionItem3,
        actionItem4,
        actionItem5,
        actionItem6,
        actionItem7,
        actionItem8,
        actionItem9,
        actionItem10
    )
    val staggeredGridLayoutManager = StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL)
    homeSweetBillsAdapter.submitList(list)
    this.apply {
        layoutManager = staggeredGridLayoutManager
        adapter = homeSweetBillsAdapter
    }
}


fun RecyclerView.setNotificationDummy() {
    val notifAdapter = NotificationAdapter()
    val notif1 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 1)
    val notif2 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 2)
    val notif3 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 3)
    val notif4 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 4)
    val notif5 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 5)
    val notif6 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 6)
    val notif7 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 7)
    val notif8 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 8)
    val notif9 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 9)
    val notif10 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 10)
    val notif11 =
        Notifications("Welcome User,\nBefore we get started please complete your profile", 11)
    val list = listOf(
        notif1,
        notif2,
        notif3,
        notif4,
        notif5,
        notif6,
        notif7,
        notif8,
        notif9,
        notif10,
        notif11
    )
    notifAdapter.submitList(list)
    this.apply {
        adapter = notifAdapter
    }
}


fun Fragment.showCustomDialogOTPVerify(context: Context, title: String = "") {
    val dialogBinding =
        OtpFillLayoutDialogBinding.inflate(this.layoutInflater)

    val customDialog =
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).create()
    customDialog.apply {
        setView(dialogBinding.root)
        setCancelable(false)
    }.show()
    dialogBinding.verifyOtpBtn.setOnClickListener {
        customDialog.dismiss()
        //verify Otp
    }
    dialogBinding.cancelDgBtn.setOnClickListener {
        customDialog.dismiss()
        //verify Otp
    }
}

/*fun RecyclerView.setUiOnLastTransaction() {
    val lastStatemnetAdapter = LastStatemnetAdapter()
    val actionItem1 = ActionItem(
        BbpsType.Electricity.type,
        0, "oct 27"
    )
    val actionItem2 = ActionItem(
        BbpsType.Electricity.type,
        0, "oct 27"
    )

    val actionItem3 = ActionItem(
        BbpsType.Electricity.type,
        0, "oct 27"
    )

    val actionItem4 = ActionItem(
        BbpsType.Electricity.type,
        0, "oct 27"
    )

    val actionItem5 = ActionItem(
        BbpsType.Electricity.type,
        0, "oct 27"
    )

    val actionItem6 = ActionItem(
        6,
        0, "oct 27"
    )


    val actionItem7 = ActionItem(
        7,
        0, "oct 27"
    )

    val actionItem8 = ActionItem(
        8,
        0, "oct 27"
    )


    val list = listOf(
        actionItem1,
        actionItem2,
        actionItem3,
        actionItem4,
        actionItem5,
        actionItem6,
        actionItem7,
        actionItem8,
    )
    lastStatemnetAdapter.submitList(list)
    this.apply {
        adapter = lastStatemnetAdapter
    }

}*/

fun RecyclerView.setGoldLoanOverView(type: Int) {
    fun clicked(actionItem: ActionItem) {
        findNavController().navigate(R.id.pmlGoldLoan)
    }

    fun payNowClicked(actionItem: ActionItem) {
        findNavController().navigate(R.id.paymentModesFrag)
    }

    val lastStatemnetAdapter = GoldLoanOverViewAdapter(::clicked, ::payNowClicked)
    val actionItem1 = ActionItem(
        type,
        0, "231212121"
    )
    val actionItem2 = ActionItem(
        type,
        0, "231212121"
    )

    val actionItem3 = ActionItem(
        type,
        0, "231212121"
    )

    val actionItem4 = ActionItem(
        type,
        0, "231212121"
    )

    val actionItem5 = ActionItem(
        type,
        0, "231212121"
    )

    val actionItem6 = ActionItem(
        6,
        0, "231212121"
    )


    val actionItem7 = ActionItem(
        7,
        0, "231212121"
    )

    val actionItem8 = ActionItem(
        8,
        0, "231212121"
    )


    val list = listOf(
        actionItem1,
        actionItem2,
        actionItem3,
        actionItem4,
        actionItem5,
        actionItem6,
        actionItem7,
        actionItem8,
    )
    lastStatemnetAdapter.submitList(list)
    this.apply {
        adapter = lastStatemnetAdapter
    }

}


fun RecyclerView.setServicesUi(
    context: Context,
    onMenuServiceClicked: (MenuServices) -> Unit,
    onMenuServiceClickedTwo: (MenuServices) -> Unit,
    onMenuServiceClickedTitle: (MenuServices) -> Unit
) {
    val menuServiceAdapter = MenuServicesAdapter(
        onMenuServiceClicked,
        onMenuServiceClickedTwo,
        onMenuServiceClickedTitle
    )
    val service1 = MenuServices(
        100,
        context.getString(R.string.privac_settings),
        context.getString(R.string.chag_yr_pin),
        ""
    )
    val service2 = MenuServices(
        101,
        context.getString(R.string.transaction),
        context.getString(R.string.check_ye_spent),
        ""
    )
    val service3 = MenuServices(
        103,
        context.getString(R.string.app_Setting),
        context.getString(R.string.yr_pref),
        ""
    )
    val service4 = MenuServices(
        104,
        context.getString(R.string.anything_fr_us),
        context.getString(R.string.give_us_fdbck),
        context.getString(R.string.raise_a_complaint)
    )
    val service5 = MenuServices(
        105,
        context.getString(R.string.log_out),
        "",
        ""
    )

    val listService = listOf(service1, service2, service3, service4, service5)
    menuServiceAdapter.submitList(listService)
    this.adapter = menuServiceAdapter
}

fun Fragment.showResetPinSuccessDialog() {
    val sBinding = DialogPinRestSuccessBinding.inflate(layoutInflater)
    val builder = MaterialAlertDialogBuilder(
        requireContext(), R.style.MaterialAlertDialogStyle
    ).setView(sBinding.root).setCancelable(true).create()
    builder.apply {
        window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setCancelable(false)
        setContentView(sBinding.root)
    }
    sBinding.cancelBtn.setOnClickListener {
        builder.dismiss()
    }
    builder.show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun ViewGroup.showVg() {
    visibility = View.VISIBLE
}

fun View.disableButton(context: Context) {
    this.isEnabled = false
    this.setBackgroundColor(
        ContextCompat.getColor(context, R.color.color_prim_one_40)
    )

}

fun View.enableButton(context: Context) {
    isEnabled = true
    setBackgroundColor(
        ContextCompat.getColor(context, R.color.splash_screen_one)
    )

}

fun CircularProgressIndicator.startProgress(context: Context) {
    isIndeterminate = true
    progress = 50
}

fun CircularProgressIndicator.endProgress(context: Context) {
    isIndeterminate = false
    progress = 100
}


fun View.hideView() {
    this.animate()
        .translationY(this.height.toFloat())
        .alpha(0.0f)
        .setDuration(300)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = View.GONE
            }
        })
}

fun View.shoViewWithAnim() {
    visibility = View.VISIBLE
    alpha = 0.0f
    this.animate()
        .translationY(0F)
        .alpha(1.0f)
        .setListener(null)
}

fun View.showViewWithAnimDur300() {
    visibility = View.VISIBLE
    alpha = 0.0f
    this.animate()
        .translationY(0F)
        .alpha(1.0f)
        .setDuration(300)
        .setListener(null)
}

fun TextView.setTColor(str: String, context: Context, colorId: Int) {
    text = str
    setTextColor(ContextCompat.getColor(context, colorId))
}


fun toggle(view: View, show: Boolean) {
    val transition: Transition = Fade()
    transition.duration = 600
    transition.addTarget(view)
    TransitionManager.beginDelayedTransition(view as ViewGroup, transition)
    view.setVisibility(if (show) View.VISIBLE else View.GONE)
}

fun ViewGroup.hideViewGrp() {
    this.animate()
        .translationY(this.height.toFloat())
        .alpha(0.0f)
        .setDuration(300)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = View.GONE
            }
        })
}


fun ObjectAnimator.disableViewDuringAnimation(view: View) {
    addListener(object : AnimatorListenerAdapter() {

    })
}




