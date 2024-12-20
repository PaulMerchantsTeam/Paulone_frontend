package com.paulmerchants.gold.ui.others

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.google.android.material.chip.Chip
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.AllTxnAdapter
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.AllTxnFragBinding
import com.paulmerchants.gold.model.responsemodels.Transactions
import com.paulmerchants.gold.ui.MainActivity
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.utility.AppUtility.changeStatusBarWithReqdColor
import com.paulmerchants.gold.utility.AppUtility.showSnackBar
import com.paulmerchants.gold.utility.Constants
import com.paulmerchants.gold.utility.hide
import com.paulmerchants.gold.utility.show
import com.paulmerchants.gold.viewmodels.TxnViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TransactionFrag : BaseFragment<AllTxnFragBinding>(AllTxnFragBinding::inflate) {

    private val txnViewModel: TxnViewModel by viewModels()
    private val allTxnAdapter = AllTxnAdapter(::showTxn)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).commonViewModel.isUnderMainLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.status_code ==200) {
                    if (it.data?.down == true && it.data.id == 1) {
                        findNavController().navigate(R.id.mainScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                    } else if (it.data?.down == true && it.data.id == 2) {
                        findNavController().popBackStack(R.id.homeScreenFrag,true)
                        findNavController().navigate(R.id.loginScreenFrag)
                        (activity as MainActivity).binding.bottomNavigationView.hide()
                        (activity as MainActivity).binding.underMainTimerParent.root.show()
                    } else if (it.data?.down == false) {
//
                        (activity as MainActivity).binding.underMainTimerParent.root.hide()

                    } else {

                    }
                }
            }
        }
    }
    private fun showTxn(transactions: Transactions) {
        if (transactions.order_id != null) {
            val bundle = Bundle().apply {
                putString(Constants.ORDER_ID, transactions.order_id)
                putString(Constants.PAYMENT_ID, transactions.payment_id)
            }
            findNavController().navigate(R.id.paidReceiptFrag, bundle)
        } else {
            "Transaction not found".showSnackBar()
        }
    }

    override fun AllTxnFragBinding.initialize() {
        changeStatusBarWithReqdColor(requireActivity(), R.color.green_new_bg)
    }

    override fun onStart() {
        super.onStart()

        binding.chip2.performClick()
        getTxnHistory(11)
        (activity as MainActivity).commonViewModel.getUnderMaintenanceStatus(requireContext())
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = group.findViewById(checkedId)
            chip?.let { chipView ->
                when (chipView.text.toString()) {
                    "All" -> {
//                        Toast.makeText(requireContext(), "All", Toast.LENGTH_SHORT).show()
                        getTxnHistory(11)
                    }

                    "Success" -> {
//                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        getTxnHistory(1)
                    }

                    "Failed" -> {
//                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        getTxnHistory(0)
                    }
                }
            } ?: kotlin.run {

            }
        }
        binding.neddSuppMenuCard.setOnClickListener {
            AppUtility.dialer(requireContext(), "18001371333")
        }
        modifyHeaders()

    }

    private fun getTxnHistory(status: Int) {
        lifecycleScope.launch {
            try {
                txnViewModel.getTxnHistory(status)
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
