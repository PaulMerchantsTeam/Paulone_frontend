package com.paulmerchants.gold.ui.bottom

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.PrePaidCardAdapter
import com.paulmerchants.gold.adapter.UpcomingLoanAdapter
import com.paulmerchants.gold.adapter.UpcomingLoanNewuserAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants.DUE_LOAN_DATA
import com.paulmerchants.gold.databinding.DummyHomeScreenFragmentBinding
import com.paulmerchants.gold.enums.BbpsType
import com.paulmerchants.gold.model.ActionItem
import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.OurServices
import com.paulmerchants.gold.model.PrepaidCardModel
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.setUiOnHomeSweetHomeBills
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.utility.startCustomAnimation
import com.paulmerchants.gold.viewmodels.CommonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date


@AndroidEntryPoint
class HomeScreenFrag :
    BaseFragment<DummyHomeScreenFragmentBinding>(DummyHomeScreenFragmentBinding::inflate) {

    private val upcomingLoanAdapter = UpcomingLoanAdapter(::onPayDueClicked)
    private val upcomingNewUserAdapter = UpcomingLoanNewuserAdapter()
    private val prePaidCardAdapter = PrePaidCardAdapter(::onClicked)
    private val commonViewModel: CommonViewModel by viewModels()
    private lateinit var secureFiles: SecureFiles
    lateinit var navController: NavController

    //    private val homeSweetBillsAdapter = HomeSweetBillsAdapter()
    private val TAG = "HomeScreenFrag"

    override fun DummyHomeScreenFragmentBinding.initialize() {
        navController = findNavController()
        secureFiles = SecureFiles()
//        setUpComingOurServices()


    }

    override fun onStart() {
        super.onStart()

//        commonViewModel.getLogin(secureFiles)
        setProfileUi()
        setUpComingDueLoans()

        startAnimationOnIcon()
        setUiOnHomeSweetHomeBills()
        handleRechargeAndBillUi()
        setPrepaidCardUi()
        setAddCardView()
    }

    private fun showHideLoadinf() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.shimmmerParent.startShimmer()
            delay(2000)
            binding.shimmmerParent.hideShimmer()
            binding.shimmmerParent.hide()
            binding.rvUpcomingDueLoans.show()
        }

    }

    private fun onPayDueClicked(dueLoans: GetPendingInrstDueRespItem) {
        val bundle = Bundle().apply {
            putParcelable(DUE_LOAN_DATA, dueLoans)
        }
        findNavController().navigate(
            R.id.quickPayDialog, bundle
        )
    }

    private fun onClicked(prepaidCardModel: PrepaidCardModel) {
        findNavController().navigate(R.id.pcFrag)
    }

    private fun setPrepaidCardUi() {
        val prepaidCard1 = PrepaidCardModel(1, 4, "Prithvi Kumar")
        val prepaidCard2 = PrepaidCardModel(2, 4, "Swati")
        val prepaidCard3 = PrepaidCardModel(3, 4, "Arjun S Narayanan")
        prePaidCardAdapter.submitList(listOf(prepaidCard1, prepaidCard2, prepaidCard3))
        binding.prepaidCardRv.adapter = prePaidCardAdapter
    }

    private fun setProfileUi() {
        val userFirstName =
            AppUtility.getFirstName(AppSharedPref.getStringValue(Constants.CUSTOMER_NAME))
        binding.searchProfileParent.userName.text = "Hey ${userFirstName ?: "User"}"
        binding.searchProfileParent.firtLetterUser.text = "${userFirstName?.first() ?: "U"}"
        commonViewModel.isStartAnim.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    animateHintEditText()
                }
            }
        }

        binding.searchProfileParent.apply {
            searchView.setOnClickListener {
                commonViewModel.isStartAnim.postValue(false)
                binding.searchProfileParent.searchView.clearAnimation()
            }

            searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    commonViewModel.isStartAnim.postValue(false)
                    binding.searchProfileParent.searchView.clearAnimation()

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    commonViewModel.isStartAnim.postValue(false)
                    binding.searchProfileParent.searchView.clearAnimation()
                    p0?.let { char ->
                        Log.d("TAG", "onTextChanged: text = $char")
                        if (char.isNotEmpty()) {
                            commonViewModel.isStartAnim.postValue(false)
                            binding.searchProfileParent.searchView.clearAnimation()

//                            searchView.setCompoundDrawablesWithIntrinsicBounds(
//                                0,
//                                0,
//                                0,
//                                0
//                            )
                        } else {
                            commonViewModel.isStartAnim.postValue(false)
                            binding.searchProfileParent.searchView.clearAnimation()

                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                    Log.d("TAG", "afterTextChanged: $p0")
                }
            })
        }
        profileHandle()
    }

    private fun profileHandle() {
        binding.searchProfileParent.profileIv.setOnClickListener {
            findNavController().navigate(
                R.id.profileFrag
            )
        }
        binding.searchProfileParent.notImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFrag_to_notificationsScreenFrag)
        }
    }

    private fun handleRechargeAndBillUi() {
        /**
         * initially hide ---home_sweet_home, financial_Security,transit_window ,back_top
         * |-----flow----|
         * on ... more click
         * hide own parent icon  and change behaviour too
         * show---home_sweet_home, financial_Security,transit_window ,back_top
         *
         */
        binding.allPaymnetActionParent.moreParent.setOnClickListener {
            binding.allPaymnetActionParent.billsNRechargerParent.hide()
            binding.allPaymnetActionParent.forMoreParentAllAction.show()
            binding.allPaymnetActionParent.backToNormalAction.show()

        }
        binding.allPaymnetActionParent.backToNormalAction.setOnClickListener {
            binding.allPaymnetActionParent.apply {
                billsNRechargerParent.show()
                forMoreParentAllAction.hide()
                backToNormalAction.hide()

            }


        }

        binding.allPaymnetActionParent.homeLoanParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.HomeLoan.type), findNavController())
        }

        binding.allPaymnetActionParent.personalLoanParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.PersonalLoan.type), findNavController())

        }
        binding.allPaymnetActionParent.creditCardParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.CreditCard.type), findNavController())

        }
        binding.allPaymnetActionParent.dthServiceActionParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.DthService.type), findNavController())

        }
        binding.allPaymnetActionParent.dthServiceActionParent2.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.DthService.type), findNavController())

        }
        binding.allPaymnetActionParent.electricityParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Electricity.type), findNavController())

        }
        binding.allPaymnetActionParent.boradBandParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Broadband.type), findNavController())

        }
        binding.allPaymnetActionParent.MobileParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Mobile.type), findNavController())

        }
        binding.allPaymnetActionParent.mobileParent2.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MobileRecharge.type), findNavController())

        }
        binding.allPaymnetActionParent.mobPostPaidParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MobilePostpaid.type), findNavController())

        }
        binding.allPaymnetActionParent.ottParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.OttWorld.type), findNavController())

        }
        binding.allPaymnetActionParent.insuranceParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.Insurance.type), findNavController())

        }
        binding.allPaymnetActionParent.muncipalTaxParent.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
//            AppUtility.onBillClicked(ActionItem(BbpsType.MunicipalTax.type), findNavController())

        }
        binding.allPaymnetActionParent.fastTagParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.FastTag.type), findNavController())

        }
        binding.allPaymnetActionParent.challanTraffitParent.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
//            AppUtility.onBillClicked(ActionItem(BbpsType.Challan.type), findNavController())

        }
        binding.allPaymnetActionParent.metroCardParent.setOnClickListener {
            AppUtility.onBillClicked(ActionItem(BbpsType.MetroCard.type), findNavController())

        }

    }

    override fun onResume() {
        super.onResume()
        binding.searchProfileParent.searchView.show()
        commonViewModel.isStartAnim.postValue(true)

        binding.searchProfileParent.searchView.startAnimation(AnimationUtils.loadAnimation(
            requireContext(), R.anim.slide_down_to_mid
        ).apply {
            this.start()
        })
    }

    override fun onPause() {
        super.onPause()
        commonViewModel.isStartAnim.postValue(false)
        binding.searchProfileParent.searchView.clearAnimation()
        binding.searchProfileParent.searchView.startAnimation(AnimationUtils.loadAnimation(
            requireContext(), R.anim.slide_down_to_mid
        ).apply {
            this.cancel()
        })


    }


    private fun animateHintEditText() {
        val strList = listOf(
            getString(R.string.search_fr_bills),
            getString(R.string.search_fr_credit),
            getString(R.string.search_fr_upcoming_dues),
            getString(R.string.search_for_loans),
        )
        lifecycleScope.launchWhenResumed {
            delay(1000)
            binding.searchProfileParent.searchView.hint = strList[0]
            binding.searchProfileParent.searchView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_down_to_mid
                )
            )
            delay(1000)
            binding.searchProfileParent.searchView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_mid_to_up
                )
            )
            delay(1000)
            binding.searchProfileParent.searchView.hint = strList[1]
            binding.searchProfileParent.searchView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_down_to_mid
                )
            )
            delay(1000)
            binding.searchProfileParent.searchView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_mid_to_up
                )
            )
            delay(1000)
            binding.searchProfileParent.searchView.hint = strList[2]
            binding.searchProfileParent.searchView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_down_to_mid
                )
            )
            delay(1000)
            binding.searchProfileParent.searchView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_mid_to_up
                )
            )


            delay(1000)
            binding.searchProfileParent.searchView.hint = strList[3]
            binding.searchProfileParent.searchView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_down_to_mid
                )
            )
            delay(1000)
            binding.searchProfileParent.searchView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_mid_to_up
                )
            )

            delay(1000)
            commonViewModel.isStartAnim.postValue(true)
        }

    }


    private fun startAnimationOnIcon() {
        binding.allPaymnetActionParent.apply {
            goldIv.startCustomAnimation(R.drawable.anim_gold_icon)
            dthIV.startCustomAnimation(R.drawable.anim_dth_service_icon)
            elecIv.startCustomAnimation(R.drawable.anim_elec_icon)
            boradBandIv.startCustomAnimation(R.drawable.anim_broadband_icon)
            mobileIv.startCustomAnimation(R.drawable.anim_mobile_icon)
            mob2Iv.startCustomAnimation(R.drawable.anim_mobile_icon)
            mobPostIv.startCustomAnimation(R.drawable.anim_post_paid_icon)
            ottIv.startCustomAnimation(R.drawable.anim_ott_icon)
            dthIV2.startCustomAnimation(R.drawable.anim_dth_service_icon)
            isuranceIv.startCustomAnimation(R.drawable.anim_insurance_icon)
            mucipalIv.startCustomAnimation(R.drawable.anim_munciple_tax)
            fastIv.startCustomAnimation(R.drawable.anim_fastag_icon)
            challanIv.startCustomAnimation(R.drawable.anim_challan_icon)
            metroCardIv.startCustomAnimation(R.drawable.anim_metro_card)

        }
    }

    private fun setUpComingOurServices() {
        val ourServices1 = OurServices(
            R.drawable.gold_loan_hand, getString(R.string.gold_n_loans), R.color.yellow_main
        )
        val ourServices2 = OurServices(
            R.drawable.hand_prepaid_card, getString(R.string.prepaid_n_cards), R.color.green_main
        )
        val ourServices3 = OurServices(
            R.drawable.hand_invoice, getString(R.string.bills_n_payment), R.color.sky_blue_main
        )
        val ourServices4 = OurServices(
            R.drawable.hand_digital_gold, getString(R.string.digital_n_gold), R.color.orange_main
        )
        val serviceList = listOf(ourServices1, ourServices2, ourServices3, ourServices4)
        upcomingNewUserAdapter.submitList(serviceList)
        binding.rvUpcomingDueLoans.adapter = upcomingNewUserAdapter

    }

    private fun setUpComingDueLoans() {
        showHideLoadinf()
        val currentDate = AppUtility.getCurrentDate()
        Log.d(TAG, "setUpComingDueLoans: ..currentDate..$currentDate")
        Log.d(
            TAG, "setUpComingDueLoans: ..currentDate..${
                secureFiles.encryptKey(
                    currentDate, BuildConfig.SECRET_KEY_GEN
                )
            }"
        )
        val encDate = secureFiles.encryptKey(
            currentDate, BuildConfig.SECRET_KEY_GEN
        )
        commonViewModel.getPendingInterestDues(
            encDate.toString()
        )
        commonViewModel.getPendingInterestDuesLiveData.observe(viewLifecycleOwner) {
            it?.let {
                commonViewModel.notZero = it.filter { it.InterestDue != 0.0000 }
                upcomingLoanAdapter.submitList(commonViewModel.notZero)
                binding.rvUpcomingDueLoans.adapter = upcomingLoanAdapter
                setLoanOverView()
            }
        }
//        val dueLoans1 = DueLoans(1, 4, 6000)
//        val dueLoans2 = DueLoans(2, 4, 6000)
//        val dueLoans3 = DueLoans(3, 4, 6000)
//        val dueLoans4 = DueLoans(4, 4, 6000)
//        val list = listOf(dueLoans1, dueLoans2, dueLoans3, dueLoans4)
//        upcomingLoanAdapter.submitList(list)
//        binding.rvUpcomingDueLoans.adapter = upcomingLoanAdapter
    }

    private fun setUiOnHomeSweetHomeBills() {
        binding.allPaymnetActionParent.homeSweetHomBillsRv.setUiOnHomeSweetHomeBills(
            requireContext(), ::onBillClicked
        )
    }

    private fun setLoanOverView() {
        var totalAmount = 0.0
        binding.apply {
//            loanOverViewCardParent.cardParent.setBackgroundColor(R.drawable.new_user_card_grad)

//            loanOverViewCardParent.viewLoanBtn.text = getString(R.string.apply_now)
//            loanOverViewCardParent.viewLoanBtn.setOnClickListener {
//                findNavController().navigate(R.id.applyLoanForNewUser)
//            }            android:text="You are having 4 active loans totalling upto"
            loanOverViewCardParent.youHaveTotalLoanTv.text =
                "You are having ${commonViewModel.notZero.size} active loans totalling upto"
            for (i in commonViewModel.notZero) {
                totalAmount += i.InterestDue
            }
            Log.d(TAG, "setLoanOverView: ......${totalAmount}")
            loanOverViewCardParent.totalLoanAmountTv.text = "INR $totalAmount"
            loanOverViewCardParent.viewLoanBtn.setOnClickListener {
                findNavController().navigate(R.id.goldLoanScreenFrag)

            }
            loanOverViewCardParent.renewLoansTv.show()
            loanOverViewCardParent.youHaveTotalLoanTv.show()
        }
        binding.shimmerCardLoanOverView.stopShimmer()
        binding.loanOverViewCardParent.root.show()
    }

    private fun setAddCardView() {
        binding.apply {
            addCardBtn.setOnClickListener {
                findNavController().navigate(R.id.addUpiCard)

            }
        }
    }

    private fun onBillClicked(actionItem: ActionItem) {
        AppUtility.onBillClicked(actionItem, findNavController())
    }


}
