package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.utility.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProceedToPay :
    BaseFragment<LayoutLoanEmiProceedToPayBinding>(LayoutLoanEmiProceedToPayBinding::inflate) {
    override fun LayoutLoanEmiProceedToPayBinding.initialize() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        var bhmValue = true
        var walletValue = true
        var creditValue = true
        binding.apply {
            headerLoan.endIconIv.setImageResource(R.drawable.bbps_small)
            headerLoan.backIv.setOnClickListener {
                findNavController().navigateUp()
            }
            headerLoan.titlePageTv.setText(R.string.loan_emi)
            Log.d("TAG", "onResume: $bhmValue")
            arrowDowmBhmIv.setOnClickListener {
                if (bhmValue) {
                    bhmUpiParent.setBackgroundResource(R.drawable.rect_opem_loans)
                    arrowDowmBhmIv.setImageResource(R.drawable.cross_icon)
                    upiMethodParent.show()
                    verifyUpiBtn.setOnClickListener {

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
                        findNavController().navigate(R.id.addCardFrag)
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
                paymentModeTv.show()
                paymentModeCardTv.show()
                preferredModeParent.hide()
                otherModeParent.hide()
                otpFill.otpParent.show()
            }
        }
    }


}