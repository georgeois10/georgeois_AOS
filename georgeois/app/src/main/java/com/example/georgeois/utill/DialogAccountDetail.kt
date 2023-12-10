package com.example.georgeois.utill

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.georgeois.databinding.DialogAccountDetailBinding
import com.example.georgeois.dataclass.AccountBookClass
import com.example.georgeois.repository.InAccountBookRepository
import com.example.georgeois.repository.OutAccountBookRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DialogAccountDetail(private val context: Context, private val layoutInflater: LayoutInflater) {
    // 호출할 다이얼로그 함수를 정의한다.
    fun callFunction(accountDetail:AccountBookClass) {

        val dialogAccountDetailBinding = DialogAccountDetailBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(context)
        dialogAccountDetailBinding.textViewDialogAccountDetailCategory.text = accountDetail.category
        var moneyType = MoneyType()
        var tempMoney = accountDetail.amount.toString()
        tempMoney = moneyType.moneyText(tempMoney)
        dialogAccountDetailBinding.textViewDialogAccountDetailAmount.text = tempMoney
        dialogAccountDetailBinding.textViewDialogAccountDetailDate.text = accountDetail.date
        dialogAccountDetailBinding.textViewDialogAccountDetailContent.text = accountDetail.content
        dialogAccountDetailBinding.textViewDialogAccountDetailProperty.text = accountDetail.property.toString()

        if(accountDetail.property==null) {
            dialogAccountDetailBinding.linearLayoutDialogAccountDetailProperty.visibility = View.GONE
        }else{
            dialogAccountDetailBinding.linearLayoutDialogAccountDetailProperty.visibility = View.VISIBLE
        }

        builder.setView(dialogAccountDetailBinding.root)
        builder.setPositiveButton("수정") { dialog, which ->

        }
        builder.setNegativeButton("취소"){ dialog,which ->

        }

        val dialog = builder.create()

        dialogAccountDetailBinding.imageButtonDialogAccountDetailDelete.setOnClickListener {
            when(accountDetail.isInorOut) {
                'i' -> InAccountBookRepository.deleteInAccountBook(accountDetail.idx)
                'o' -> OutAccountBookRepository.deleteInAccountBook(accountDetail.idx)
            }
            dialog.dismiss()
        }
        dialogAccountDetailBinding.imageButtonDialogAccountDetail.setOnClickListener {
            dialogAccountDetailBinding.constraintLayoutDialogAccountDetail.visibility = View.VISIBLE
            dialogAccountDetailBinding.imageButtonDialogAccountDetail.visibility = View.GONE
        }
        dialogAccountDetailBinding.imageButtonDialogAccountDetailImageDelete.setOnClickListener {
            dialogAccountDetailBinding.constraintLayoutDialogAccountDetail.visibility = View.GONE
            dialogAccountDetailBinding.imageButtonDialogAccountDetail.visibility = View.VISIBLE
        }

        dialog.show()
    }
}
