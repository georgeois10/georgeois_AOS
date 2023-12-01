package com.example.georgeois.repository

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthRepository {
    companion object {
        private val auth = FirebaseAuth.getInstance()
        private var verificationId: String? = null



        fun sendPhoneNumberVerification(phoneNumber: String, activity: Activity, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        fun checkVerificationCode(code: String): PhoneAuthCredential? {
            if (verificationId == null) {
                return null
            }
            return PhoneAuthProvider.getCredential(verificationId!!, code)
        }

        fun signInWithCredential(credential: PhoneAuthCredential, onComplete: (Boolean, String?) -> Unit) {
            auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false,null)
                }
            }.addOnFailureListener { e ->
                onComplete(false, e.message.toString())
            }
        }

        fun setVerificationId(verificationId: String) {
            this.verificationId = verificationId
        }
    }
}