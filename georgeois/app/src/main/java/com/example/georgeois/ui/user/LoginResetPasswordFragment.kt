package com.example.georgeois.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentLoginResetPasswordBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.CheckValidation
import com.example.georgeois.utill.CommonTextWatcher
import com.google.android.material.textfield.TextInputLayout

class LoginResetPasswordFragment : Fragment() {
    private lateinit var loginResetPasswordBinding: FragmentLoginResetPasswordBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginResetPasswordBinding = FragmentLoginResetPasswordBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        loginResetPasswordBinding.run {

            // 툴바
            materialToolbarLoginResetPassword.run {
                title = "비밀번호 재설정"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.LOGIN_RESET_PASSWORD_FRAGMENT)
                }
            }

            // 비밀번호 유효성 검사
            textInputEditTextLoginResetPasswordPw.addTextChangedListener(CommonTextWatcher {
                // 비밀번호 error, hint 처리
                checkPasswordValidate()

                val confirmPwLength = textInputEditTextLoginResetPasswordConfirmPw.text?.length

                // 비밀번호 확인란에 텍스트가 존재할 때
                if(confirmPwLength != null && confirmPwLength > 0 ) {
                    checkConfirmPasswordValidate()
                }
            })

            // 비밀번호 같은지 검사
            textInputEditTextLoginResetPasswordConfirmPw.addTextChangedListener(CommonTextWatcher {
                val password = textInputEditTextLoginResetPasswordPw.text.toString()
                val confirmPassword = textInputEditTextLoginResetPasswordConfirmPw.text.toString()

                val passwordConfirmValidate = CheckValidation().validateConfirmPassword(password, confirmPassword)

                validateMessageHandler(
                    passwordConfirmValidate,
                    textInputLayoutLoginResetPasswordConfirmPw,
                    "비밀번호가 일치합니다.",
                    "비밀번호가 일치하지 않습니다."
                )
            })

            buttonLoginResetPasswordSetNewPw.setOnClickListener {
                mainActivity.removeFragment(MainActivity.LOGIN_RESET_PASSWORD_FRAGMENT)
                mainActivity.replaceFragment(MainActivity.LOGIN_MAIN_FRAGMENT,false,null)
            }
        }


        return loginResetPasswordBinding.root
    }

    private fun checkConfirmPasswordValidate() {
        val password = loginResetPasswordBinding.textInputEditTextLoginResetPasswordPw.text.toString()
        val confirmPassword = loginResetPasswordBinding.textInputEditTextLoginResetPasswordConfirmPw.text.toString()

        val confirmPasswordValidateResult = CheckValidation().validateConfirmPassword(password, confirmPassword)

        validateMessageHandler(
            confirmPasswordValidateResult,
            loginResetPasswordBinding.textInputLayoutLoginResetPasswordConfirmPw,
            "비밀번호가 일치합니다.",
            "비밀번호가 일치하지 않습니다."
        )
    }

    private fun checkPasswordValidate() {
        val password = loginResetPasswordBinding.textInputEditTextLoginResetPasswordPw.text.toString()

        val passwordValidateResult = CheckValidation().validatePassword(password)

        // 비밀번호 error, hint 처리
        validateMessageHandler(
            passwordValidateResult,
            loginResetPasswordBinding.textInputLayoutLoginResetPasswordPw,
            "사용 가능한 비밀번호입니다.",
            "* 8~16사이의 숫자, 영어 소문자, 특수문자의 조합이어야 합니다.\n* 사용 가능한 특수문자( $@!%*#?& )"
        )

    }

    private fun validateMessageHandler(isSucceed: Boolean, textInputLayout: TextInputLayout, helperMessage: String, errorMessage: String) {
        if (isSucceed) {
            textInputLayout.error = null
            textInputLayout.isErrorEnabled = false
            textInputLayout.helperText = helperMessage
        } else {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = errorMessage
        }
    }
}