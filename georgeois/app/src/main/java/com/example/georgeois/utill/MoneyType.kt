package com.example.georgeois.utill

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Locale

class MoneyType {
    fun moneyText(money: String): String {
        // 문자열을 숫자로 변환
        val amount = try {
            money.toDouble()
        } catch (e: NumberFormatException) {
            // 변환 실패 시 기본값으로 0.0을 사용하거나 에러 처리를 수행
            0.0
        }

        // 숫자를 포맷팅
        val formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(amount)

        return "$formattedAmount 원"
    }

    fun formatAmount(amount: Double): String {
        return when {
            amount >= 1e8 -> {
                val formattedValue = String.format("%.1f", amount / 1e8)
                if (formattedValue.endsWith(".0")) "${formattedValue.substringBeforeLast('.')}억원" else "${formattedValue}억원"
            }
            amount >= 1e4 -> {
                val formattedValue = String.format("%.1f", amount / 1e4)
                if (formattedValue.endsWith(".0")) "${formattedValue.substringBeforeLast('.')}만원" else "${formattedValue}만원"
            }
            else -> "${amount.toInt()}원"
        }
    }



}


class MoneyTextWatcher(private val textInputEditText: TextInputEditText) : TextWatcher {
    private val numberFormat = NumberFormat.getInstance(Locale.getDefault())
    private val maxValue = 1_000_000_000 // 10억
    var formattedText: String = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // 이 메서드는 텍스트 변경 전에 호출됩니다.
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // 이 메서드는 텍스트가 변경될 때 호출됩니다.
        val text = s.toString().replace(",", "").trim()
        val parsed = if (text.isNotEmpty()) text.toDouble() else 0.0

        // 강제로 10억으로 제한
        val limitedValue = parsed.coerceAtMost(maxValue.toDouble())

        formattedText = numberFormat.format(limitedValue)

        // 로그 추가
        Log.d("MoneyTextWatcher", "onTextChanged: $formattedText")

        textInputEditText.removeTextChangedListener(this)
        textInputEditText.setText(formattedText)
        textInputEditText.setSelection(formattedText.length)
        textInputEditText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        // 이 메서드는 텍스트 변경 후에 호출됩니다.
    }
}


