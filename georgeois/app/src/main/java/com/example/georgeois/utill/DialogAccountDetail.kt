package com.example.georgeois.utill

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.georgeois.databinding.DialogAccountDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DialogAccountDetail(private val context: Context, private val layoutInflater: LayoutInflater) {
    // 호출할 다이얼로그 함수를 정의한다.
    fun callFunction() {

        val dialogAccountDetailBinding = DialogAccountDetailBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(context)
        builder.setView(dialogAccountDetailBinding.root)

        builder.setPositiveButton("수정") { dialog, which ->

        }
        builder.setNegativeButton("취소"){ dialog,which ->

        }

        val dialog = builder.create()

        dialogAccountDetailBinding.imageButtonDialogAccountDetailDelete.setOnClickListener {
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
