package com.paulmerchants.gold.ui.others

import android.os.Build
import androidx.fragment.app.viewModels
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.adapter.LastStatemnetAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.common.Constants
import com.paulmerchants.gold.databinding.LoanStatementBinding
import com.paulmerchants.gold.model.RespGetLoanOutStandingItem
import com.paulmerchants.gold.model.RespLoanStatment
import com.paulmerchants.gold.security.SecureFiles
import com.paulmerchants.gold.utility.AppUtility
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
            secureFiles.encryptKey(loanOutStanding?.OpenDate.toString(), BuildConfig.SECRET_KEY_GEN)
                .toString(),
            secureFiles.encryptKey(fromDate, BuildConfig.SECRET_KEY_GEN).toString(),
        )

        commonViewModel.getRespLoanStatmentLiveData.observe(viewLifecycleOwner) {
            it?.let {
                lastLoanAdapter.submitList(it)
                binding.rvLastTrans.adapter = lastLoanAdapter
            }
        }
    }

    private fun setLoanStatement(resp: RespLoanStatment) {
        binding.apply {

        }
    }

    private fun setData(loanOutStanding: RespGetLoanOutStandingItem?) {
        binding.apply {
            loanNumTv.text = "Loan Number - ${loanOutStanding?.AcNo}"
            loanStateDateLargeTv.text =
                "Last Statement (${AppUtility.getDateWithYearOrdinals(loanOutStanding?.OpenDate.toString())} - ${AppUtility.getCurrentDateOnly()})"
        }
    }

}