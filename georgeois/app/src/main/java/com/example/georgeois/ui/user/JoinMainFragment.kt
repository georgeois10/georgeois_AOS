package com.example.georgeois.ui.user

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.georgeois.R
import com.example.georgeois.resource.FieldState
import com.example.georgeois.databinding.FragmentJoinMainBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.CommonTextWatcher
import com.example.georgeois.viewModel.AuthViewModel
import com.example.georgeois.viewModel.JoinViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class JoinMainFragment : Fragment() {
    private lateinit var joinMainBinding: FragmentJoinMainBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var joinViewModel: JoinViewModel
    private lateinit var authViewModel: AuthViewModel
    private var selectedYear: Int? = null
    private val imageLoadLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                joinMainBinding.circleImageViewJoinMainProfile.setImageURI(uri)
                joinViewModel = ViewModelProvider(this)[JoinViewModel::class.java]
                joinViewModel.setProfilePath(uri)
                setImageName(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        joinMainBinding = FragmentJoinMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        joinViewModel = ViewModelProvider(this)[JoinViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        joinViewModel.run {

            // 아이디 hint, Error message update
            idFieldState.observe(requireActivity()) {
                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainId, it)
            }

            // 비밀번호 hint, Error meesage update
            pwFieldState.observe(requireActivity()) {
                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainPw, it)
            }

            // 비밀번호 hint, Error meesage update
            confirmPwFieldState.observe(requireActivity()) {
                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainConfirmPw, it)
            }

            // 이름 Error meesage update
            nmFieldState.observe(requireActivity()) {
                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainNm, it)
            }

            // 닉네임 hint, Error meesage update
            nickNmFieldState.observe(requireActivity()) {
                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainNickNm, it)
            }

            // 이메일 Error meesage update
            emailFieldState.observe(requireActivity()) {
                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainEmail, it)
            }

            // 예산 hint, error message update
            budgetFieldState.observe(requireActivity()) {
                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainBudget, it)
            }

            joinFieldState.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        // 회원가입 성공 후 LoginMainFragment로 이동
                        Toast.makeText(requireContext(), "${it.data}", Toast.LENGTH_SHORT).show()
                        mainActivity.removeFragment(MainActivity.JOIN_MAIN_FRAGMENT)
                        mainActivity.replaceFragment(MainActivity.LOGIN_MAIN_FRAGMENT,false,null)
                    }

                    is FieldState.Fail -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    is FieldState.Error -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            unCheckedField.observe(requireActivity()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

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
//                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainVerificationNumber, it)

                val textInputLayout = joinMainBinding.textInputLayoutJoinMainVerificationNumber
                when (it) {
                    is FieldState.Success -> {
                        val helperMessage = it.data.toString()
                        textInputLayout.error = null
                        textInputLayout.isErrorEnabled = false
                        textInputLayout.helperText = helperMessage

                        // 핸드폰 인증 성공
                        // UserViewModel로 전화번호 전송
                        val phoneNumber = joinMainBinding.textInputEditTextJoinMainPNumber.text.toString()
                        joinViewModel.setCheckPhoneNumber(phoneNumber)
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
                imageLoadLauncher.launch("image/*")
            }
            
            // 아이디 내용 변경 시 아이디 중복 확인 통과한 거 통과 실패로 변경
            textInputEditTextJoinMainId.addTextChangedListener(CommonTextWatcher {
                joinViewModel.idChanged()
            })


            // 아이디 중복확인 클릭
            buttonJoinMainCheckIdDuplication.setOnClickListener {
                val id = textInputEditTextJoinMainId.text.toString()
                joinViewModel.checkIdDuplication(id)
            }

            // 비밀번호 유효성 검사
            textInputEditTextJoinMainPw.addTextChangedListener(CommonTextWatcher {
                val password = textInputEditTextJoinMainPw.text.toString()
                val confirmPassword = textInputEditTextJoinMainConfirmPw.text.toString()

                joinViewModel.checkPasswordValidate(password)

                // 비밀번호 확인란에 텍스트가 존재할 때
                if(confirmPassword.isNotEmpty()) {
                    joinViewModel.checkConfirmPasswordValidate(confirmPassword)
                }
            })

            // 비밀번호 같은지 검사
            textInputEditTextJoinMainConfirmPw.addTextChangedListener(CommonTextWatcher {
                val confirmPassword = textInputEditTextJoinMainConfirmPw.text.toString()
                joinViewModel.checkConfirmPasswordValidate(confirmPassword)
            })

            // 이름 유효성 검사
            textInputEditTextJoinMainNm.addTextChangedListener(CommonTextWatcher {
                val name = textInputEditTextJoinMainNm.text.toString()
                joinViewModel.checkNmValidate(name)
            })

            // 닉네임 내용 변경 시 닉네임 중복 확인 통과한 거 통과 실패로 변경
            textInputEditTextJoinMainNickNm.addTextChangedListener(CommonTextWatcher {
                joinViewModel.nicknameChanged()
            })

            // 닉네임 중복 확인 클릭
            buttonJoinMainCheckNickNmDuplication.setOnClickListener {
                val nickNm = textInputEditTextJoinMainNickNm.text.toString()
                joinViewModel.checkNickNmValidate(nickNm)
            }

            // 전화번호 텍스트 변경
            textInputEditTextJoinMainPNumber.addTextChangedListener(CommonTextWatcher {
                joinViewModel.phoneNumberChanged()
            })

            // 전화번호로 인증 문자 전송 버튼 클릭
            buttonJoinMainSendVerificationNumber.setOnClickListener {
                val phoneNumber = textInputEditTextJoinMainPNumber.text.toString()

                authViewModel.sendVerificationNumber("$phoneNumber", requireActivity())
            }

            // 인증번호 확인 클릭
            buttonJoinMainCheckVerificationNumber.setOnClickListener {
                // TODO : 인증번호 확인 기능 구현
                val code = textInputEditTextJoinMainVerificationNumber.text.toString()

                authViewModel.checkVerificationNumber(code)

            }

            // 성별 선택
            radioGroupJoinMainGender.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    radioButtonJoinMainMale.id -> {
                        joinViewModel.setGender('M')
                    }

                    radioButtonJoinMainFemale.id -> {
                        joinViewModel.setGender('F')
                    }
                }
            }

            // 출생년도 스피너 클릭
            setBirthYearSpinner()
            
            // 이메일 유효성 검사
            textInputEditTextJoinMainEmail.addTextChangedListener(CommonTextWatcher {
                val email = textInputEditTextJoinMainEmail.text.toString()
                joinViewModel.checkEmailValidate(email)
            })

            // 예산 유효성 검사
            textInputEditTextJoinMainBudget.addTextChangedListener(CommonTextWatcher {
                val budget = textInputEditTextJoinMainBudget.text.toString()
                joinViewModel.checkBudgetValidate(budget)
            })

            // 회원가입 클릭
            buttonJoinMainJoin.setOnClickListener {
                val auth = 'L'
                joinViewModel.join(auth, mainActivity)
            }
        }

        return joinMainBinding.root
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

    private fun setBirthYearSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear downTo 1900).map { it }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, years)

        joinMainBinding.spinnerJoinMainBirthYear.run {
            this.adapter = adapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedYear = years[position]
                    // ViewModel에 선택된 년도 전송
                    joinViewModel.setBirthYear(selectedYear!!)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedYear = null
                    joinViewModel.setBirthYear(null)
                }
            }
        }
    }

    // 이미지 이름 찾아서 설정
    private fun setImageName(uri: Uri) {
        val cursor: Cursor? = mainActivity.contentResolver.query(uri, null, null, null, null)
        try {
            cursor?.let {it
                if (it.moveToFirst()) {
                    val imageNameIdx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val imageName = it.getString(imageNameIdx)
                    joinViewModel.setProfileName(imageName)
                }
            }
        } finally {
            cursor?.close()
        }
    }
}