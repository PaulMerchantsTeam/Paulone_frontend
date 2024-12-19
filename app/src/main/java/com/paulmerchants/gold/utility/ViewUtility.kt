package com.paulmerchants.gold.utility

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.MenuServicesAdapter

import com.paulmerchants.gold.databinding.AppCloseDialogBinding
import com.paulmerchants.gold.databinding.DialogPinRestSuccessBinding
import com.paulmerchants.gold.databinding.ErrorDialogBinding
import com.paulmerchants.gold.model.MenuServices

const val IS_SHOW_TXN = "IS_SHOW_TXN"
fun Activity.showCustomDialogFoPaymentStatus(
    header: String = "",
    message: String,
    isClick: (Boolean) -> Unit,
) {
    val binding = AppCloseDialogBinding.inflate(layoutInflater)
    val dialog = BottomSheetDialog(this)
    dialog.setCancelable(true)
    dialog.setContentView(binding.root)
    binding.quickPay.text = if (header == "") "Payment Status" else header
    binding.upcomDTv.apply {
        text = message
        show()
    }
    binding.cancelDgBtn.setOnClickListener {
        dialog.dismiss()
    }
    binding.loginParentBtn.setOnClickListener {
        isClick(true)
        dialog.dismiss()
//        finish()
    }
    dialog.show()
}
fun Activity.showCustomDialogFoPaymentError(
    header: String = "",
    message: String,
    isClick: (Boolean) -> Unit,
) {
    val binding = AppCloseDialogBinding.inflate(layoutInflater)
    val dialog = BottomSheetDialog(this)
    dialog.setCancelable(true)
    dialog.setContentView(binding.root)
//    binding.quickPay.text = if (header == "") "Payment Status" else header
    binding.upcomDTv.apply {
        text = message

        show()
    }
    binding.img.show()
    binding.quickPay.hide()
    binding.cancelDgBtn.setOnClickListener {
        dialog.dismiss()
    }
    binding.loginParentBtn.setOnClickListener {
        isClick(true)
        dialog.dismiss()
//        finish()
    }
    dialog.show()
}
fun Activity.showCustomDialogForError(
    header: String = "",
    message: String,
    isClick: (Boolean) -> Unit,
) {
    val binding = ErrorDialogBinding.inflate(layoutInflater)

    val dialog = AlertDialog.Builder(this)
        .setView(binding.root)
        .setCancelable(false) // Prevent dialog dismissal on back press
        .create()

    binding.dialogTitle.text = if (header == "") "Payment Status" else header

    binding.dialogMessage.apply {
        setTextColor(getColor(R.color.black))

        text = message
        show()
    }



    binding.buttonOk.setOnClickListener {
        isClick(true)
        dialog.dismiss()

    }
    dialog.show()
}

fun ImageView.startCustomAnimation(drawable: Int) {
    val animationDrawable: AnimationDrawable
    this.apply {
        setBackgroundResource(drawable)
        animationDrawable = background as AnimationDrawable
        animationDrawable.start()
    }
}







//fun Fragment.showCustomDialogOTPVerify(
//    mobile:String,
//    appSharedPref: AppSharedPref?,
//    type: Int = 0,
//    context: Context,
//    title: String = "",
//    viewModel: ViewModel,
//) {
//    val dialogBinding =
//        OtpFillLayoutDialogBinding.inflate(this.layoutInflater)
//
//    val customDialog =
//        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).create()
//    customDialog.apply {
//        setView(dialogBinding.root)
//        setCancelable(false)
//    }.show()
//
//    dialogBinding.verifyOtpBtn.setOnClickListener {
////        (viewModel as ProfileViewModel).verifyOtp(appSharedPref,mobile,)
//        customDialog.dismiss()
//        //verify Otp
//    }
//    dialogBinding.cancelDgBtn.setOnClickListener {
//        customDialog.dismiss()
//        //verify Otp
//    }
//}

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




fun RecyclerView.setServicesUi(
    context: Context,
    onMenuServiceClicked: (MenuServices) -> Unit,
    onMenuServiceClickedTwo: (MenuServices) -> Unit,
    onMenuServiceClickedTitle: (MenuServices) -> Unit,
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
        context.getString(R.string.reset_yr_pin)
    )
    val service2 = MenuServices(
        101,
        context.getString(R.string.transaction),
        context.getString(R.string.check_ye_spent),
        ""
    )
//    val service3 = MenuServices(
//        103,
//        context.getString(R.string.app_Setting),
//        context.getString(R.string.yr_pref),
//        ""
//    )
    val service4 = MenuServices(
        104,
        context.getString(R.string.anything_fr_us),
        context.getString(R.string.give_us_fdbck),
        ""
//        context.getString(R.string.raise_a_complaint)
    )
    val service5 = MenuServices(
        105,
        context.getString(R.string.about_us),
        "Privacy & Policy",
        "Terms & Condition"
    )
    val service6 = MenuServices(
        106,
        context.getString(R.string.log_out),
        "", ""
    )

    val listService = listOf(service1, service2, service4, service5, service6)
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

fun CircularProgressIndicator.startProgress() {
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




