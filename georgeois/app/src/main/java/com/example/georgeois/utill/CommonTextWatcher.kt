package com.example.georgeois.utill

import android.text.Editable
import android.text.TextWatcher

class CommonTextWatcher(
    private val afterChanged: (Editable?) -> Unit
) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//        TODO("Not yet implemented")
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//        TODO("Not yet implemented")
    }

    override fun afterTextChanged(editable: Editable?) {
        afterChanged(editable)
    }
}