package com.example.georgeois.utill

import DateTimePicker
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.example.georgeois.R
import com.example.georgeois.databinding.DialogAccountDetailBinding
import com.example.georgeois.dataclass.AccountBookClass
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.dataclass.OutAccountBookClass
import com.example.georgeois.repository.InAccountBookRepository
import com.example.georgeois.repository.OutAccountBookRepository
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface DialogDismissListener {
    fun onDialogDismissed()
}

class DialogAccountDetail(private val context: Context, private val layoutInflater: LayoutInflater, var inCategory:String,var outCategory:String) {
    // 호출할 다이얼로그 함수를 정의한다.
    private var dismissListener: DialogDismissListener? = null

    fun setOnDialogDismissListener(listener: DialogDismissListener) {
        dismissListener = listener
    }
    fun callFunction(accountDetail:AccountBookClass) {
        lateinit var moneyTextWatcher : MoneyTextWatcher

        var uCategory = ""
        var uProperty = 'H'
        var uContent = ""
        var uAmount = ""
        var uBudgetyn = 0
        var uDate = ""
        val dialogAccountDetailBinding = DialogAccountDetailBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(context)
        dialogAccountDetailBinding.run {
            moneyTextWatcher = MoneyTextWatcher(textInputEditTextDialogAccountDetailAmount)
            textInputLayoutDialogAccountDetailAmount.editText?.addTextChangedListener(moneyTextWatcher)
            textInputLayoutDialogAccountDetailAmount.editText?.setText(accountDetail.amount.toString())
            textInputLayoutDialogAccountDetailContent.editText?.setText(accountDetail.content)
            //textViewDialogAccountDetailDate.text = accountDetail.date
            // accountDetail.date = 2023-12-11 16:02:00
            var settingDate = LocalDateTime.parse(accountDetail.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
            textViewDialogAccountDetailDate.text = settingDate
            uDate = accountDetail.date
            val dateTimePicker = DateTimePicker(context)

            textViewDialogAccountDetailDate.setOnClickListener {
                dateTimePicker.setDateTimeListener(object :DateTimePicker.DateTimeListener{
                    override fun onDateTimeSelected(dateTime: String) {
                        textViewDialogAccountDetailDate.text = dateTime
                        uDate = dateTimePicker.updateDate(dateTime)
                    }

                })
                dateTimePicker.showDateTimePicker()
            }


            // 예산 설정
            when(accountDetail.budregi_yn){
                0 -> checkBoxDialogAccountDetailNoBudget.isChecked = false
                1 -> checkBoxDialogAccountDetailNoBudget.isChecked = true
            }

            // 자산 설정
            var previousCheckedChip:Chip? = null
            when(accountDetail.property){
                null -> dialogAccountDetailBinding.linearLayoutDialogAccountDetailProperty.visibility = View.GONE
                'S' -> {
                    previousCheckedChip = chipDialogAccountDetailCreditCard
                    chipDialogAccountDetailCreditCard.isChecked = true
                }
                'H' -> {
                    previousCheckedChip = chipDialogAccountDetailCash
                    chipDialogAccountDetailCash.isChecked = true
                }
                'C' -> {
                    previousCheckedChip = chipDialogAccountDetailCashCard
                    chipDialogAccountDetailCashCard.isChecked = true
                }
            }
            val chips = dialogAccountDetailBinding.chipGroupDialogAccountDetail.children.toList()
            for (chip in chips) {
                if (chip is Chip) {
                    chip.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            previousCheckedChip?.isChecked = false
                            previousCheckedChip = chip
                            uProperty = when(chip.text.toString()){
                                "현금"-> 'H'
                                "체크카드" -> 'C'
                                "신용카드" -> 'S'
                                else -> 'H'
                            }
                        }
                        else{
                            if (chips.none { it is Chip && it.isChecked }) {
                                chip.isChecked = true
                            }
                        }
                    }
                }
            }

            // 카테고리 설정
            var inCategoryList = inCategory.split(",")
            var outCategoryList = outCategory.split(",")
            spinnerDialogAccountDetail.adapter = when(accountDetail.isInorOut) {
                'i' -> CategorySpinnerAdapter(context, R.layout.item_spinner_home_register, inCategoryList)
                'o' -> CategorySpinnerAdapter(context, R.layout.item_spinner_home_register, outCategoryList)
                else -> throw IllegalArgumentException("isInorOut 값이 잘못되었습니다: ${accountDetail.isInorOut}")
            }
            val categoryList = when(accountDetail.isInorOut) {
                'i' -> inCategoryList
                'o' -> outCategoryList
                else -> emptyList()
            }
            val categoryIndex = categoryList.indexOf(accountDetail.category)
            if (categoryIndex != -1) {
                spinnerDialogAccountDetail.setSelection(categoryIndex)
            } else {
                throw IllegalArgumentException("category값이 잘못되었습니다.: ${accountDetail.category}")
            }

            spinnerDialogAccountDetail.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    uCategory = spinnerDialogAccountDetail.getItemAtPosition(p2).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        }
        
        builder.setView(dialogAccountDetailBinding.root)
        builder.setPositiveButton("수정") { dialog, which ->
            uContent = dialogAccountDetailBinding.textInputEditTextDialogAccountDetailContent.text.toString()
            uAmount = dialogAccountDetailBinding.textInputEditTextDialogAccountDetailAmount.text.toString()
            uBudgetyn = if(dialogAccountDetailBinding.checkBoxDialogAccountDetailNoBudget.isChecked){
                1
            }else{
                0
            }
            uAmount = uAmount.replace(",","")

            when(accountDetail.isInorOut) {
                'i' -> {
                    var inAccountBook = InAccountBookClass(
                        accountDetail.idx,
                        accountDetail.uIdx,
                        accountDetail.creUser,
                        accountDetail.uNicknm,
                        uAmount.toInt(),
                        uContent,
                        uCategory,
                        uDate,
                        accountDetail.imgpath,
                        uBudgetyn
                    )
                    InAccountBookRepository.updateInAccountBook(inAccountBook)
                }
                'o' -> {
                    var outAccountBook = OutAccountBookClass(
                        accountDetail.idx,
                        accountDetail.uIdx,
                        accountDetail.creUser,
                        accountDetail.uNicknm,
                        uAmount.toInt(),
                        uContent,
                        uCategory,
                        uDate,
                        accountDetail.imgpath,
                        uBudgetyn,
                        uProperty
                    )
                    OutAccountBookRepository.updateOutAccountBook(outAccountBook)
                }
            }
            dismissListener?.onDialogDismissed()
            dialog.dismiss()

        }
        builder.setNegativeButton("삭제"){ dialog,which ->
            val deleteBuilder = MaterialAlertDialogBuilder(context, R.style.DialogTheme).apply {
                val customTitle = setCustomTitle("삭제하시겠습니까?")
                setCustomTitle(customTitle)
                setNegativeButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
                    when(accountDetail.isInorOut) {
                        'i' -> InAccountBookRepository.deleteInAccountBook(accountDetail.idx)
                        'o' -> OutAccountBookRepository.deleteInAccountBook(accountDetail.idx)
                    }
                    dismissListener?.onDialogDismissed()
                    dialog.dismiss()
                }
                setPositiveButton("취소",null)
            }
            deleteBuilder.show()
        }

        val dialog = builder.create()
        dialogAccountDetailBinding.imageButtonDialogAccountDetailClose.setOnClickListener {
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
    fun setCustomTitle(title: String): TextView {
        val customTitle = TextView(context).apply {
            text = title  // 타이틀 텍스트 설정
            textSize = 25f  // 텍스트 사이즈 설정
            typeface = ResourcesCompat.getFont(context, R.font.space)  // 글꼴 스타일 설정
            setTextColor(Color.BLACK)  // 텍스트 색상 설정
            setPadding(100, 100, 0, 20)  // 패딩 설정 (단위: px)
        }
        return customTitle
    }

}
