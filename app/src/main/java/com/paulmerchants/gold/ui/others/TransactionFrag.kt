package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.AllTxnAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.AllTxnFragBinding
import com.paulmerchants.gold.model.newmodel.Transactions
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.ui.MapActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.viewmodels.TxnViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TransactionFrag : BaseFragment<AllTxnFragBinding>(AllTxnFragBinding::inflate) {

    private val txnViewModel: TxnViewModel by viewModels()
    private val allTxnAdapter = AllTxnAdapter(::showTxn)

    private fun showTxn(transactions: Transactions) {
        if (transactions.orderId != null) {
            val bundle = Bundle().apply {
                putString(Constants.PAYMENT_ID, transactions.orderId)
            }
            findNavController().navigate(R.id.paidReceiptFrag, bundle)
        } else {
            "Transaction not found".showSnackBar()
        }
    }

    override fun AllTxnFragBinding.initialize() {

    }

    override fun onStart() {
        super.onStart()
        binding.neddSuppMenuCard.setOnClickListener {
            AppUtility.dialer(requireContext(), "18001371333")
        }
        modifyHeaders()

        lifecycleScope.launch {
            try {
                txnViewModel.getTxnHistory()
                    .collectLatest { data ->
                        Log.d("TAG", "onCreate: ..dattttttttt........}")
                        setTransaction(data)
                    }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }


    private fun modifyHeaders() {
        binding.backIv.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setTransaction(data: PagingData<Transactions>) {
        allTxnAdapter.submitData(lifecycle, data)
        binding.rvTxnAll.adapter = allTxnAdapter
    }
}
