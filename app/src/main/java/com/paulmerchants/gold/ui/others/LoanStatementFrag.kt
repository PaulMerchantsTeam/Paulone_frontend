package com.paulmerchants.gold.ui.others

import android.os.Build
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.LastStatemnetAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LoanStatementBinding
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.RespLoanStatment
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.getScreenBitmap
import com.paulmerchants.gold.utility.AppUtility.saveAsPdf
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.viewmodels.CommonViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoanStatementFrag : BaseFragment<LoanStatementBinding>(LoanStatementBinding::inflate) {

    private val commonViewModel: CommonViewModel by viewModels()
    private lateinit var secureFiles: SecureFiles
    private val lastLoanAdapter = LastStatemnetAdapter()
    override fun LoanStatementBinding.initialize() {
        secureFiles = SecureFiles()
    }

    override fun onStart() {
        super.onStart()
        binding.headerScrn.apply {
            backIv.setOnClickListener { findNavController().navigateUp() }
            titlePageTv.text = getString(R.string.loan_Statment)
        }
        val loanOutStanding =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arguments?.getParcelable(
                Constants.LOAN_OVERVIEW, RespGetLoanOutStandingItem::class.java
            ) else arguments?.getParcelable<RespGetLoanOutStandingItem>(Constants.LOAN_OVERVIEW) as? RespGetLoanOutStandingItem
        setData(loanOutStanding)
        val fromDate = AppUtility.getCurrentDate()
        val toDate = ""

        commonViewModel.getLoanClosureReceipt(loanOutStanding?.AcNo.toString())
        commonViewModel.getRespClosureReceiptLiveData.observe(viewLifecycleOwner) {
            it?.let {

            }
        }
        //opening date to till now.
        commonViewModel.getLoanStatement(
            loanOutStanding?.AcNo.toString(),
            loanOutStanding?.OpenDate.toString(),
            fromDate,
        )

        commonViewModel.getRespLoanStatmentLiveData.observe(viewLifecycleOwner) {
            it?.let {
                lastLoanAdapter.submitList(it)
                binding.rvLastTrans.adapter = lastLoanAdapter
            }
        }
        binding.gotToHomeBtn.setOnClickListener {
            findNavController().popBackStack(R.id.goldLoanScreenFrag, true)
            findNavController().popBackStack(R.id.loanStatementFrag, true)
            findNavController().navigate(R.id.homeScreenFrag)
        }
        binding.donwloadPdfBtn.setOnClickListener {
            val screenBitmap = getScreenBitmap(binding.constraintLayout7, R.color.open_loans)
            val pdfWidth = 450f
            val pdfHeight = 842f
            saveAsPdf(
                requireContext(),
                pdfWidth,
                pdfHeight,
                screenBitmap,
                R.color.open_loans
            )
        }
    }

    private fun setLoanStatement(resp: RespLoanStatment) {
        binding.apply {

        }
    }

    private fun setData(loanOutStanding: RespGetLoanOutStandingItem?) {
        binding.apply {
            loanNumTv.text = "Loan Number - ${loanOutStanding?.AcNo}"
            if (loanOutStanding?.OpenDate !=null && loanOutStanding?.OpenDate!= ""){
                loanStateDateLargeTv.text =
                    "Last Statement (${AppUtility.getDateWithYearOrdinals(loanOutStanding?.OpenDate.toString())} - ${AppUtility.getCurrentDateOnly()})"
            }

        }
    }

}