package com.example.georgeois.utill

import android.util.Patterns

class CheckValidation {
    fun validateEmail(targetString: String): Boolean {
        // 이메일 형식이 아닐 경우
        if (Patterns.EMAIL_ADDRESS.matcher(targetString).matches()) {
            return false
        }
        return true
    }

    /**
     * 1. 하나 이상의 영어 소문자
     * 2. 하나 이상의 숫자
     * 3. 하나 이상의 특수문자 ( $@!%*#?& )
     *
     * 위 경우를 제외한 다른 모든 문자 제외
     */
    fun validatePassword(password: String): Boolean {
        return password.matches("^(?=.*[a-z])(?=.*[0-9])(?=.*[$@!%*#?&])[a-z0-9@!%*#?&]{8,16}$".toRegex())
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

}