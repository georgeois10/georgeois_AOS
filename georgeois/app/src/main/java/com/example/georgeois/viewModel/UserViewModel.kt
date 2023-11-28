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
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * CoroutineScope(Dispatchers.IO) 로 처리를 했지만 메모리 릭 가능성 존재
 * androidx.lifecycle:lifecycle-viewmodel-ktx 사용 할 지 검토 필요
 */
class UserViewModel : ViewModel() {


    // --------- 로그인 ----------
    private val _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> = _user

    // --------- 회원가입 ---------
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



    // --------- 로그인 ----------
    /**
     * 코드 수정할 예정
     */
    fun login(id: String, pw: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = UserRepository.login(id, pw)
            val userMap = result["user"]

            if (userMap.isNullOrEmpty()) {
                _user.postValue(null)
                return@launch
            }

            val json = JSONObject(userMap)

            val cre_date_string = json.getString("cre_date")
            val mod_date_string = json.getString("mod_date")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val cre_date = dateFormat.parse(cre_date_string)
            val mod_date = dateFormat.parse(mod_date_string)


            val user = User(
                json.getInt("u_idx"),
                json.getString("u_auth").toCharArray().get(0),
                json.getString("u_id"),
                json.getString("u_pw"),
                json.getString("u_nm"),
                json.getString("u_nicknm"),
                json.getString("u_pnumber"),
                json.getString("u_gender").toCharArray().get(0),
                json.getInt("u_birth"),
                json.getString("u_email"),
                json.getString("u_profilepath"),
                json.getString("u_in_ctgy"),
                json.getString("u_out_ctgy"),
                json.getInt("u_budget"),
                json.getDouble("u_alarm_yn").toInt() == 1,
                json.getDouble("del_yn").toInt() == 1,
                cre_date!!,
                json.getString("cre_user"),
                mod_date!!,
                json.getString("mod_user"),
            )
            _user.postValue(user)

//            Log.d("mytag", user.toString())

//            val cre_date = Date(0L)
//            val mod_date = Date(0L)
//
//            val user = User(
//                userMap["u_idx"] as Int,
//                userMap["u_auth"] as Char,
//                userMap["u_id"] as String,
//                userMap["u_pw"] as String,
//                userMap["u_nm"] as String,
//                userMap["u_nicknm"] as String,
//                userMap["u_pnumber"] as String,
//                userMap["u_gender"] as Char,
//                userMap["u_birth"] as Int,
//                userMap["u_email"] as String,
//                userMap["u_profilepath"] as String,
//                userMap["u_in_ctgy"] as String,
//                userMap["u_out_ctgy"] as String,
//                userMap["u_budget"] as Int,
//                userMap["u_alarm_yn"] as Boolean,
//                userMap["del_yn"] as Boolean,
//                cre_date,
//                userMap["cre_user"] as String,
//                mod_date,
//                userMap["mod_user"] as String,
//            )
            this.cancel()
        }
    }

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

//        val user = User(
//            -1,
//            auth!!,
//            checkedId!!,
//            checkedPw!!,
//            checkedNm!!,
//            checkedNickNm!!,
//            "",
//            selectedGender!!,
//            selectedBirthYear!!,
//            checkedEmail,
//            "",
//            "",
//            "",
//            0,
//            0,
//            0,
//            Timestamp(System.currentTimeMillis()),
//            checkedId!!,
//            Timestamp(System.currentTimeMillis()),
//            checkedId!!,
//        )

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