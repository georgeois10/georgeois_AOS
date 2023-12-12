package com.example.georgeois.ui.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentLoginFindPasswordBinding
import com.example.georgeois.resource.FieldState
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.viewModel.AuthViewModel
import com.example.georgeois.viewModel.UserViewModel

class LoginFindPasswordFragment : Fragment() {
    private lateinit var loginFindPasswordBinding: FragmentLoginFindPasswordBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel
    private var phoneAuthFlag = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        loginFindPasswordBinding = FragmentLoginFindPasswordBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        authViewModel.run {
            // 휴대폰 인증번호 전송 요청에 따른 Toast 메시지 update
            phoneNumberFieldState.observe(requireActivity()) {
                when(it) {
                    is FieldState.Success -> {
                        Toast.makeText(requireContext(), "${it.data}", Toast.LENGTH_SHORT).show()
                    }

                    is FieldState.Fail -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }

            // 휴대폰 인증 hint, Error message update
            confirmCodeFieldState.observe(requireActivity()) {
                val textInputLayout = loginFindPasswordBinding.textInputLayoutLoginFindPasswordVerificationNumber
                when (it) {
                    is FieldState.Success -> {
                        // 핸드폰 인증 성공
                        val helperMessage = it.data.toString()
                        textInputLayout.error = null
                        textInputLayout.isErrorEnabled = false
                        textInputLayout.helperText = helperMessage
                        // 인증 완료되면 인증번호 발송, 확인 버튼 클릭 불가능하게 변경
                        loginFindPasswordBinding.buttonLoginFindPasswordSendVerificationNumber.isClickable = false
                        loginFindPasswordBinding.buttonLoginFindPasswordCheckVerificationNumber.isClickable = false
                        phoneAuthFlag = true
                    }

                    is FieldState.Fail -> {
                        val errorMessage = it.message.toString()
                        textInputLayout.requestFocus()
                        textInputLayout.isErrorEnabled = true
                        textInputLayout.error = errorMessage
                    }

                    is FieldState.Error -> {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        userViewModel.run {
            findIdList.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        val user = it.data

                        if (user == null) {
                            Toast.makeText(requireContext(), "가입된 계정이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                            return@observe
                        }

                        val bundle = Bundle().apply {
                            putString("u_idx", user[0]["u_idx"])
                            putString("u_nicknm", user[0]["u_nicknm"])
                        }

                        Log.d("myttest", "${user[0]["u_idx"]}")
                        Log.d("myttest", "${user[0]["u_nicknm"]}")

                        mainActivity.replaceFragment(MainActivity.LOGIN_RESET_PASSWORD_FRAGMENT,true, bundle)
                        userViewModel.clearFindIdList()
                    }

                    is FieldState.Fail -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    is FieldState.Error -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loginFindPasswordBinding.run {

            // 툴바
            materialToolbarLoginFindPassword.run {
                title = "비밀번호 찾기"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.LOGIN_FIND_PASSWORD_FRAGMENT)
                }
            }

            // 전화번호로 인증번호 발송 클릭
            buttonLoginFindPasswordSendVerificationNumber.setOnClickListener {
                val phoneNumber = textInputEditTextLoginFindPasswordPnumber.text.toString()
                authViewModel.sendVerificationNumber(phoneNumber, requireActivity())
            }

            // 인증번호 확인 클릭
            buttonLoginFindPasswordCheckVerificationNumber.setOnClickListener {
                val code = textInputEditTextLoginFindPasswordVerificationNumber.text.toString()
                authViewModel.checkVerificationNumber(code)
            }

            // 필드 확인후 비밀번호 재설정 페이지로 이동
            buttonLoginFindPasswordMoveToPasswordReset.setOnClickListener {
                // 핸드폰 번호로 등록된 유저 존재하는지 확인

                val id = loginFindPasswordBinding.textInputEditTextLoginFindPasswordId.text.toString()
                val phoneNumber = loginFindPasswordBinding.textInputEditTextLoginFindPasswordPnumber.text.toString()

                if (id.isEmpty()) {
                    Toast.makeText(requireContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!phoneAuthFlag) {
                    Toast.makeText(requireContext(), "휴대폰 인증을 진행해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                userViewModel.findByIdAndPhoneNumber(id, phoneNumber)
            }

        }

        return loginFindPasswordBinding.root
    }
}