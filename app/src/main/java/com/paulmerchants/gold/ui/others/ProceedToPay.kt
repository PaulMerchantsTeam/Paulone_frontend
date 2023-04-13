package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.ViewUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.utility.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProceedToPay :
    BaseFragment<LayoutLoanEmiProceedToPayBinding>(LayoutLoanEmiProceedToPayBinding::inflate) {
    private var bbpsHeaderVale: String? = null

    override fun LayoutLoanEmiProceedToPayBinding.initialize() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bbpsHeaderVale = arguments?.getString(Constants.BBPS_HEADER, "")
        Log.d("TAGHeader", "onCreate: $bbpsHeaderVale")

    }

    override fun onStart() {
        super.onStart()
        var  proceedBtn = "1"
        var bhmValue = true
        var walletValue = true
        var creditValue = true
        binding.apply {
            headerLoan.endIconIv.show()
            headerLoan.endIconIv.setImageResource(R.drawable.bbps_small)
            headerLoan.backIv.setOnClickListener {
                findNavController().navigateUp()
            }
           val headerBundle  =  Bundle().apply {
               putString(Constants.BBPS_HEADER,bbpsHeaderVale)
           }
            headerLoan.titlePageTv.setText(bbpsHeaderVale.toString())
            Log.d("TAG", "onResume: $bhmValue")

            prePaidcardSelectParent.setOnClickListener {
                selectPrecard.setBackgroundColor(resources.getColor(R.color.splash_screen_one))
                selectUpi.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
                proceedBtn = "1"

            }
            selectUpiIdParent.setOnClickListener {
                selectUpi.setBackgroundColor(resources.getColor(R.color.splash_screen_one))
                selectPrecard.setBackgroundColor(resources.getColor(R.color.splash_screen_two))
                proceedBtn = "2"
            }
            arrowDowmBhmIv.setOnClickListener {
                if (bhmValue) {


                    bhmUpiParent.setBackgroundResource(R.drawable.rect_opem_loans)
                    arrowDowmBhmIv.setImageResource(R.drawable.cross_icon)
                    upiMethodParent.show()
                    verifyUpiBtn.setOnClickListener {
                        showCustomDialogOTPVerify(requireContext(),"OTP send to the number ending with *4555")
                        addNewUpiTv.hide()
                        selectUpiIdParent.show()
                        upiCardTv.show()
                        upiNumTv.show()
                        upiMethodParent.hide()
                        arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                        bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                        }



//                    upiMethodParent.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            requireContext(), R.anim.slide_down
//                        )
//                    )
                    //wallet
                    walletMethodParent.hide()
                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
                    //credit
                    creditCardParent.hide()
                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
                    bhmValue = false
                } else {
                    upiMethodParent.hide()
//                    upiMethodParent.startAnimation(
//                        AnimationUtils.loadAnimation(
//                            requireContext(), R.anim.slide_up
//                        )
//                    )
                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    bhmValue = true
                }
            }
            arrowDownWalletIv.setOnClickListener {
                if (walletValue) {
                    arrowDownWalletIv.setImageResource(R.drawable.cross_icon)
                    walletMethodParent.show()
                    walletParent.setBackgroundResource(R.drawable.rect_opem_loans)

                    // upi
                    upiMethodParent.hide()
                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                    //creditCa0rd
                    creditCardParent.hide()
                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
                    walletValue = false

                } else {
                    walletMethodParent.hide()
                    walletMethodParent.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(), R.anim.slide_up
                        )
                    )
                    arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
                    walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    walletValue = true
                }
            }


            arrowDownCreditIv.setOnClickListener {
                if (creditValue){
                arrowDownCreditIv.setImageResource(R.drawable.cross_icon)
                creditDebitParent.setBackgroundResource(R.drawable.rect_opem_loans)
                creditCardParent.show()
                //wallet
                walletMethodParent.hide()
                walletParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                arrowDownWalletIv.setImageResource(R.drawable.arrow_down_black)
                //upi
                upiMethodParent.hide()
                bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                    addCardBtn.setOnClickListener {
                        findNavController().navigate(R.id.addCardFrag,headerBundle)
                    }
                    creditValue = false
                }else{
                    creditCardParent.hide()
                    creditCardParent.startAnimation(
                        AnimationUtils.loadAnimation(
                            requireContext(), R.anim.slide_up
                        )
                    )
                    arrowDownCreditIv.setImageResource(R.drawable.arrow_down_black)
                    creditDebitParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                    creditValue = true
                }

            }


            proceedToPayBtn.setOnClickListener {
                if(proceedBtn == "1"){
                    paymentModeTv.show()
                    paymentModeCardTv.show()
                    preferredModeParent.hide()
                    otherModeParent.hide()
                    otpFill.otpParent.show()
                    proceedBtn = "3"
                }else if(proceedBtn == "2"){
                    proceedToPayParent.hide()
                    preferredModeParent.hide()
                    otherModeParent.hide()
                    timeCountTv.show()
                    openApplicationTv.show()
                    lifecycleScope.launch {
                     delay(1000)
                     findNavController().navigate(R.id.paymentConfirmed,headerBundle)

                 }

                }
                else if (proceedBtn == "3"){
                    findNavController().navigate(R.id.paymentConfirmed,headerBundle)
                }

            }
        }
    }


}