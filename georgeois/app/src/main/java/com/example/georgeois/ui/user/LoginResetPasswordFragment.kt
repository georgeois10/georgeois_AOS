package com.example.georgeois.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentLoginResetPasswordBinding
import com.example.georgeois.resource.FieldState
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.CheckValidation
import com.example.georgeois.utill.CommonTextWatcher
import com.example.georgeois.viewModel.JoinViewModel
import com.example.georgeois.viewModel.UserViewModel
import com.google.android.material.textfield.TextInputLayout

class LoginResetPasswordFragment : Fragment() {
    private lateinit var loginResetPasswordBinding: FragmentLoginResetPasswordBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginResetPasswordBinding = FragmentLoginResetPasswordBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        userViewModel.run {
            // 비밀번호 hint, Error meesage update
            pwFieldState.observe(requireActivity()) {
                updateValidateMessage(loginResetPasswordBinding.textInputLayoutLoginResetPasswordPw, it)
            }

            // 비밀번호 hint, Error meesage update
            confirmPwFieldState.observe(requireActivity()) {
                updateValidateMessage(loginResetPasswordBinding.textInputLayoutLoginResetPasswordConfirmPw, it)
            }

            // 비밀번호 reset
            resetPwState.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        mainActivity.removeFragment(MainActivity.LOGIN_RESET_PASSWORD_FRAGMENT)
                        mainActivity.replaceFragment(MainActivity.LOGIN_MAIN_FRAGMENT,false,null)
                        Toast.makeText(requireContext(), "비밀번호가 변경되었습니다.\n로그인 해 주세요.", Toast.LENGTH_SHORT).show()
                    }

                    else -> Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()

                }
            }
        }

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
                val password = textInputEditTextLoginResetPasswordPw.text.toString()
                val confirmPassword = textInputEditTextLoginResetPasswordConfirmPw.text.toString()

                userViewModel.checkPasswordValidate(password)

                // 비밀번호 확인란에 텍스트가 존재할 때
                if(confirmPassword.isNotEmpty()) {
                    userViewModel.checkConfirmPasswordValidate(password, confirmPassword)
                }
            })

            // 비밀번호 같은지 검사
            textInputEditTextLoginResetPasswordConfirmPw.addTextChangedListener(CommonTextWatcher {
                val password = textInputEditTextLoginResetPasswordPw.text.toString()
                val confirmPassword = textInputEditTextLoginResetPasswordConfirmPw.text.toString()

                userViewModel.checkConfirmPasswordValidate(password, confirmPassword)
            })

            buttonLoginResetPasswordSetNewPw.setOnClickListener {
                val u_idx = requireArguments().getString("u_idx")
                val u_nicknm = requireArguments().getString("u_nicknm")
                val password = loginResetPasswordBinding.textInputEditTextLoginResetPasswordPw.text.toString()

                if (userViewModel.pwFieldState.value is FieldState.Success && userViewModel.confirmPwFieldState.value is FieldState.Success) {
                    userViewModel.resetPassword(u_idx!!, u_nicknm!!, password)
                } else {
                    Toast.makeText(requireContext(), "비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        return loginResetPasswordBinding.root
    }


    /**
     * ViewMdoel의 LiveDate의 결과에 따른 TextInputLayout 의 Helper, Error Message UI 업데이트
     *
     * helperMessage 색은 xml에서 @color/accentGreen 으로 지정 되어 있다.
     * helperMessage 와 errorMessage 의 String 값은 [JoinViewModel] 에서 [FieldState] 에서 적용됨
     *
     * CASE
     * [FieldState.Success.data] 에 helperMessage 설정
     * [FieldState.Fail.message] 에 ErrorMessage 설정
     * [FieldState.Error.message] 에 Error 메시지 설정
     */
    private fun updateValidateMessage(textInputLayout: TextInputLayout, fieldState: FieldState<String>) {

        when (fieldState) {
            is FieldState.Success -> {
                val helperMessage = fieldState.data.toString()
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
                textInputLayout.helperText = helperMessage
            }

            is FieldState.Fail -> {
                val errorMessage = fieldState.message.toString()
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = errorMessage
            }

            is FieldState.Error -> {
                Toast.makeText(requireContext(), "${fieldState.message}.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}