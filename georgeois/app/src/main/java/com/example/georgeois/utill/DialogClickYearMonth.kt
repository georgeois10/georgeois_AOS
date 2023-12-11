package com.example.georgeois.utill

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import com.example.georgeois.databinding.DialogClickYearMonthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate

class DialogClickYearMonth(private val context: Context) {

    interface YearMonthListner {
        fun onYearMonthSelected(year: String,month:String)
    }

    private var yearMonthListener: YearMonthListner? = null

    fun setYearMonthListener(listener: YearMonthListner) {
        yearMonthListener = listener
    }

    fun callFunction(currentYear:Int, currentMonth:Int, layoutInflater: LayoutInflater){

        val dialogClickYearMonthBinding = DialogClickYearMonthBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(context)
        dialogClickYearMonthBinding.run {
            var year = yearpickerDatepicker
            var month = monthpickerDatepicker

            year.wrapSelectorWheel = false
            year.wrapSelectorWheel = false

            year.minValue = 2000
            month.minValue = 1

            //  최대값 설정
            year.maxValue = LocalDate.now().year
            month.maxValue = 12
            year.value = currentYear
            month.value = currentMonth
        }

        builder.setView(dialogClickYearMonthBinding.root)
        builder.setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->
            val selectedYear = dialogClickYearMonthBinding.yearpickerDatepicker.value.toString()
            val selectedMonth = dialogClickYearMonthBinding.monthpickerDatepicker.value.toString()
            yearMonthListener?.onYearMonthSelected(selectedYear,selectedMonth)
        }
        builder.setNegativeButton("취소",null)
        val dialog = builder.create()
        dialog.show()
    }


}