package com.example.georgeois.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.georgeois.BuildConfig
import com.example.georgeois.resource.FieldState
import com.example.georgeois.dataclass.User
import com.example.georgeois.repository.UserRepository
import com.example.georgeois.resource.FindUserResponse
import com.example.georgeois.resource.SocialLoginType
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.CheckValidation
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * CoroutineScope(Dispatchers.IO) 로 처리를 했지만 메모리 릭 가능성 존재
 * androidx.lifecycle:lifecycle-viewmodel-ktx 사용 할 지 검토 필요
 */
class UserViewModel : ViewModel() {

    // --------- 유저 ----------
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _loginSuccessState = MutableLiveData<FieldState<SocialLoginType?>>()
    val loginSuccessState: LiveData<FieldState<SocialLoginType?>> = _loginSuccessState

    private val _findIdList = MutableLiveData<FieldState<List<Map<String, String>>>>()
    val findIdList: LiveData<FieldState<List<Map<String, String>>>> = _findIdList

    private val _pwFieldState = MutableLiveData<FieldState<String>>()
    val pwFieldState: LiveData<FieldState<String>> = _pwFieldState

    private val _confirmPwFieldState = MutableLiveData<FieldState<String>>()
    val confirmPwFieldState: LiveData<FieldState<String>> = _confirmPwFieldState

    private val _resetPwState = MutableLiveData<FieldState<Boolean>>()
    val resetPwState: LiveData<FieldState<Boolean>> = _resetPwState


    // --------- 로컬 로그인 ----------
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


    // --------- 네이버 로그인 ----------
    fun naverLogin(mainActivity: MainActivity) {
        // TODO : 네이버 로그인 화면으로 이동 구현
        NaverIdLoginSDK.initialize(mainActivity,
            BuildConfig.NAVER_CLIENT_ID,
            BuildConfig.NAVER_CLIENT_SECRET,
            BuildConfig.NAVER_APP_NAME
        )

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(nidProfileCallback)
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()

                Log.d("LoginViewModel-naverLogin-OAuthLoginCallback", "errorCode:$errorCode, errorDesc:$errorDescription")
                _loginSuccessState.value = FieldState.Fail("$errorDescription")
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)

                Log.d("LoginViewModel-naverLogin-OAuthLoginCallback", "errorCode:$errorCode, message:$message")
                _loginSuccessState.value = FieldState.Error("오류가 발생했습니다. 다시 시도해주세요.")
            }
        }

        NaverIdLoginSDK.authenticate(mainActivity, oauthLoginCallback)
    }

    /**
     * 유저 정보를 가져올때 사용될 Callback
     * [naverLogin] 에서 OAuthLoginCallback onSuccess 일 때 사용 될 Callback
     */
    private val nidProfileCallback = object : NidProfileCallback<NidProfileResponse> {
        override fun onError(errorCode: Int, message: String) {
            Log.d("LoginViewModel-naverLogin-nidProfileCallback", "errorCode:$errorCode, message:$message")
            _loginSuccessState.value = FieldState.Error("오류가 발생했습니다. 다시 시도해주세요.")
        }

        override fun onFailure(httpStatus: Int, message: String) {
            Log.d("LoginViewModel-naverLogin-nidProfileCallback", "httpStatus:$httpStatus, errorDesc:$message")
            _loginSuccessState.value = FieldState.Fail(message)
        }

        override fun onSuccess(result: NidProfileResponse) {
            val profile = result.profile

            if (profile == null) {
                _loginSuccessState.value = FieldState.Fail("사용자 정보를 불러올 수 없습니다.")
                return
            }

            val email = profile.email

            // 이미 존재하는지 검사
            viewModelScope.launch {
                if (email == null) {
                    _loginSuccessState.postValue(FieldState.Error("이메일 정보를 불러올 수 없습니다."))
                    return@launch
                }

                try {
                    val isAvailable = UserRepository.checkIdDuplication(email)

                    when (isAvailable["result"]) {
                        // 계정이 존재하지 않을 때
                        true -> _loginSuccessState.postValue(FieldState.Success(SocialLoginType.NaverUser(profile)))
                        // 계정이 존재할 때
                        false -> {
                            _loginSuccessState.postValue(FieldState.Success(null))
                            val primaryId = profile.id
                            // TODO : 로그인 성공시 _user 에 유저 데이터 넣어줘야 함
                            login(email, primaryId!!.substring(0, 8))
                        }

                        // 서버에선 true 아니면 false 만 반환하게 되어있음
                        else -> _loginSuccessState.postValue(FieldState.Fail("데이터를 조회할 수 없습니다."))
                    }

                } catch (e: Exception) {
                    Log.e("LoginViewModel.checkIdDuplication", "${e.printStackTrace()}")
                    _loginSuccessState.postValue(FieldState.Error("오류가 발생했습니다. 다시 시도해주세요."))
                }
            }
        }

    }


    // ------- 카카오 로그인 -------

    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    private val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            // 카카오계정으로 로그인 실패
            _loginSuccessState.value = FieldState.Error("로그인에 실패하였습니다.")
        } else if (token != null) {
            // 카카오계정으로 로그인 성공
            getKakaoAccountInfo()
        }
    }

    fun kakaoLogin(mainActivity: MainActivity) {
        KakaoSdk.init(mainActivity, BuildConfig.KAKAO_NATIVE_KEY)

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(mainActivity) { token, error ->
                if (error != null) {
                    // 카카오톡으로 로그인 실패

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = kakaoCallback)
                } else if (token != null) {
                    // 카카오계정으로 로그인 성공
                    getKakaoAccountInfo()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = kakaoCallback)
        }

    }

    private fun getKakaoAccountInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("UserViewModel-getKakaoAccountInfo", "$error")
                _loginSuccessState.value = FieldState.Error("로그인에 실패하였습니다.")
            } else if (user != null) {
                // 사용자 정보 요청 성공

                // 카카오 계정이 이메일이 아닐때 전화번호를 id로 설정
                val kakaoId = user.kakaoAccount?.email ?: user.kakaoAccount?.phoneNumber

                // 이미 존재하는 계정인지 확인
                viewModelScope.launch {
                    if (kakaoId == null) {
                        _loginSuccessState.postValue(FieldState.Error("정보를 불러올 수 없습니다."))
                        return@launch
                    }

                    try {
                        val isAvailable = UserRepository.checkIdDuplication(kakaoId)

                        when (isAvailable["result"]) {
                            // 계정이 존재하지 않을 때
                            true -> _loginSuccessState.postValue(FieldState.Success(SocialLoginType.KakaoUser(user)))

                            // 계정이 존재할 때
                            false -> {
                                _loginSuccessState.postValue(FieldState.Success(null))

                                val primaryId = user.id
                                // TODO : 로그인 성공시 _user 에 유저 데이터 넣어줘야 함
                                login(kakaoId, primaryId.toString().substring(0, 8))
                            }

                            // 서버에선 true 아니면 false 만 반환하게 되어있음
                            else -> _loginSuccessState.postValue(FieldState.Fail("데이터를 조회할 수 없습니다."))
                        }

                    } catch (e: Exception) {
                        Log.e("LoginViewModel.checkIdDuplication", "${e.printStackTrace()}")
                        _loginSuccessState.postValue(FieldState.Error("오류가 발생했습니다. 다시 시도해주세요."))
                    }
                }
            }
        }
    }

    /**
     * 소셜 로그인 후 소셜 회원가입으로 이동하고
     * 뒤로 돌아가기 클릭 시를 대비해 loginSuccessState.value = null 로 변경
     *
     * 이 함수를 사용하지 않으면 observer가 Success로 떠서 뒤로 돌아가도 회원가입 페이지로 이동됨
     */
    fun clearLoginSuccessState() {
        _loginSuccessState.value = null
    }



    // -------- 아이디, 비밀번호 찾기 ---------
    fun findIdByPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {

            val result = UserRepository.findIdByPhoneNumber(phoneNumber)

            result.enqueue(object : Callback<FindUserResponse> {
                override fun onResponse(
                    call: Call<FindUserResponse>,
                    response: Response<FindUserResponse>
                ) {
                    // 응답 실패 시
                    if (!(response.isSuccessful)) {
                        Log.e("UserViewModel-findIdByPhoneNumber", "서버 응답 실패 - ${response.message()}")
                        _findIdList.postValue(FieldState.Error("서버 연결에 실패하였습니다."))
                        return
                    }

                    // 응답 성공 시
                    if (response.body()?.result == true) {
                        // 조회 성공
                        val ids = response.body()?.user
                        _findIdList.postValue(FieldState.Success(ids))

                        return
                    }

                    // 조회 실패 ( 응답은 성공하였지만 select 실패 )
                    // 가입된 계정 없음
                    _findIdList.postValue(FieldState.Success(null))
                    return
                }

                override fun onFailure(call: Call<FindUserResponse>, t: Throwable) {
                    Log.e("UserViewModel-findIdByPhoneNumber", "서버 연결 실패 - ${t.printStackTrace()}")
                    _findIdList.postValue(FieldState.Error("서버 연결에 실패하였습니다."))
                }
            })
        }
    }


    // 소셜 회원가입한 경우는 반환되지 않음
    fun findByIdAndPhoneNumber(id: String, phoneNumber: String) {
        if (!(CheckValidation.validateId(id))) {
            _findIdList.postValue(FieldState.Fail("잘못된 아이디입니다.\n소셜 회원가입인 경우는 소셜사이트에서 비밀번호를 변경해주세요."))
            return
        }
        val result = UserRepository.findByIdAndPhoneNumber(id, phoneNumber)

        result.enqueue(object : Callback<FindUserResponse> {
            override fun onResponse(
                call: Call<FindUserResponse>,
                response: Response<FindUserResponse>
            ) {
                // 응답 실패 시
                if (!(response.isSuccessful)) {
                    Log.e("UserViewModel-findByIdAndPhoneNumber", "서버 응답 실패 - ${response.message()}")
                    _findIdList.postValue(FieldState.Error("서버 연결에 실패하였습니다."))
                    return
                }

                // 응답 성공 시
                if (response.body()?.result == true) {
                    // 조회 성공
                    val user = response.body()?.user
                    _findIdList.postValue(FieldState.Success(user))

                    return
                }
                Log.d("mytag--", "${response.body().toString()}")

                // 조회 실패 ( 응답은 성공하였지만 select 실패 )
                // 가입된 계정 없음
                _findIdList.postValue(FieldState.Success(null))
                return
            }

            override fun onFailure(call: Call<FindUserResponse>, t: Throwable) {
                Log.e("UserViewModel-findByIdAndPhoneNumber", "서버 연결 실패 - ${t.printStackTrace()}")
                _findIdList.postValue(FieldState.Error("서버 연결에 실패하였습니다."))
            }
        })
    }


    // --------- 비밀번호 재설정 ---------
    fun resetPassword(idx: String, nickname: String, password: String) {
        val result = UserRepository.resetPassword(idx.toInt(), nickname, password)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                // 응답 실패 시
                if (!(response.isSuccessful)) {
                    Log.e("UserViewModel-resetPassword", "서버 응답 실패 - ${response.message()}")
                    _resetPwState.postValue(FieldState.Error("서버 연결에 실패하였습니다."))
                    return
                }

                // 응답 성공 시
                val responseBody = response.body()?.string()
                val isSuccess = responseBody?.let { JSONObject(it) }
                // 응답 성공 시
                if (isSuccess?.get("result") == true) {
                    //  비밀번호 변경 완료
                    _resetPwState.postValue(FieldState.Success(true))
                    return
                }

                Log.d("mytag--", "${isSuccess?.get("result")}")
                // 비밀번호 변경 실패
                _resetPwState.postValue(FieldState.Fail("비밀번호 변경에 실패하였습니다. 다시 시도해 주세요."))
                return
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UserViewModel-resetPassword", "서버 연결 실패 - ${t.printStackTrace()}")
                _resetPwState.postValue(FieldState.Error("서버 연결에 실패하였습니다."))
            }
        })
    }

    fun checkPasswordValidate(password: String) {
        // 유효성 검사 통과 실패 시
        if (!(CheckValidation.validatePassword(password))) {
            _pwFieldState.value = FieldState.Fail("* 8~16사이의 숫자, 영어 소문자, 특수문자의 조합이어야 합니다.\n* 사용 가능한 특수문자( $@!%*#?& )")
            return
        }

        // 검사 통과 시
        _pwFieldState.value = FieldState.Success("* 사용 가능한 비밀번호 입니다.")
    }


    fun checkConfirmPasswordValidate(password: String, confirmPassword: String) {
        if (password.isNotEmpty()) {
            if (!(CheckValidation.validateConfirmPassword(password, confirmPassword))) {
                _confirmPwFieldState.value = FieldState.Fail("* 비밀번호가 일치하지 않습니다.")
                return
            }

            _confirmPwFieldState.value = FieldState.Success("* 비밀번호가 일치합니다.")
        }
    }

    fun clearFindIdList() {
        _findIdList.value = null
    }


}