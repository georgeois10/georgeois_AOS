package com.example.georgeois.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.georgeois.repository.AuthRepository
import com.example.georgeois.resource.FieldState
import com.example.georgeois.utill.CheckValidation
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _phoneNumberFieldState = MutableLiveData<FieldState<String>>()
    val phoneNumberFieldState: MutableLiveData<FieldState<String>> = _phoneNumberFieldState

    private val _isPassed = MutableLiveData<FieldState<String>>()
    val isPassed: MutableLiveData<FieldState<String>> = _isPassed

//    fun sendVerificationNumber(phoneNumber: String) {
////        if (!(CheckValidation.validatePhoneNumber(phoneNumber))) {
////            // 올바른 형식이 아닐 때
////            _phoneNumberFieldState.value = FieldState.Fail("올바른 형식이 아닙니다.")
////            return
////        }
//
////        AuthRepository.sendPhoneNumberVerification(phoneNumber)
//        _phoneNumberFieldState.value = FieldState.Success("인증번호가 전송되었습니다.")
//    }

//    fun checkVerificationNumber(code: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = AuthRepository.checkVerificationCode(code)
//
//            if (result == null) {
//                _isPassed.postValue(FieldState.Fail("인증번호 전송을 먼저 진행해주세요."))
//            } else if (result == true) {
//                _isPassed.postValue(FieldState.Success("인증되었습니다."))
//            } else {
//                _isPassed.postValue(FieldState.Fail("인증에 실패하셨습니다. 인증번호를 다시 확인해주세요."))
//            }
//
//            this.cancel()
//        }
//    }

    fun sendVerificationNumber(phoneNumber: String, activity: Activity) {
//        if (!(CheckValidation.validatePhoneNumber(phoneNumber))) {
//            // 올바른 형식이 아닐 때
//            _phoneNumberFieldState.value = FieldState.Fail("올바른 형식이 아닙니다.")
//            return
//        }
        AuthRepository.sendPhoneNumberVerification(phoneNumber, activity, getAuthCallbacks())
        _phoneNumberFieldState.value = FieldState.Success("인증번호가 전송되었습니다.")
    }

    fun checkVerificationNumber(code: String) {
        val credential = AuthRepository.checkVerificationCode(code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (credential == null) {
            _isPassed.value = FieldState.Fail("먼저 인증번호를 전송해 주세요.")
            return
        }

        AuthRepository.signInWithCredential(credential) {isSuccess, message ->
            // 인증 성공
            if (isSuccess) {
                _isPassed.value = FieldState.Success("인증되었습니다.")
                return@signInWithCredential
            }

            // 인증 실패
            if (message == null) {
                _isPassed.value = FieldState.Fail("인증번호가 일치하지 않습니다.")

                return@signInWithCredential
            }

            // 그 외
            _isPassed.value = FieldState.Error("$message")
        }
    }

    private fun getAuthCallbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
//                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
//                TODO("Not yet implemented")
            }

            override fun onCodeSent(verificationCode: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationCode, token)
                AuthRepository.setVerificationId(verificationCode)
            }

        }
    }
}