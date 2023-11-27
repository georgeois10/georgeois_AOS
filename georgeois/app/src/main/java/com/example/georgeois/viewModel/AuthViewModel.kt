package com.example.georgeois.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.georgeois.repository.AuthRepository
import com.example.georgeois.resource.FieldState
import com.example.georgeois.utill.CheckValidation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _phoneNumberFieldState = MutableLiveData<FieldState<String>>()
    val phoneNumberFieldState: MutableLiveData<FieldState<String>> = _phoneNumberFieldState

    private val _isPassed = MutableLiveData<FieldState<String>>()
    val isPassed: MutableLiveData<FieldState<String>> = _isPassed

    fun sendVerificationNumber(phoneNumber: String) {
//        if (!(CheckValidation.validatePhoneNumber(phoneNumber))) {
//            // 올바른 형식이 아닐 때
//            _phoneNumberFieldState.value = FieldState.Fail("올바른 형식이 아닙니다.")
//            return
//        }

        AuthRepository.sendPhoneNumberVerification(phoneNumber)
        _phoneNumberFieldState.value = FieldState.Success("인증번호가 전송되었습니다.")
    }

    fun checkVerificationNumber(code: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = AuthRepository.checkVerificationNumber(code)

            if (result == null) {
                _isPassed.postValue(FieldState.Fail("인증번호 전송을 먼저 진행해주세요."))
            } else if (result == true) {
                _isPassed.postValue(FieldState.Success("인증되었습니다."))
            } else {
                _isPassed.postValue(FieldState.Fail("인증에 실패하셨습니다. 인증번호를 다시 확인해주세요."))
            }

            this.cancel()
        }
    }
}