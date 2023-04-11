package com.paulmerchants.gold.ui.others

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LayoutLoanEmiProceedToPayBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProceedToPay :
    BaseFragment<LayoutLoanEmiProceedToPayBinding>(LayoutLoanEmiProceedToPayBinding::inflate) {
    override fun LayoutLoanEmiProceedToPayBinding.initialize() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            headerLoan.endIconIv.setImageResource(R.drawable.bbps_small)

            headerLoan.backIv.setOnClickListener {
                findNavController().navigateUp()
            }
            headerLoan.titlePageTv.setText(R.string.loan_emi)
            arrowDowmBhmIv.setOnClickListener {
                arrowDowmBhmIv.setImageResource(R.drawable.cross_icon)
                upiMethodParent.show()
                bhmUpiParent.setBackgroundResource(R.drawable.rect_opem_loans)
//                arrowDowmBhmIv.setOnClickListener {
//                    bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
//                    arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
//                }

                // wallet
                walletMethodParent.hide()
                //credit
                creditCardParent.hide()

            }
            arrowDownWalletIv.setOnClickListener {
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


            }
            arrowDownCreditIv.setOnClickListener {
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
            }

            proceedToPayBtn.setOnClickListener {
                findNavController().navigate(R.id.paymentConfirmed)
            }
        }
    }


}