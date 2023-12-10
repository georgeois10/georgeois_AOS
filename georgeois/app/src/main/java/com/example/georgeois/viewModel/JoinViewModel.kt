package com.example.georgeois.viewModel

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.georgeois.dataclass.JoinUser
import com.example.georgeois.resource.FieldState
import com.example.georgeois.repository.UserRepository
import com.example.georgeois.utill.CheckValidation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * CoroutineScope(Dispatchers.IO) 로 처리를 했지만 메모리 릭 가능성 존재
 * androidx.lifecycle:lifecycle-viewmodel-ktx 사용 할 지 검토 필요
 */
class JoinViewModel : ViewModel() {

    private val _idFieldState = MutableLiveData<FieldState<String>>()
    val idFieldState: LiveData<FieldState<String>> = _idFieldState

    private val _pwFieldState = MutableLiveData<FieldState<String>>()
    val pwFieldState: LiveData<FieldState<String>> = _pwFieldState

    private val _confirmPwFieldState = MutableLiveData<FieldState<String>>()
    val confirmPwFieldState: LiveData<FieldState<String>> = _confirmPwFieldState

    private val _nmFieldState = MutableLiveData<FieldState<String>>()
    val nmFieldState: LiveData<FieldState<String>> = _nmFieldState

    private val _nickNmFieldState = MutableLiveData<FieldState<String>>()
    val nickNmFieldState: LiveData<FieldState<String>> = _nickNmFieldState

    private val _isPhoneChecked = MutableLiveData<FieldState<String>>()

    private val _genderFieldState = MutableLiveData<FieldState<String>>(FieldState.Fail(""))

    private val _birthYearFieldState = MutableLiveData<FieldState<String>>(FieldState.Fail(""))

    private val _emailFieldState = MutableLiveData<FieldState<String>>()
    val emailFieldState: LiveData<FieldState<String>> = _emailFieldState

    private val _budgetFieldState = MutableLiveData<FieldState<String>>()
    val budgetFieldState: LiveData<FieldState<String>> = _budgetFieldState

    private val _joinFieldState = MutableLiveData<FieldState<String>>()
    val joinFieldState: LiveData<FieldState<String>> = _joinFieldState

    private val _unCheckedField = MutableLiveData<String>()
    val unCheckedField: LiveData<String> = _unCheckedField


    fun setProfilePath(uri: Uri?) {
        if (uri != null) {
            profileUri = uri
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
                    _idFieldState.postValue(FieldState.Success("* 사용 가능한 아이디입니다."))
                    checkedId = id
                } else {
                    _idFieldState.postValue(FieldState.Fail("* 이미 존재하는 아이디입니다."))
                }

            } catch (e: Exception) {
                Log.e("JoinViewModel.checkIdDuplication", "${e.printStackTrace()}")
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
                    _nickNmFieldState.postValue(FieldState.Success("* 사용가능한 닉네임입니다."))
                    checkedNickNm = nickNm
                } else {
                    _nickNmFieldState.postValue(FieldState.Fail("이미 존재하는 닉네임입니다."))
                }
            } catch (e: Exception) {
                Log.e("JoinViewModel.checkNickNmValidate", "${e.printStackTrace()}")
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
        checkedEmail = email
        _emailFieldState.value = FieldState.Success("")
    }

    fun checkBudgetValidate(budget: String) {
        if (!(CheckValidation.validateBudget(budget))) {
            _budgetFieldState.value = FieldState.Fail("예산은 1,000만원 이하로 설정해주세요.")
            return
        }
        myBudget = budget.toInt()
        _budgetFieldState.value = FieldState.Success("")
    }

    fun setProfileName(imageName: String) {
        profileName = imageName
    }

    fun setCheckPhoneNumber(phoneNumber: String) {
        checkedPhoneNumber = phoneNumber
        _isPhoneChecked.value = FieldState.Success("")
    }

    fun setBirthYear(year: Int?) {
        if (year == null) {
            _birthYearFieldState.value = FieldState.Fail("출생년도를 선택해 주세요.")
            return
        }
        selectedBirthYear = year
        _birthYearFieldState.value = FieldState.Success("")

    }

    fun setGender(gender: Char) {
        selectedGender = gender
        _genderFieldState.value = FieldState.Success("")
    }

    fun phoneNumberChanged() {
        _isPhoneChecked.value = FieldState.Fail("")
    }

    // 중복 확인 누르고 텍스트가 변경 되었을 때 FieldState.Fail 로 변경
    fun idChanged() {
        _idFieldState.value = FieldState.Fail("")
    }

    fun nicknameChanged() {
        _nickNmFieldState.value = FieldState.Fail("")
    }


    fun join(auth: Char, activity: Activity) {
        // 유효성 검사에 통과하지 못한 Field가 있는지 확인
        if (!checkAllFieldStateAvailable())
            return

        joinAuth = auth

        viewModelScope.launch {

            // 프로필 이미지  MultipartBody.Part로 변환
            val profileMultiPart = makeProfileMultipart(activity)

            if (profileMultiPart == null) {
                _joinFieldState.postValue(FieldState.Fail("이미지를 찾을수 없습니다."))
                return@launch
            }

            // 카카오와 네이버 계정이 중복 될 수 있기 때문에
            // authType 이니셜을 앞에 붙였다.

            val idRequestBody =
                if (joinAuth != 'L') {
                    checkedId!!.toRequestBody()
                } else {
                    "$joinAuth$checkedId".toRequestBody()

                }

            // profile 서버로 전송
            if (profileUri != null || profileName != null) {
                saveProfileImage(idRequestBody, profileMultiPart)
            } else {
                val joinUser = JoinUser(
                    auth,
                    "",
                    checkedId!!,
                    checkedPw!!,
                    checkedNm!!,
                    checkedNickNm!!,
                    checkedPhoneNumber!!,
                    selectedGender!!,
                    selectedBirthYear!!,
                    checkedEmail,
                    myBudget
                )

                Log.d("mytag", joinUser.toString())

                saveUser(joinUser)
            }

        }

    }

    /**
     * 유저 정보 저장
     */
    private fun saveUser(joinUser: JoinUser) {
        val result = UserRepository.join(joinUser)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                // 응답 실패 시
                if (!(response.isSuccessful)) {
                    Log.d("JoinViewModel.join", "서버 응답 실패 - ${response.message()}")
                    _joinFieldState.postValue(FieldState.Error("연결에 실패하였습니다."))
                    return
                }

                // 응답 성공 시
                val responseBody = response.body()?.string()
                val isSuccess = responseBody?.let { JSONObject(it) }

                // 가입 성공
                if(isSuccess?.get("result") == true) {
                    _joinFieldState.postValue(FieldState.Success("회원가입 되었습니다.\n로그인을 진행해 주세요."))
                    return
                }

                // 가입 실패 ( 응답은 성공하였지만 Insert 실패 )
                // reponse.body().string() 이 {"reuslt" : false} 상태
                _joinFieldState.postValue(FieldState.Fail("가입에 실패하였습니다. 다시 시도해주세요."))

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("JoinViewModel.join", "서버 연결 실패 - ${t.printStackTrace()}")
                _joinFieldState.postValue(FieldState.Error("연결에 실패하였습니다."))
            }

        })
    }

    /**
     * 이미지를 MultipartBody.Part로 변환
     */
    private fun makeProfileMultipart(activity: Activity): MultipartBody.Part? {
        val inputStream = activity.contentResolver.openInputStream(profileUri!!)
        if (inputStream == null) {
            _joinFieldState.postValue(FieldState.Fail("이미지를 찾을수 없습니다."))
            return null
        }

        val byteArray = ByteArray(inputStream.available())
        inputStream.read(byteArray)
        inputStream.close()

        val requestFile = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("profile", profileName, requestFile)
    }

    /**
     * 이미지 파일 서버에 저장 후 경로 return
     */
    private fun saveProfileImage(id: RequestBody, profileMultiPart: MultipartBody.Part) {
        val result = UserRepository.saveProfile(id, profileMultiPart)
        var profilePath: String? = null

        result.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                // 응답 실패 시
                if (!(response.isSuccessful)) {
                    Log.e("JoinViewModel-join-saveProfileImage", "서버 응답 실패 - ${response.message()}")
                    return
                }

                // 응답 성공 시
                val responseBody = response.body()?.string()
                val isSuccess = responseBody?.let { JSONObject(it) }

                // 가입 성공
                if (isSuccess?.get("result") == true) {
//                    Log.d("JoinViewModel-join-saveProfileImage", "성공 - ${isSuccess.get("content")}")
                    profilePath = isSuccess.get("path").toString()

                    val joinUser = JoinUser(
                        joinAuth,
                        profilePath!!,
                        checkedId!!,
                        checkedPw!!,
                        checkedNm!!,
                        checkedNickNm!!,
                        checkedPhoneNumber!!,
                        selectedGender!!,
                        selectedBirthYear!!,
                        checkedEmail,
                        myBudget
                    )

                    saveUser(joinUser)
                    return
                }

                // 가입 실패 ( 응답은 성공하였지만 Insert 실패
                // reponse.body().string() 이 {"reuslt" : false} 상태
                Log.e("JoinViewModel-join-saveProfileImage", "이미지 저장 실패 - ${isSuccess?.get("error")}")
                _joinFieldState.postValue(FieldState.Fail("가입에 실패하였습니다. 다시 시도해주세요."))
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("JoinViewModel-join-saveProfileImage", "서버 연결 실패 - ${t.printStackTrace()}")
            }
        })
    }


    // 모든 LiveData 의 value 가 유효성 검사 통과했는지 확인
    private fun checkAllFieldStateAvailable(): Boolean {
        // 모든 가입 필드 허용되었는지 확인하기 위한 변수 (이메일은 선택사항)

        val fieldStateMap = mapOf(
            _idFieldState to "아이디",
            _pwFieldState to "비밀번호",
            _confirmPwFieldState to "비밀번호 확인",
            _nmFieldState to "이름",
            _nickNmFieldState to "닉네임",
            _isPhoneChecked to "전화번호 본인인증",
            _genderFieldState to "성별",
            _birthYearFieldState to "출생년도",
            _budgetFieldState to "예산"
        )

        fieldStateMap.keys.map {field ->
            if (field.value !is FieldState.Success) {
                // 유효성 검사 통과 실패
                _unCheckedField.value = "${fieldStateMap[field]}(은)는 필수 항목 입니다."
                field.value = FieldState.Fail("필수 항목입니다.")
                return false
            }
        }

        return true
    }

    // -------------- 소셜 회원가입 -------------

    /**
     *  소셜 회원가입 시 사용될 함수
     */
    fun setSocialJoinId(id: String) {
        _idFieldState.value = FieldState.Success("")
        checkedId = id
    }

    fun setSocialJoinPw(pw: String) {
        _pwFieldState.value = FieldState.Success("")
        _confirmPwFieldState.value = FieldState.Success("")
        checkedPw = pw
    }

    private companion object {
        var joinAuth: Char = 'L'
        var profileUri: Uri? = null
        var profileName: String? = null
        var checkedId: String? = null
        var checkedPw: String? = null
        var isPwConfirmPassed: Boolean = false
        var checkedNm: String? = null
        var checkedNickNm: String? = null
        var checkedPhoneNumber: String? = null
        var selectedGender: Char? = null
        var selectedBirthYear: Int? = null
        var checkedEmail: String = ""
        var myBudget: Int = -1
    }

}