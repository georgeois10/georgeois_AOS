package com.example.georgeois.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.georgeois.dataclass.JoinUser
import com.example.georgeois.resource.FieldState
import com.example.georgeois.dataclass.User
import com.example.georgeois.repository.UserRepository
import com.example.georgeois.utill.CheckValidation
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    //-------------
    private val _phoneNumberFieldState = MutableLiveData<FieldState<Boolean>>()
    val phoneNumberFieldState: MutableLiveData<FieldState<Boolean>> = _phoneNumberFieldState

    private val _genderFieldState = MutableLiveData<FieldState<Boolean>>()
    val genderFieldState: MutableLiveData<FieldState<Boolean>> = _genderFieldState

    private val _birthYearFieldState = MutableLiveData<FieldState<Boolean>>()
    val birthYearFieldState: MutableLiveData<FieldState<Boolean>> = _birthYearFieldState
    //-------------

    private val _emailFieldState = MutableLiveData<FieldState<String>>()
    val emailFieldState: MutableLiveData<FieldState<String>> = _emailFieldState

    private val _joinFieldState = MutableLiveData<FieldState<String>>()
    val joinFieldState: MutableLiveData<FieldState<String>> = _joinFieldState


    init {
        _genderFieldState.value = FieldState.Fail("")
        _birthYearFieldState.value = FieldState.Fail("")
    }

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

            var mod_date: Date? = null


            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val cre_date = dateFormat.parse(cre_date_string)

            if (mod_date_string != "null") {
                mod_date = dateFormat.parse(mod_date_string)
            }

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
                cre_date,
                json.getString("cre_user"),
                mod_date,
                json.getString("mod_user"),
            )
            _user.postValue(user)

            this.cancel()
        }
    }


    // ------------ 회원 가입 ------------
    fun setProfilePath(uri: Uri?) {
        if (uri != null) {
            profile = uri.toString()
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

    fun checkPhoneNumberValidate(phoneNumber: String) {
        checkedPhoneNumber = phoneNumber
    }

    fun checkEmailValidate(email: String) {
        if (email.isNotEmpty() && !CheckValidation.validateEmail(email)) {
            _emailFieldState.value = FieldState.Fail("유효한 이메일 형식이 아닙니다.")
            return
        }
        checkedEmail = email
        _emailFieldState.value = FieldState.Success("")
    }

    fun setBirthYear(year: Int?) {
        if (year == null) {
            _birthYearFieldState.value = FieldState.Fail("출생년도를 선택해 주세요.")
            return
        }
        selectedBirthYear = year
        _birthYearFieldState.value = FieldState.Success(true)

    }

    fun setGender(gender: Char) {
        selectedGender = gender
        _genderFieldState.value = FieldState.Success(true)
    }

    fun localJoin(auth: Char) {
        // 유효성 검사에 통과하지 못한 Field가 있는지 확인
        if (!checkAllFieldStateAvailable())
            return

//        val joinUser = mutableMapOf<String, String>(
//            "auth" to auth.toString(),
//            "profile" to profile,
//            "id" to checkedId!!,
//            "pw" to checkedPw!!,
//            "nm" to checkedNm!!,
//            "nicknm" to checkedNickNm!!,
//            "pnumber" to checkedPhoneNumber!!,
//            "gender" to selectedGender!!.toString(),
//            "birth" to selectedBirthYear!!.toString(),
//            "email" to checkedEmail
//        )

        val joinUser = JoinUser(
            auth.toString(),
            profile,
            checkedId!!,
            checkedPw!!,
            checkedNm!!,
            checkedNickNm!!,
            checkedPhoneNumber!!,
            selectedGender!!.toString(),
            selectedBirthYear!!,
            checkedEmail
        )


        // 모든 Field가 유효성 검사 통과 시
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = UserRepository.join(joinUser)

                result.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        // 응답 실패 시
                        if (!(response.isSuccessful)) {
                            Log.d("UserViewModel.localJoin", "서버 응답 실패 - ${response.message()}")
                            _joinFieldState.postValue(FieldState.Error("연결에 실패하였습니다."))
                            return
                        }

                        // 응답 성공 시
                        val responseBody = response.body()?.string()
                        val isSuccess = responseBody?.let { JSONObject(it) }

                        // 가입 성공
                        if(isSuccess?.get("result") == true) {
                            Log.d("mytag- 성공", "${response.body()?.string()}")
                            _joinFieldState.postValue(FieldState.Success("회원가입 되었습니다.\n로그인을 진행해 주세요."))
                            return
                        }

                        // 가입 실패
                        // reponse.body().string() 이 {"reuslt" : false} 상태
                        _joinFieldState.postValue(FieldState.Fail("가입에 실패하였습니다. 다시 시도해주세요."))

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("UserViewModel.localJoin", "서버 연결 실패 - ${t.printStackTrace()}")
                        _joinFieldState.postValue(FieldState.Error("연결에 실패하였습니다."))
                    }

                })

            } catch (e: Exception) {
                Log.e("UserViewModel.localJoin", "${e.printStackTrace()}")
                _joinFieldState.postValue(FieldState.Error("가입 중 오류가 발생했습니다."))
            }

            this.cancel()
        }
    }

    // 모든 LiveData 의 value 가 유효성 검사 통과했는지 확인
    private fun checkAllFieldStateAvailable(): Boolean {
        // 모든 가입 필드 허용되었는지 확인하기 위한 변수 (이메일은 선택사항)
        val fieldStateList = arrayListOf(
            _idFieldState.value,
            _pwFieldState.value,
            _confirmPwFieldState.value,
            _nmFieldState.value,
            _nickNmFieldState.value,
            _genderFieldState.value,
            _birthYearFieldState.value,
        )

        fieldStateList.map { fieldState ->
            if (fieldState !is FieldState.Success) {
                // 유효성 검사 통과 실패
                Log.d("mytag-field", fieldState?.message.toString())
                return false
            }
        }

        return true
    }

    private companion object {
        var profile: String = ""
        var checkedId: String? = null
        var checkedPw: String? = null
        var isPwConfirmPassed: Boolean = false
        var checkedNm: String? = null
        var checkedNickNm: String? = null
        var checkedPhoneNumber: String? = null
        var selectedGender: Char? = null
        var selectedBirthYear: Int? = null
        var checkedEmail: String = ""
    }

}