package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.util.Log
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

    override fun onStart() {
        super.onStart()
        var bhmValue = true
        binding.apply {
            headerLoan.endIconIv.setImageResource(R.drawable.bbps_small)

            headerLoan.backIv.setOnClickListener {
                findNavController().navigateUp()
            }
            headerLoan.titlePageTv.setText(R.string.loan_emi)
            Log.d("TAG", "onResume: $bhmValue")




                arrowDowmBhmIv.setOnClickListener {
                    if (bhmValue) {
                        Log.d("TAGTrue", "onResume: $bhmValue")

                        arrowDowmBhmIv.setImageResource(R.drawable.cross_icon)
                        upiMethodParent.show()
                        bhmUpiParent.setBackgroundResource(R.drawable.rect_opem_loans)
                        bhmValue = false
                        walletMethodParent.hide()
                        creditCardParent.hide()
                        Log.d("TAGTrue", "onResume: $bhmValue")
                    } else if (!bhmValue){
                        Log.d("TAGFalse", "onResume: $bhmValue")
                        bhmValue = true

                            upiMethodParent.hide()
                            arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                            bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)
                            Log.d("TAGFalse....", "onResume: $bhmValue")

                        }


            }

            arrowDowmBhmIv.setOnClickListener {

                    arrowDowmBhmIv.setImageResource(R.drawable.cross_icon)
                    upiMethodParent.show()
                    bhmUpiParent.setBackgroundResource(R.drawable.rect_opem_loans)
                    arrowDowmBhmIv.setOnClickListener {


                            upiMethodParent.hide()
                            arrowDowmBhmIv.setImageResource(R.drawable.arrow_down_black)
                            bhmUpiParent.setBackgroundResource(R.drawable.card_sky_rect_6)



                    }

                    // wallet
                    walletMethodParent.hide()
                    //credit
                    creditCardParent.hide()

//                } else{



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
            addCardBtn.setOnClickListener {

                findNavController().navigate(R.id.addCardFrag)
            }

                proceedToPayBtn.setOnClickListener {
                    findNavController().navigate(R.id.paymentConfirmed)
                }
            }
        }


    }