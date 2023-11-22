package com.example.georgeois.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentJoinMainBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.CheckValidation
import com.example.georgeois.utill.CommonTextWatcher
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class JoinMainFragment : Fragment() {
    private lateinit var joinMainBinding: FragmentJoinMainBinding
    private lateinit var mainActivity: MainActivity
    private var selectedYear: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        joinMainBinding = FragmentJoinMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        joinMainBinding.run {

            // 툴바
            materialToolbarJoinMain.run {
                title = "회원가입"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.JOIN_MAIN_FRAGMENT)
                }
            }

            // profile 이미지 클릭
            circleImageViewJoinMainProfile.setOnClickListener {
                Toast.makeText(requireContext(), "이미지 클릭", Toast.LENGTH_SHORT).show()
                // TODO : 갤러리 권한 확인 후 갤러리로 이동하여 사진 받아오기
            }

            // 아이디 중복확인 클릭
            buttonJoinMainCheckIdDuplication.setOnClickListener {
                // TODO : id 중복 확인 기능 구현
            }

            // 비밀번호 유효성 검사
            textInputEditTextJoinMainPw.addTextChangedListener(CommonTextWatcher {
                checkPasswordValidate()

                val confirmPwLength = textInputEditTextJoinMainConfirmPw.text?.length

                // 비밀번호 확인란에 텍스트가 존재할 때
                if(confirmPwLength != null && confirmPwLength > 0 ) {
                    checkConfirmPasswordValidate()
                }
            })

            // 비밀번호 같은지 검사
            textInputEditTextJoinMainConfirmPw.addTextChangedListener(CommonTextWatcher {
                checkConfirmPasswordValidate()
            })

            // 닉네임 중복 확인 클릭
            buttonJoinMainCheckNickNmDuplication.setOnClickListener {
                // TODO : 닉네임 중복 확인 기능 구현
            }

            // 전화번호로 인증 문자 전송 버튼 클릭
            buttonJoinMainSendVerificationNumber.setOnClickListener {
                // TODO : 전화번호로 문자 전송 기능 구현
            }

            // 인증번호 확인 클릭
            buttonJoinMainCheckVerificationNumber.setOnClickListener {
                // TODO : 인증번호 확인 기능 구현
            }

            // 성별 선택
            radioGroupJoinMainGender.setOnCheckedChangeListener { _, chckedId ->
                when (chckedId) {
                    radioButtonJoinMainMale.id -> {
                        Toast.makeText(requireContext(), "Male 선택", Toast.LENGTH_SHORT).show()
                    }

                    radioButtonJoinMainFemale.id -> {
                        Toast.makeText(requireContext(), "Female 선택", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // 출생년도 스피너 클릭
            setBirthYearSpinner()

            // 회원가입 클릭
            buttonJoinMainJoin.setOnClickListener {
                // TODO : 회원가입 기능 구현
                mainActivity.removeFragment(MainActivity.JOIN_MAIN_FRAGMENT)
                mainActivity.replaceFragment(MainActivity.LOGIN_MAIN_FRAGMENT,false,null)
            }
        }

        return joinMainBinding.root
    }

    private fun checkConfirmPasswordValidate() {
        val password = joinMainBinding.textInputEditTextJoinMainPw.text.toString()
        val confirmPassword = joinMainBinding.textInputEditTextJoinMainConfirmPw.text.toString()

        val confirmPasswordValidateResult = CheckValidation().validateConfirmPassword(password, confirmPassword)

        validateMessageHandler(
            confirmPasswordValidateResult,
            joinMainBinding.textInputLayoutJoinMainConfirmPw,
            "비밀번호가 일치합니다.",
            "비밀번호가 일치하지 않습니다."
        )
    }

    private fun checkPasswordValidate() {
        val password = joinMainBinding.textInputEditTextJoinMainPw.text.toString()

        val passwordValidateResult = CheckValidation().validatePassword(password)

        // 비밀번호 error, hint 처리
        validateMessageHandler(
            passwordValidateResult,
            joinMainBinding.textInputLayoutJoinMainPw,
            "사용 가능한 비밀번호입니다.",
            "* 8~16사이의 숫자, 영어 소문자, 특수문자의 조합이어야 합니다.\n* 사용 가능한 특수문자( $@!%*#?& )"
        )

    }

    /**
     * isSucceed : 성공 시 true 실패 시 false
     * textInputLayout : TextInputLayout
     * helperMessage : 성공 시 메세지, fragment_join_main.xml 의 TextInputLayout 에 @color/accentGreen 으로 지정 되어 있다.
     * errorMessage : 실패 시 메세지
     *
     * ViewModel 적용 시 isSucced 바뀔 가능성 있음.
     */
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

    private fun setBirthYearSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear downTo 1900).map { it.toString() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, years)

        joinMainBinding.spinnerJoinMainBirthYear.run {
            this.adapter = adapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedYear = years[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }
}