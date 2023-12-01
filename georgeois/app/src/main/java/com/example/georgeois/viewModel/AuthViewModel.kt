package com.example.georgeois.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.georgeois.repository.AuthRepository
import com.example.georgeois.resource.FieldState
import com.example.georgeois.utill.CheckValidation
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _phoneNumberFieldState = MutableLiveData<FieldState<String>>()
    val phoneNumberFieldState: MutableLiveData<FieldState<String>> = _phoneNumberFieldState

    private val _confirmCodeFieldState = MutableLiveData<FieldState<String>>()
    val confirmCodeFieldState: MutableLiveData<FieldState<String>> = _confirmCodeFieldState

    // 인증번호 전송
    fun sendVerificationNumber(phoneNumber: String, activity: Activity) {
        if (!(CheckValidation.validatePhoneNumber(phoneNumber))) {
            // 올바른 형식이 아닐 때
            _phoneNumberFieldState.value = FieldState.Fail("올바른 형식이 아닙니다.")
            return
        }

//       아래 phoneNumber는 테스트용 전화번호이다.
//       val phoneNumber1 = "+16505555555"

        val krPhoneNumber = "+82$phoneNumber"

        AuthRepository.sendPhoneNumberVerification(krPhoneNumber, activity, getAuthCallbacks())
        _phoneNumberFieldState.value = FieldState.Success("인증번호가 전송되었습니다.")
    }

    // 인증번호 유효성 검사 후 인증 확인 진행
    fun checkVerificationNumber(code: String) {
        if (!(CheckValidation.validateVerifyCode(code))) {
            _confirmCodeFieldState.value = FieldState.Fail("* 유효하지 않은 인증번호입니다.")
            return
        }
        val credential = AuthRepository.checkVerificationCode(code)
        signInWithPhoneAuthCredential(credential)
    }

    // 인증번호 확인
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (credential == null) {
            _confirmCodeFieldState.value = FieldState.Fail("* 먼저 인증번호를 전송해 주세요.")
            return
        }

        AuthRepository.signInWithCredential(credential) {isSuccess, message ->
            // 인증 성공
            if (isSuccess) {
                _confirmCodeFieldState.value = FieldState.Success("* 인증되었습니다.")
                return@signInWithCredential
            }

            // 인증 실패
            if (message == null) {
                Log.e("AuthViewModel-signInWithPhoneAuthCredential-1", message.toString())
                _confirmCodeFieldState.value = FieldState.Fail("* 인증번호가 일치하지 않습니다.")

                return@signInWithCredential
            }

            // 그 외
            Log.e("AuthViewModel-signInWithPhoneAuthCredential", message.toString())
        }
    }

    /**
     * [sendVerificationNumber] 에서 사용될 Callback
     */
    private fun getAuthCallbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // 자동 인증
                _confirmCodeFieldState.value = FieldState.Success("* 인증되었습니다.")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // 인증에 문제 발생 시 - 잘못된 확인 요청 일 때ㅊqq
                _confirmCodeFieldState.value = FieldState.Error("인증번호 전송에 문제가 발생했습니다.")

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // 유효하지 않은 요청
                        Log.e("AuthViewModel-getAuthCallbacks-invalid", "${e.printStackTrace()}")
                        _confirmCodeFieldState.value = FieldState.Error("유효하지 않은 요청입니다.")
                    }

                    is FirebaseTooManyRequestsException -> {
                        // 프로젝트의 sms 요청한도 초과
                        Log.e("AuthViewModel-getAuthCallbacks-tooManyRequest", "${e.printStackTrace()}")
                        _confirmCodeFieldState.value = FieldState.Error("너무 많이 요청되었습니다. 잠시 후 다시 시도해주세요.")
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        //  Activity가 null 인상태로 reCAPTCHA 시도
                        Log.e("AuthViewModel-getAuthCallbacks-activityMissing", "${e.printStackTrace()}")
                        _confirmCodeFieldState.value = FieldState.Error("reCAPTCHA 를 시도할 수 없습니다.")
                    }

                    else -> {
                        Log.e("AuthViewModel-getAuthCallbacks", "${e.printStackTrace()}")
                        _confirmCodeFieldState.value = FieldState.Error("오류가 발생했습니다.")
                    }
                }
            }

            override fun onCodeSent(verificationCode: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationCode, token)
                // 코드 전송 시
                AuthRepository.setVerificationId(verificationCode)
            }

        }
    }
}