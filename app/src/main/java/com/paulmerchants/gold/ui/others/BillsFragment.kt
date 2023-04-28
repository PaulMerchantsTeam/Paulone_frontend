package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.enums.BbpsType
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.common.Constants.BBPS_TYPE
import com.paulmerchants.gold.databinding.LayoutLoanEmiBinding
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BillsFragment : BaseFragment<LayoutLoanEmiBinding>(LayoutLoanEmiBinding::inflate) {
    private var bbpsActionValue: Int? = null
    override fun LayoutLoanEmiBinding.initialize() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         bbpsActionValue = arguments?.getInt(BBPS_TYPE, 0)

        Log.d("TAGValue", "onCreate: $bbpsActionValue")


    }

    override fun onStart() {
        super.onStart()
    }
private fun showHeaderDetails(text:Int,image:Int){
    binding.headerLoan.endIconIv.show()
    binding.headerLoan.endIconIv.setImageResource(R.drawable.bbps_small)
    binding.headerLoan.titlePageTv.setText(text)
    binding.loanPersonIv.setImageResource(image)
    binding.headerLoan.backIv.setOnClickListener {
        findNavController().navigateUp()
    }
}
    private fun onNavigateProceedToPay(header:String) {
        val bundleBbpsHeader = Bundle().apply {
            putString(Constants.BBPS_HEADER,header)
        }
        findNavController().navigate(R.id.proceedToPay, bundleBbpsHeader)
    }
    override fun onResume() {
        super.onResume()
        Log.d("TAGValue", "onResume: $bbpsActionValue ")


        when (bbpsActionValue) {

            BbpsType.GoldLoan.type -> {}
            BbpsType.MunicipalTax.type -> {}
            BbpsType.Challan.type -> {}


            BbpsType.HomeLoan.type -> {
               showHeaderDetails(R.string.loan_emi,R.drawable.loan_person)
                binding.proceedBtn.setOnClickListener {
                   binding.loanDetailsEnterParent.hide()
                   binding.loanProccedDetailParent.show()
                    binding.proceedBtn.setText(R.string.proceed_to_pay)
                    binding.proceedBtn.setOnClickListener {
                        onNavigateProceedToPay(resources.getString(R.string.loan_emi))

                    }
                    binding.headerLoan.backIv.setOnClickListener {
                        binding.loanDetailsEnterParent.show()
                        binding.loanProccedDetailParent.hide()
                        binding.proceedBtn.setText(R.string.proceed)

                    }



                }
            }
            BbpsType.PersonalLoan.type -> {
                showHeaderDetails(R.string.loan_personal,R.drawable.loan_person)
                binding.proceedBtn.setOnClickListener {
                    binding.loanDetailsEnterParent.hide()
                    binding.loanProccedDetailParent.show()
                    binding.proceedBtn.setText(R.string.proceed_to_pay)

                    binding.proceedBtn.setOnClickListener {
                        onNavigateProceedToPay(resources.getString(R.string.loan_personal))
                    }
                    binding.headerLoan.backIv.setOnClickListener {
                        binding.loanDetailsEnterParent.show()
                        binding.loanProccedDetailParent.hide()
                        binding.proceedBtn.setText(R.string.proceed)

                    }



                }
            }
            BbpsType.CreditCard.type -> {
                showHeaderDetails(R.string.credit_card_bill,R.drawable.ic_credit_card)
                binding.bankTv.hide()
                binding.spinnerBankLenderParent.hide()
                binding.loanTypeTv.hide()
                binding.typeOfLOandParent.hide()
                binding.loanAcntNoTv.hide()
                binding.enterLoanEt.hide()
                binding.mobileNoTv.setText(R.string.enter_your_credit_card_number)
                binding.emiPaidTv.setText(R.string.amount_to_be_paid)

                binding.proceedBtn.setOnClickListener {
                    binding.loanDetailsEnterParent.hide()
                    binding.loanProccedDetailParent.show()
                    binding.proceedBtn.setText(R.string.proceed_to_pay)

                    binding.proceedBtn.setOnClickListener {
                        onNavigateProceedToPay(resources.getString(R.string.credit_card_bill))
                    }
                    binding.headerLoan.backIv.setOnClickListener {
                        binding.loanDetailsEnterParent.show()
                        binding.loanProccedDetailParent.hide()
                        binding.proceedBtn.setText(R.string.proceed)

                    }



                }



            }
            BbpsType.DthService.type -> {
                showHeaderDetails(R.string.dth_recharge,R.drawable.dth_bill)
                binding.apply {
                    bankTv.setText(R.string.select_operator)
                    loanAcntNoTv.setText(R.string.customer_id)
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    dthNoteTv.show()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.dth_recharge))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }


                }

            }

            BbpsType.OttWorld.type -> {
                showHeaderDetails(R.string.ott_subscription,R.drawable.ott_main)
                binding.apply {
                    bankTv.setText(R.string.select_ott_platform)
                    loanAcntNoTv.setText(R.string.enter_register_mail_number)
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.ott_subscription))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }


                }

            }
            BbpsType.Electricity.type -> {
                showHeaderDetails(R.string.electricity_bill,R.drawable.elec_layer1)
                binding.apply {
                    bankTv.setText(R.string.select_state)
                    loanTypeTv.setText(R.string.select_board)
                    loanAcntNoTv.setText(R.string.enter_your_account_number)
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    viewHor.show()
                    recentTransTv.show()
                    recentTransElecRv.show()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.electricity_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }


            }
            BbpsType.Broadband.type -> {
                showHeaderDetails(R.string.broadBand_bill,R.drawable.broadband_bill)
                binding.apply {
                    bankTv.setText(R.string.select_operator)
                    loanTypeTv.setText(R.string.your_number_with_std)
                    loanAcntNoTv.setText(R.string.amount_to_be_paid)
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.broadBand_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }


                }


            }
            BbpsType.Education.type -> {
                showHeaderDetails(R.string.education_fees_bill,R.drawable.education_layer1)
                binding.apply {
                    institutionAreaTv.show()
                    institutionAreaParent.show()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    bankTv.setText(R.string .select_institute_location)
                    loanTypeTv.setText(R.string.select_institute)
                    institutionAreaTv.setText(R.string.select_institute_area)
                    loanAcntNoTv.setText(R.string.enter_enrolment_number)
                    mobileNoTv.setText(R.string.enter_date_birth)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.education_fees_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }


            }
            BbpsType.GasCylinder.type -> {
                showHeaderDetails(R.string.book_a_cylinder,R.drawable.cylinder_gas)
                binding.apply {
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    bankTv.setText(R.string.select_your_gas_provider)
                    loanAcntNoTv.setText(R.string.your_mobile_number)
                    mobileNoTv.setText(R.string.amount_to_be_paid)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.book_a_cylinder))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }


            }
            BbpsType.Apartment.type -> {
                showHeaderDetails(R.string.apartment_bill,R.drawable.apartment_layer1)
                binding.apply {
                    institutionAreaTv.show()
                    institutionAreaParent.show()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    bankTv.setText(R.string .select_city)
                    loanTypeTv.setText(R.string.select_apartment)
                    institutionAreaTv.setText(R.string.utility_type)
                    loanAcntNoTv.setText(R.string.enter_your_mobile_number)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.apartment_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }

            }
            BbpsType.GasPipeLine.type -> {
                showHeaderDetails(R.string.gas_pipeline_bill,R.drawable.gas_pipe_l_1)
                binding.apply {
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    bankTv.setText(R.string.select_your_gas_provider)
                    loanAcntNoTv.setText(R.string.your_mobile_number)
                    mobileNoTv.setText(R.string.customer_id)
                    emiPaidTv.setText(R.string.amount_to_paid)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.gas_pipeline_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }

            }
            BbpsType.HomeRent.type -> {
                showHeaderDetails(R.string.home_rent_bill,R.drawable.home_rent)
                binding.apply {
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    rentAmntTv.show()
                    rentAmntEt.show()
                    bankTv.setText(R.string.select_landlord_bank)
                    loanAcntNoTv.setText(R.string.bank_account_number)
                    mobileNoTv.setText(R.string.ifsc_code)
                    emiPaidTv.setText(R.string.account_holder_name)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.home_rent_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }
            }
            BbpsType.WaterBill.type -> {
                showHeaderDetails(R.string.water_bill,R.drawable.tap)
                binding.apply {
                    bankTv.setText(R.string.select_board)
                    loanAcntNoTv.setText(R.string.your_account_number)
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.water_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }

            }
            BbpsType.LandLineBill.type -> {
                showHeaderDetails(R.string.landline_bill,R.drawable.landline_layer1)
                binding.apply {
                    bankTv.setText(R.string.select_operator)
                    loanAcntNoTv.setText(R.string.your_number_with_std)
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.landline_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }
            }
            BbpsType.CableTvBill.type -> {
                showHeaderDetails(R.string.cable_tv_bill,R.drawable.cable_tv)
                binding.apply {
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()

                    emiPaidTv.hide()
                    emiPaidEt.hide()

                    bankTv.setText(R.string.select_operator)
                    loanAcntNoTv.setText(R.string.your_account_number)
                    mobileNoTv.setText(R.string.amount_to_paid)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.cable_tv_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }


                }

            }
            BbpsType.Insurance.type -> {
                showHeaderDetails(R.string.insurance_premium,R.drawable.insurance_layer1)
                binding.apply {
                    bankTv.setText(R.string.select_insurer)
                    loanAcntNoTv.setText(R.string.your_policy_number)
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.insurance_premium))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }
            }

            BbpsType.Mobile.type -> {
                showHeaderDetails(R.string.mobile_recharge_bill,R.drawable.mobile_layer_2 )
                binding.apply {
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    viewHorPost.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    mobileRechargeTv.show()
                    enterMobTv.show()
                    enterMobEt.show()
                    bankTv.setText(R.string.select_operator)
                    loanAcntNoTv.setText(R.string.enter_state)
                    mobileNoTv.setText(R.string.amount_to_be_paid)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.mobile_recharge_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                    prepaidTv.setOnClickListener{
                        viewHorPre.show()
                        viewHorPost.hide()
                        loanTypeTv.hide()
                        typeOfLOandParent.hide()
                        viewHorPost.hide()
                        mobileNoTv.show()
                        mobileNoEt.show()
                        emiPaidTv.hide()
                        emiPaidEt.hide()
                        mobileRechargeTv.show()
                        enterMobTv.show()
                        enterMobEt.show()
                        bankTv.setText(R.string.select_operator)
                        loanAcntNoTv.setText(R.string.enter_state)
                        mobileNoTv.setText(R.string.amount_to_be_paid)
                    }
                    postpaidTv.setOnClickListener{
                        loanTypeTv.hide()
                        viewHorPre.hide()
                        enterMobTv.hide()
                        enterMobEt.hide()
                        viewHorPost.show()
                        typeOfLOandParent.hide()
                        emiPaidTv.hide()
                        emiPaidEt.hide()
                        mobileRechargeTv.show()
                        mobileNoTv.hide()
                        mobileNoEt.hide()
                        bankTv.setText(R.string.select_operator)
                        loanAcntNoTv.setText(R.string.customer_number)
                    }



                }



            }
            BbpsType.MobileRecharge.type -> {
                showHeaderDetails(R.string.mobile_recharge_bill,R.drawable.anim_mobile_icon)
                binding.apply {
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    viewHorPost.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    mobileRechargeTv.show()
                    enterMobTv.show()
                    enterMobEt.show()
                    bankTv.setText(R.string.select_operator)
                    loanAcntNoTv.setText(R.string.enter_state)
                    mobileNoTv.setText(R.string.amount_to_be_paid)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.mobile_recharge_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                    prepaidTv.setOnClickListener{
                        viewHorPre.show()
                        viewHorPost.hide()
                        loanTypeTv.hide()
                        typeOfLOandParent.hide()
                        viewHorPost.hide()
                        emiPaidTv.hide()
                        mobileNoTv.show()
                        mobileNoEt.show()
                        emiPaidEt.hide()
                        mobileRechargeTv.show()
                        enterMobTv.show()
                        enterMobEt.show()
                        bankTv.setText(R.string.select_operator)
                        loanAcntNoTv.setText(R.string.enter_state)
                        mobileNoTv.setText(R.string.amount_to_be_paid)
                    }
                    postpaidTv.setOnClickListener{
                        loanTypeTv.hide()
                        viewHorPre.hide()
                        viewHorPost.show()
                        typeOfLOandParent.hide()
                        enterMobTv.hide()
                        enterMobEt.hide()
                        emiPaidTv.hide()
                        emiPaidEt.hide()
                        mobileRechargeTv.show()
                        mobileNoTv.hide()
                        mobileNoEt.hide()
                        bankTv.setText(R.string.select_operator)
                        loanAcntNoTv.setText(R.string.customer_number)
                    }



                }

            }
            BbpsType.MobilePostpaid.type -> {
                showHeaderDetails(R.string.postpaid_bill,R.drawable.mob_post_layer1)
                binding.apply {
                    loanTypeTv.hide()
                    viewHorPre.hide()
                    typeOfLOandParent.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    mobileRechargeTv.show()
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    bankTv.setText(R.string.select_operator)
                    loanAcntNoTv.setText(R.string.customer_number)
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.postpaid_bill))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                    prepaidTv.setOnClickListener{
                        viewHorPre.show()
                        viewHorPost.hide()
                        loanTypeTv.hide()
                        mobileNoTv.show()
                        mobileNoEt.show()
                        typeOfLOandParent.hide()
                        viewHorPost.hide()
                        emiPaidTv.hide()
                        emiPaidEt.hide()
                        mobileRechargeTv.show()
                        enterMobTv.show()
                        enterMobEt.show()
                        bankTv.setText(R.string.select_operator)
                        loanAcntNoTv.setText(R.string.enter_state)
                        mobileNoTv.setText(R.string.amount_to_be_paid)
                    }
                    postpaidTv.setOnClickListener{
                        loanTypeTv.hide()
                        viewHorPre.hide()
                        viewHorPost.show()
                        typeOfLOandParent.hide()
                        emiPaidTv.hide()
                        emiPaidEt.hide()
                        enterMobTv.hide()
                        enterMobEt.hide()
                        mobileRechargeTv.show()
                        mobileNoTv.hide()
                        mobileNoEt.hide()
                        bankTv.setText(R.string.select_operator)
                        loanAcntNoTv.setText(R.string.customer_number)
                    }
                }
            }
            BbpsType.FastTag.type -> {
                showHeaderDetails(R.string.fastag_recharge,R.drawable.fastag_layer1)
                binding.apply {
                    bankTv.setText(R.string.select_fast_tag_bank)
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    loanAcntNoTv.hide()
                    enterLoanEt.hide()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.fastag_recharge))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }
            }

            BbpsType.MetroCard.type -> {
                showHeaderDetails(R.string.metro_card_recharge,R.drawable.metro_card)
                binding.apply {
                  bankTv.setText(R.string.select_metro)
                    loanTypeTv.hide()
                    typeOfLOandParent.hide()
                    mobileNoTv.hide()
                    mobileNoEt.hide()
                    emiPaidTv.hide()
                    emiPaidEt.hide()
                    loanAcntNoTv.hide()
                    enterLoanEt.hide()
                    binding.proceedBtn.setOnClickListener {
                        binding.loanDetailsEnterParent.hide()
                        binding.loanProccedDetailParent.show()
                        binding.proceedBtn.setText(R.string.proceed_to_pay)

                        binding.proceedBtn.setOnClickListener {
                            onNavigateProceedToPay(resources.getString(R.string.metro_card_recharge))
                        }
                        binding.headerLoan.backIv.setOnClickListener {
                            binding.loanDetailsEnterParent.show()
                            binding.loanProccedDetailParent.hide()
                            binding.proceedBtn.setText(R.string.proceed)

                        }



                    }

                }

            }

        }
    }
}
