package com.example.georgeois.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.georgeois.resource.FieldState
import com.example.georgeois.dataclass.User
import com.example.georgeois.repository.UserRepository
import com.example.georgeois.utill.CheckValidation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.sql.Timestamp

/**
 * CoroutineScope(Dispatchers.IO) 로 처리를 했지만 메모리 릭 가능성 존재
 * androidx.lifecycle:lifecycle-viewmodel-ktx 사용 할 지 검토 필요
 */
class UserViewModel : ViewModel() {
    private val _isIdAvailable = MutableLiveData<Boolean>(false)
    val isIdAvailable: MutableLiveData<Boolean> = _isIdAvailable

    private val _idFieldState = MutableLiveData<FieldState<String>>()
    val idFieldState: MutableLiveData<FieldState<String>> = _idFieldState

    private val _pwFieldState = MutableLiveData<FieldState<String>>()
    val pwFieldState: MutableLiveData<FieldState<String>> = _pwFieldState

    private val _confirmPwFieldState = MutableLiveData<FieldState<String>>()
    val confirmPwFieldState: MutableLiveData<FieldState<String>> = _confirmPwFieldState

    private val _nmFieldState = MutableLiveData<FieldState<String>>()
    val nmFieldState: MutableLiveData<FieldState<String>> = _nmFieldState

    private val _nickNmFieldState = MutableLiveData<FieldState<String>>()
    val nickNmFieldState: MutableLiveData<FieldState<String>> = _nickNmFieldState

    private val _emailFieldState = MutableLiveData<FieldState<String>>()
    val emailFieldState: MutableLiveData<FieldState<String>> = _emailFieldState

    private val _failedFieldState = MutableLiveData<FieldState<String>>()
    val failedFieldState: MutableLiveData<FieldState<String>> = _failedFieldState

//    init {
//        _user.value = User(
//            0,
//            'x',
//            "",
//            "",
//            "",
//            "",
//            "",
//            "",
//            0,
//            "",
//            "",
//            "",
//            "",
//            0,
//            false,
//            false,
//            Timestamp(System.currentTimeMillis()),
//            "",
//            Timestamp(System.currentTimeMillis()),
//            "",
//        )
//    }

    fun checkIdDuplication(id: String) {

        // 유효성 검사 통과 실패 시
        if (!(CheckValidation.validateId(id))) {
            _idFieldState.value = FieldState.Fail("* 5~20자의 하나 이상의 영어 소문자, 숫자 조합이어야 합니다.")
            return
        }

        // 검사 통과 시
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = UserRepository.checkIdDuplication(id)
                if (result["result"] == true) {
                    // Background 에서 동작 되기 때문에 postValue() 사용함.
                    _idFieldState.postValue(FieldState.Success("사용 가능한 아이디입니다."))
//                    _idFieldState.value = FieldState.Success("사용 가능한 아이디입니다.")
                    checkedId = id
                } else {
                    _idFieldState.postValue(FieldState.Fail("이미 존재하는 아이디입니다."))
//                    _idFieldState.value = FieldState.Fail("이미 존재하는 아이디입니다.")
                }

            } catch (e: Exception) {
                Log.e("UserViewModel.checkIdDuplication", "${e.printStackTrace()}")
//                _idFieldState.value = FieldState.Error(e.message.toString())
                _idFieldState.postValue(FieldState.Error(e.message.toString()))
            }

            this.cancel()
        }

    }

    fun checkPasswordValidate(password: String) {
        // 유효성 검사 통과 실패 시
        if (!(CheckValidation.validatePassword(password))) {
            _pwFieldState.value = FieldState.Fail("* 8~16사이의 숫자, 영어 소문자, 특수문자의 조합이어야 합니다.\n* 사용 가능한 특수문자( $@!%*#?& )")
            return
        }
        
        // 검사 통과 시
        _pwFieldState.value = FieldState.Success("* 사용 가능한 비밀번호 입니다.")
        checkedPw = password
    }


    fun checkConfirmPasswordValidate(confirmPassword: String) {
        if (checkedPw != null) {

            if (!(CheckValidation.validateConfirmPassword(checkedPw!!, confirmPassword))) {
                _confirmPwFieldState.value = FieldState.Fail("* 비밀번호가 일치하지 않습니다.")
                isPwConfirmPassed = false
                return
            }

            _confirmPwFieldState.value = FieldState.Success("* 비밀번호가 일치합니다.")
            isPwConfirmPassed = true

        }
    }

    fun checkNmValidate(nm: String) {
        if (!(CheckValidation.validateName(nm))) {
            _nmFieldState.value = FieldState.Fail("* 유효하지 않은 문자가 포함되어 있습니다.")
            return
        }

        _nmFieldState.value = FieldState.Success("")
        checkedNm = nm
    }



    fun checkNickNmValidate(nickNm: String) {
        if (!(CheckValidation.validateNickName(nickNm))) {
            _nickNmFieldState.value = FieldState.Fail("* 5~20자의 하나 이상의 영어 소문자 또는 한글, 숫자 조합이어야 합니다.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = UserRepository.checkNickNmDuplication(nickNm)

                if (result["result"] == true) {
//                    _nickNmFieldState.value = FieldState.Success("* 사용가능한 닉네임입니다.")
                    _nickNmFieldState.postValue(FieldState.Success("* 사용가능한 닉네임입니다."))
                    checkedNickNm = nickNm
                } else {
//                    _idFieldState.postValue(FieldState.Fail("이미 존재하는 아이디입니다."))
                    _nickNmFieldState.postValue(FieldState.Fail("이미 존재하는 닉네임입니다."))
                }
            } catch (e: Exception) {
                Log.e("UserViewModel.checkNickNmValidate", "${e.printStackTrace()}")
//                _nickNmFieldState.value = FieldState.Error(e.message.toString())
                _nickNmFieldState.postValue(FieldState.Error(e.message.toString()))
            }

            this.cancel()
        }



    }

    fun checkEmailValidate(email: String) {
        if (email.isNotEmpty() && !CheckValidation.validateEmail(email)) {
            _emailFieldState.value = FieldState.Fail("유효한 이메일 형식이 아닙니다.")
            return
        }

        _emailFieldState.value = FieldState.Success("")
        checkedEmail = email
    }

    fun setBirthYear(year: Int?) {
        selectedBirthYear = year
    }

    fun setGender(gender: Char) {
        selectedGender = gender
    }

    fun localJoin(user: User) {
        // 유효성 검사에 통과하지 못한 Field가 있는지 확인
        if (!checkAllFieldStateAvailable())
            return

        val user = User(
            -1,
            auth!!,
            checkedId!!,
            checkedPw!!,
            checkedNm!!,
            checkedNickNm!!,
            "",
            selectedGender!!,
            selectedBirthYear!!,
            checkedEmail,
            "",
            "",
            "",
            0,
            false,
            false,
            Timestamp(System.currentTimeMillis()),
            checkedId!!,
            Timestamp(System.currentTimeMillis()),
            checkedId!!,
        )

        // 모든 Field가 유효성 검사 통과 시
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = UserRepository.requestJoin(user)

            } catch (e: Exception) {
                Log.e("UserViewModel.localJoin", "${e.printStackTrace()}")
            }
        }
    }

    private fun checkAllFieldStateAvailable(): Boolean {
        val fieldStateList = arrayListOf(
            _idFieldState.value,
            _pwFieldState.value,
            _confirmPwFieldState.value,
            _nmFieldState.value,
            _nickNmFieldState.value,
            _emailFieldState.value
        )

        fieldStateList.map { fieldState ->
            if (fieldState !is FieldState.Success) {
                // 유효성 검사 통과 실패

                return false
            }
        }

        return true
    }

    private companion object {
        var auth: Char? = null
        var checkedId: String? = null
        var checkedPw: String? = null
        var isPwConfirmPassed: Boolean = false
        var checkedNm: String? = null
        var checkedNickNm: String? = null
        var selectedGender: Char? = null
        var selectedBirthYear: Int? = null
        var checkedEmail: String = ""
    }

}