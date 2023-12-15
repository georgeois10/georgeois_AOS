package com.example.georgeois.utill

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.CycleInterpolator
import android.widget.TextView
import com.example.georgeois.R
import com.google.android.material.textfield.TextInputEditText

class ContentTextWatcher(private val textInputEditText: TextInputEditText, private val textView: TextView) : TextWatcher {

    private val maxCharacterCount = 1000

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    @SuppressLint("ResourceAsColor")
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val textLength = s?.length ?: 0
        textView.text = "$textLength/$maxCharacterCount"

        if (textLength >= maxCharacterCount) {
            textView.setTextColor(R.color.accentRed)
            shakeView(textView)
        } else {
            textView.setTextColor(R.color.black)
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun shakeView(view: View) {
        val shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)
        shakeAnimator.duration = 500
        shakeAnimator.interpolator = CycleInterpolator(2f) // Number of cycles for the animation

        shakeAnimator.start()
    }

    private fun shakeView(editText: TextInputEditText) {
        val parentView = editText.parent as View // Assuming TextInputEditText is directly inside a parent view
        shakeView(parentView)
    }
}
