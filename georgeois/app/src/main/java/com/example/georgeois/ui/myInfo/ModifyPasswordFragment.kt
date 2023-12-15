package com.example.georgeois.ui.myInfo

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentModifyPasswordBinding
import com.example.georgeois.resource.FieldState
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.CheckValidation
import com.example.georgeois.viewModel.AuthViewModel
import com.example.georgeois.viewModel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ModifyPasswordFragment : Fragment() {
    private lateinit var fragmentModifyPasswordBinding: FragmentModifyPasswordBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var userViewModel: UserViewModel
    private lateinit var authViewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentModifyPasswordBinding = FragmentModifyPasswordBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        userViewModel = ViewModelProvider(mainActivity)[UserViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        var sendCodeFlag = false
        var checkCodeFlag = false

        authViewModel.run {
            phoneNumberFieldState.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        val title = "인증 번호 전송 완료"
                        val message = "인증 번호가 전송되었습니다."
                        createNegativeDialogBuilder(title, message).show()
                    }

                    is FieldState.Fail -> {
                        val title = "인증 번호 전송 실패"
                        val message = it.message!!
                        createNegativeDialogBuilder(title, message).show()
                    }

                    is FieldState.Error -> Unit
                }
            }

            confirmCodeFieldState.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        checkCodeFlag = true

                        fragmentModifyPasswordBinding.run {
                            buttonModifyPasswordSendCode.isEnabled = false
                            buttonModifyPasswordCheckCode.isEnabled = false
                            textInputLayoutModifyPasswordInputNewPassword.isEnabled = true
                            textInputLayoutModifyPasswordInputNewPasswordCheck.isEnabled = true
                        }

                        val title = "인증 완료"
                        val message = "인증이 완료되었습니다."
                        createNegativeDialogBuilder(title, message).show()
                    }

                    is FieldState.Fail -> {
                        val title = "인증 번호 확인 실패"
                        val message = it.message!!
                        createNegativeDialogBuilder(title, message).show()
                    }

                    is FieldState.Error -> {
                        val title = "인증 번호 확인 오류"
                        val message = it.message!!
                        createNegativeDialogBuilder(title, message).show()
                    }
                }
            }
        }

        userViewModel.run {
            resetPwState.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        val title = "비밀번호 변경 완료"
                        val message = "비밀번호가 변경되었습니다."
                        createBaseDialogBuilder(title, message).setNegativeButton("확인") { _:DialogInterface, i: Int ->
                            mainActivity.removeFragment(MainActivity.MODIFY_PASSWORD_FRAGMENT)
                        }.show()
                    }

                    else -> Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fragmentModifyPasswordBinding.run {
            textInputLayoutModifyPasswordInputNewPassword.isEnabled = false
            textInputLayoutModifyPasswordInputNewPasswordCheck.isEnabled = false

            toolbarModifyPassword.run {
                title = "비밀번호 변경"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MODIFY_PASSWORD_FRAGMENT)
                }
            }

            // 인증번호 전송 버튼 클릭
            buttonModifyPasswordSendCode.setOnClickListener {
                val phoneNumber = textInputEdixTextModifyPasswordInputPhoneNumber.text.toString()

                // 로그인 되어있어야만 비밀번호 변경 가능
                if (userViewModel.user.value == null) {
                    val title = "로그인 오류"
                    val message = "로그인 후 이용 가능합니다."
                    createNegativeDialogBuilder(title, message).show()
                    return@setOnClickListener
                }

                // 전화번호 형식 검사
                if(containsSpecialCharacters(phoneNumber) || phoneNumber.length != 11 || !phoneNumber.startsWith("010")) {
                    val title = "휴대폰 번호 입력 오류"
                    val message = "올바른 형식의 휴대폰 번호를 입력해주세요."
                    createNegativeDialogBuilder(title, message).show()
                    return@setOnClickListener
                }

                // 등록된 전화번호와 일치하는지 검사
                if (phoneNumber != userViewModel.user.value!!.u_pNumber) {
                    val title = "등록된 전화번호와 일치하지 않습니다."
                    val message = "계정에 등록된 번호로 인증해 주세요."
                    createNegativeDialogBuilder(title, message).show()
                    return@setOnClickListener
                }

                sendCodeFlag = true
                authViewModel.sendVerificationNumber(phoneNumber, mainActivity)
            }

            // 인증번호 확인 버튼 클릭
            buttonModifyPasswordCheckCode.setOnClickListener {
                val code = textInputEditTextModifyPasswordInputCode.text.toString()
                authViewModel.checkVerificationNumber(code)
            }

            // 저장 버튼 클릭
            buttonModifyPasswordSave.setOnClickListener {
                if(!sendCodeFlag || !checkCodeFlag){
                    val title = "본인 인증 오류"
                    val message = "본인 인증을 진행해주세요."
                    createNegativeDialogBuilder(title, message).show()
                }

                val newPw = textInputEditTextModifyPasswordInputNewPassword.text.toString()
                val newPwCheck = textInputEditTextModifyPasswordInputNewPasswordCheck.text.toString()

                if (!(CheckValidation.validatePassword(newPw))) {
                    val title = "비밀번호 입력 오류"
                    val message = "8~16사이의 숫자, 영어 소문자, 특수문자의 조합이어야 합니다.\n사용 가능한 특수문자( $@!%*#?& )"
                    createNegativeDialogBuilder(title, message).show()
                    return@setOnClickListener
                }

                if (!(CheckValidation.validateConfirmPassword(newPw, newPwCheck))) {
                    val title = "비밀번호 입력 오류"
                    val message = "입력한 비밀번호를 확인해주세요."
                    createNegativeDialogBuilder(title, message).show()
                    return@setOnClickListener
                }


                // 인증번호 전송 할 때 user null check 하고
                // 인증번호가 전송 되었는지 여부는 위에서 먼저 학인하기 때문에
                // userViewModel.user.value 는 null이 아님
                val user = userViewModel.user.value!!
                userViewModel.resetPassword(user.u_idx, user.u_nickNm, newPw)
            }
        }

        return fragmentModifyPasswordBinding.root
    }

    /**
     * NegativeButton 만 설정되어 있음.
     * 인증 확인 여부, 올바른 비밀번호 형식 여부 등
     * 정보를 띄워줄 때 사용될 Dialog
     */
    private fun createNegativeDialogBuilder(title: String, message: String): MaterialAlertDialogBuilder {
        return createBaseDialogBuilder(title, message).setNegativeButton("확인", null)
    }

    /**
     * title, message 만 적용된 DialogBuilder return
     */
    private fun createBaseDialogBuilder(title: String, message: String) : MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
            val customTitle = setCustomTitle(title)
            setCustomTitle(customTitle)
            setMessage(message)
        }
    }

    fun containsSpecialCharacters(phoneNumber: String): Boolean {
        // 정규 표현식을 사용하여 특수 문자가 있는지 검사합니다.
        // 숫자, 공백, 더하기(+), 하이픈(-)을 제외한 모든 문자를 특수 문자로 간주합니다.
        val regex = Regex("[^\\d\\s+\\-]")
        return regex.containsMatchIn(phoneNumber)
    }

    fun setCustomTitle(title: String): TextView {
        val customTitle = TextView(context).apply {
            text = title  // 타이틀 텍스트 설정
            textSize = 25f  // 텍스트 사이즈 설정
            typeface = ResourcesCompat.getFont(context, R.font.space)  // 글꼴 스타일 설정
            setTextColor(Color.BLACK)  // 텍스트 색상 설정
            setPadding(100, 100, 0, 20)  // 패딩 설정 (단위: px)
        }
        return customTitle
    }
}