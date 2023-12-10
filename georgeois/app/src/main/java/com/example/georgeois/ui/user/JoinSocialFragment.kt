package com.example.georgeois.ui.user

import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentJoinSocialBinding
import com.example.georgeois.databinding.LayoutEditBudgetBinding
import com.example.georgeois.module.RetrofitModule
import com.example.georgeois.repository.UserRepository
import com.example.georgeois.resource.FieldState
import com.example.georgeois.service.UserApiService
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.CommonTextWatcher
import com.example.georgeois.viewModel.JoinViewModel
import com.example.georgeois.viewModel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.kakao.sdk.common.util.Utility
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlin.concurrent.thread

class JoinSocialFragment : Fragment() {
    private lateinit var joinSocialBinding: FragmentJoinSocialBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var joinViewModel: JoinViewModel
    private var selectedYear: Int? = null
    private val imageLoadLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // 프로필 이미지 이름 설정
            if (uri != null) {
                joinSocialBinding.circleImageViewJoinSocialProfile.setImageURI(uri)
                joinViewModel = ViewModelProvider(this)[JoinViewModel::class.java]
                joinViewModel.setProfilePath(uri)
                setImageName(uri)
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        joinSocialBinding = FragmentJoinSocialBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        joinViewModel = ViewModelProvider(this)[JoinViewModel::class.java]

        joinViewModel.run {

            // 이름 Error meesage update
            nmFieldState.observe(requireActivity()) {
                updateValidateMessage(joinSocialBinding.textInputLayoutJoinSocialNm, it)
            }

            // 닉네임 hint, Error meesage update
            nickNmFieldState.observe(requireActivity()) {
                updateValidateMessage(joinSocialBinding.textInputLayoutJoinSocialNickNm, it)
            }

            // 이메일 Error meesage update
            emailFieldState.observe(requireActivity()) {
                updateValidateMessage(joinSocialBinding.textInputLayoutJoinSocialEmail, it)
            }

            // 예산 hint Error message update
            budgetFieldState.observe(requireActivity()) {
                updateValidateMessage(joinSocialBinding.textInputLayoutJoinSocialBudget, it)
            }

            joinFieldState.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        // 회원가입 성공 후 LoginMainFragment 이동
                        Toast.makeText(requireContext(), "${it.data}", Toast.LENGTH_SHORT).show()
                        mainActivity.removeFragment(MainActivity.JOIN_SOCIAL_FRAGMENT)
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

        joinSocialBinding.run {
            setUserInfoFromBundle()

            // 툴바
            materialToolbarJoinSocial.run {
                title = "회원가입"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.JOIN_SOCIAL_FRAGMENT)
                }
            }

            // profile 이미지 클릭
            circleImageViewJoinSocialProfile.setOnClickListener {
                imageLoadLauncher.launch("image/*")
            }

            // 이름 유효성 검사
            textInputEditTextJoinSocialNm.addTextChangedListener(CommonTextWatcher {
                val name = textInputEditTextJoinSocialNm.text.toString()
                joinViewModel.checkNmValidate(name)
            })


            // 닉네임 내용 변경 시 닉네임 중복 확인 통과한 거 통과 실패로 변경
            textInputEditTextJoinSocialNickNm.addTextChangedListener(CommonTextWatcher {
                joinViewModel.nicknameChanged()
            })

            // 닉네임 중복 확인 클릭
            buttonJoinSocialCheckNickNmDuplication.setOnClickListener {
                val nickNm = textInputEditTextJoinSocialNickNm.text.toString()
                joinViewModel.checkNickNmValidate(nickNm)
            }

            // 성별 선택
            radioGroupJoinSocialGender.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    // 남성 선택
                    radioButtonJoinSocialMale.id -> {
                        joinViewModel.setGender('M')
                    }
                    // 여성 선택
                    radioButtonJoinSocialFemale.id -> {
                        joinViewModel.setGender('F')
                    }

                }
            }

            // 출생년도 스피너 클릭
            setBirthYearSpinner()

            // 이메일 유효성 검사
            textInputEditTextJoinSocialEmail.addTextChangedListener(CommonTextWatcher {
                val email = textInputEditTextJoinSocialEmail.text.toString()
                joinViewModel.checkEmailValidate(email)
            })

            // 예산 유효성 검사
            textInputEditTextJoinSocialBudget.addTextChangedListener(CommonTextWatcher {
                val budget = textInputEditTextJoinSocialBudget.text.toString()
                if (budget != "") {
                    joinViewModel.checkBudgetValidate(budget)
                }
            })

            // 회원가입 클릭
            buttonJoinSocialJoin.setOnClickListener {
                val authType = arguments?.getChar("authType")

                if (authType != null) {
                    joinViewModel.join(authType, mainActivity)
                }
            }

        }

        return joinSocialBinding.root
    }

    /**
     * bundle 로 받아온 유저 정보가 있다면 화면에 보여줌
     * 출생년도는 [setBirthYearSpinner] 에 설정되어 있음.
     */
    private fun setUserInfoFromBundle() {
        // 이름 설정
        val name = arguments?.getString("name")

        if (name != null) {
            joinSocialBinding.textInputEditTextJoinSocialNm.setText(name)
            joinViewModel.checkNmValidate(name)
        }

        // 성별 설정
        val gender = arguments?.getString("gender")?.toCharArray()?.get(0)

        if (gender != null) {
            joinViewModel.setGender(gender)
            when (gender) {
                'M' -> joinSocialBinding.radioButtonJoinSocialMale.isChecked = true
                'F' -> joinSocialBinding.radioButtonJoinSocialFemale.isChecked = true
                else -> Unit
            }
        }

        // 휴대전화 설정 - 화면엔 존재x
        val phoneNumber = arguments?.getString("phoneNumber")

        if (phoneNumber != null) {
            joinViewModel.setCheckPhoneNumber(phoneNumber)
        }

        // 아이디 설정 - 화면엔 존재x
        val id = arguments?.getString("id")

        if (id != null) {
            joinViewModel.setSocialJoinId(id)
        }

        // 비밀번호 설정 - 화면엔 존재x
        val pw = arguments?.getString("primaryId")

        if (pw != null) {
            joinViewModel.setSocialJoinPw(pw.substring(0,8))
        }
    }

//    private fun showInitBudgetDialog() {
//        val dialogBinding = LayoutEditBudgetBinding.inflate(layoutInflater)
//        val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
//                val customTitle = setCustomTitle("예산변경(최대 1,000만원)")
//                setCustomTitle(customTitle)
//            }
//
//        builder.setView(dialogBinding.root)
//
//        val inputBudget = dialogBinding.editTextInputBudget
//        inputBudget.requestFocus()
//
//        thread {
//            inputBudget.post {
//                val length = inputBudget.text.length
//                inputBudget.setSelection(length)
//                val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.showSoftInput(inputBudget, InputMethodManager.SHOW_IMPLICIT)
//            }
//        }
//
//        builder.setNegativeButton("취소", null)
//        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
//            val inputText = inputBudget.text.toString()
//
//            if(inputText.isEmpty()){
//                Toast.makeText(context, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show()
//            } else {
//                try {
//                    val budget = inputText.toInt()
//
//                    if (budget in 0..10000000) {
//                        joinViewModel.setBudget(budget)
//                    } else {
//                        Toast.makeText(context, "예산은 1,000만원 아래로 설정해주세요.", Toast.LENGTH_SHORT).show()
//                    }
//                } catch (_: Exception) {
//                    Toast.makeText(context, "예산은 1,000만원 아래로 설정해주세요.", Toast.LENGTH_SHORT).show()
//                }
//
//
//            }
//        }
//
//        builder.show()
//    }
//
//    fun setCustomTitle(title: String): TextView {
//        val customTitle = TextView(context).apply {
//            text = title  // 타이틀 텍스트 설정
//            textSize = 25f  // 텍스트 사이즈 설정
//            typeface = ResourcesCompat.getFont(context, R.font.space)  // 글꼴 스타일 설정
//            setTextColor(Color.BLACK)  // 텍스트 색상 설정
//            setPadding(100, 100, 0, 20)  // 패딩 설정 (단위: px)
//        }
//        return customTitle
//    }
//
//    fun formatNumberWithCommas(number: Int): String {
//        val formatter = NumberFormat.getNumberInstance(Locale.US)
//        return formatter.format(number)
//    }

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
        val birthYear = arguments?.getString("birthYear")?.toInt()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear downTo 1900).map { it }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, years)

        joinSocialBinding.spinnerJoinSocialBirthYear.run {
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

            // 소셜 로그인 시 전달받은 출생년도가 있다면 스피너 설정
            if (birthYear != null) {
                joinViewModel.setBirthYear(birthYear)
                setSelection(years.indexOf(birthYear))
            }
        }
    }

}