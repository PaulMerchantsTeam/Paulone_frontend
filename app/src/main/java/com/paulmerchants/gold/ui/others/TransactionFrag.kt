package com.paulmerchants.gold.ui.others

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.AllTxnAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.AllTxnFragBinding
import com.paulmerchants.gold.model.newmodel.Transactions
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.viewmodels.TxnViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TransactionFrag : BaseFragment<AllTxnFragBinding>(AllTxnFragBinding::inflate) {

    private val txnViewModel: TxnViewModel by viewModels()
    private val allTxnAdapter = AllTxnAdapter()

    override fun AllTxnFragBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        modifyHeaders()
        txnViewModel.getTxnHistory((activity as MainActivity).appSharedPref)
        txnViewModel.txnHistoryData.observe(viewLifecycleOwner) {
            it?.let {
                setTransaction(it.data)
            }
        }
    }

    private fun modifyHeaders() {
        binding.headerTransDetail.titlePageTv.text = "All Transaction"
        binding.headerTransDetail.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setTransaction(data: List<Transactions>) {
        allTxnAdapter.submitList(data)
        binding.rvTxnAll.adapter = allTxnAdapter
    }
}
