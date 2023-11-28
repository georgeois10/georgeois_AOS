package com.example.georgeois.utill

import android.util.Patterns

object CheckValidation {

    /**
     * 1. 하나 이상의 영어 소문자
     * 2. 숫자 (선택)
     * 
     * 위 경우를 제외한 다른 모든 문자 제외
     */
    fun validateId(id: String) : Boolean {
        return id.matches("^(?=.*[a-z])[a-z0-9]{5,20}$".toRegex())
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


    /**
     * 1. 하나 이상의 영어 소문자 or 영어 대문자 or 한글
     *
     * 위 경우를 제외한 다른 모든 문자 제외
     */
    fun validateName(name: String) : Boolean {
        return name.matches("^(?=.*[가-힣a-zA-Z])[가-힣a-zA-Z]{2,20}$".toRegex())
    }

    /**
     * 1. 하나 이상의 영어 소문자 or 영어 대문자 or 한글
     * 2. 숫자 (없어도 되고 있어도 되고)
     *
     * 위 경우를 제외한 다른 모든 문자 제외
     */
    fun validateNickName(nickname: String): Boolean {
        return nickname.matches("^(?=.*[가-힣a-zA-Z])[가-힣a-zA-Z0-9]{5,20}$".toRegex())
    }

    /**
     * 1. 하나 이상의 숫자
     *
     * 위 경우를 제외한 다른 모든 문자 제외
     */
    fun validatePhoneNumber(phoneNumber: String) : Boolean {
        return phoneNumber.matches("^(?=.*[0-9])[0-9]{5,15}$".toRegex())
//        return Patterns.PHONE.matcher(phoneNumber).matches()
    }


    fun validateEmail(targetString: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(targetString).matches()

    }

}