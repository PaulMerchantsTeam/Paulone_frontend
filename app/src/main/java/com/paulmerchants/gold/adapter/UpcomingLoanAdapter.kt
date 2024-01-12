package com.paulmerchants.gold.adapter


import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.paulmerchants.gold.adapterviewholders.UpcomingLoansViewHolder
import com.paulmerchants.gold.databinding.ItemUpcomingDueLoanBinding

import com.paulmerchants.gold.model.GetPendingInrstDueRespItem
import com.paulmerchants.gold.model.newmodel.GepPendingRespObj
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit


/**
 * due date--"2022-06-11T00:00:00
 *
 * Current date----
 *
 * current date - due date-----will give me the day....
 *
 * case 1 - Overdue by 4 days
 * case 2 - Due in 2 days
 * case 3 - Due by 10 days
 * case 4 - Due in 18 days
 *
 *
 */
class UpcomingLoanAdapter(private val onPayDueClicked: (GetPendingInrstDueRespItem) -> Unit) :
    ListAdapter<GetPendingInrstDueRespItem, UpcomingLoansViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UpcomingLoansViewHolder(
        ItemUpcomingDueLoanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: UpcomingLoansViewHolder, position: Int) {
        holder.bind(getItem(position), onPayDueClicked)
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<GetPendingInrstDueRespItem>() {
                override fun areItemsTheSame(
                    oldItem: GetPendingInrstDueRespItem,
                    newItem: GetPendingInrstDueRespItem,
                ): Boolean = oldItem.acNo == newItem.acNo

                override fun areContentsTheSame(
                    oldItem: GetPendingInrstDueRespItem,
                    newItem: GetPendingInrstDueRespItem,
                ): Boolean = true
            }
    }


}


//fun main() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val specificDate = LocalDateTime.parse("2024-06-21T00:00:00")
//        val currentDate = LocalDateTime.now()
//
//        val daysDifference = ChronoUnit.DAYS.between(specificDate, currentDate)
//        println("The number of days difference is: $daysDifference")
//    } else {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//        val specificDate = dateFormat.parse("2024-06-21T00:00:00")
//        val currentDate = Date()
//        val calendar1 = Calendar.getInstance()
//        val calendar2 = Calendar.getInstance()
//        calendar1.time = specificDate
//        calendar2.time = currentDate
//        val millisecondsDifference = calendar2.timeInMillis - calendar1.timeInMillis
//        val daysDifference = TimeUnit.MILLISECONDS.toDays(millisecondsDifference)
//        println("The number of days difference is: $daysDifference")
//    }
//
//}