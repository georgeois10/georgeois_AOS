package com.example.georgeois.repository

import android.telephony.PhoneNumberUtils
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthRepository {
    companion object {
        private val auth = FirebaseAuth.getInstance()
        private var verificationId: String? = null

        fun sendPhoneNumberVerification(phoneNumber: String) {
//            val countryISO = "KR"
//            val formattedPhoneNumber = PhoneNumberUtils.formatNumber(phoneNumber, countryISO)


            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // 인증 자동으로 완료된 경우 호출
                        Log.d("AuthRepository.sendPhoneNumberVerification", "자동으로 인증됨")
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        // 인증 실패
                        Log.d("AuthRepository.sendPhoneNumberVerification", "인증 실패")
                    }

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                        // 인증 코드 성공적으로 전송된 경우

                        super.onCodeSent(verificationId, token)
                        this@Companion.verificationId = verificationId
                    }

                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        suspend fun checkVerificationNumber(code: String): Boolean? {
            if (verificationId == null) {
                return null
            }
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            var result = false

            try {
                auth.signInWithCredential(credential).addOnCompleteListener {
                    result = it.isSuccessful
                }.await()
            } catch (e: Exception) {
                Log.e("AuthRepository.checkVerificationNumber", "${e.printStackTrace()}")
            }


            return result
        }
    }
}