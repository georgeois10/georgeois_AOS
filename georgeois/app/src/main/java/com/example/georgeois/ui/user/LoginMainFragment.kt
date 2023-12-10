package com.example.georgeois.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.georgeois.databinding.FragmentLoginMainBinding
import com.example.georgeois.resource.FieldState
import com.example.georgeois.resource.SocialLoginType
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.viewModel.UserViewModel


class LoginMainFragment : Fragment() {
    private lateinit var loginMainBinding: FragmentLoginMainBinding
    private lateinit var mainActivity: MainActivity

    private lateinit var userViewModel: UserViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginMainBinding = FragmentLoginMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        userViewModel = ViewModelProvider(mainActivity)[UserViewModel::class.java]

        userViewModel.run {
            user.observe(mainActivity){
                if (it != null) {
                    mainActivity.replaceFragment(MainActivity.MAIN_FRAGMENT,true,null)
                }
            }

            loginSuccessState.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        if (it.data != null) {
                            // 회원가입 필요한 상태
                            val bundle = createSocialJoinBundle(it.data)

                            // 소셜 회원가입 화면으로 이동 후 뒤로가기로 돌아올 때를 위해 loginSuccessState 를 null로 변경
                            userViewModel.clearLoginSuccessState()

                            // 소셜 회원가입 화면으로 이동
                            mainActivity.replaceFragment(MainActivity.JOIN_SOCIAL_FRAGMENT,true, bundle)
                        } else {
                            // 회원가입 필요없는 상태
                            mainActivity.replaceFragment(MainActivity.MAIN_FRAGMENT,false,null)
                        }
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

        loginMainBinding.run {
            // 로그인 클릭
            buttonLoginMainLogin.setOnClickListener {
                val id = textInputEditTextLoginMainId.text.toString()
                val pw = textInputEditTextLoginMainPw.text.toString()
                userViewModel.login(id, pw)
            }

            // 아이디 찾기
            textViewLoginMainFindId.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.LOGIN_FIND_ID_FRAGMENT,true,null)
            }
            // 비밀번호 찾기
            textViewLoginMainFindPw.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.LOGIN_FIND_PASSWORD_FRAGMENT,true,null)
            }
            // 회원가입
            textViewLoginMainJoin.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.JOIN_MAIN_FRAGMENT,true,null)
            }

            // 자동로그인 checkBox
            checkBoxLoginMainAutoLogin.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // TODO : 자동로그인 구현
                    Toast.makeText(requireContext(), "체크됨", Toast.LENGTH_SHORT).show()
                }
            }
            // 카카오 로그인
            linearLayoutLoginMainKakaoLogin.setOnClickListener {
                userViewModel.kakaoLogin(mainActivity)
            }
            // 네이버 로그인
            linearLayoutLoginMainNaverLogin.setOnClickListener {
                userViewModel.naverLogin(mainActivity)
            }
        }

        return loginMainBinding.root
    }

    /**
     * 소셜 로그인 후 회원가입이 필요한 경우
     * 소셜 로그인의 타입에 따라 bundle 생성
     *
     * 소셜 로그인 타입
     * @see SocialLoginType
     */
    private fun createSocialJoinBundle(socialLoginType: SocialLoginType) : Bundle {
        val bundle = Bundle()

        when (socialLoginType) {
            is SocialLoginType.NaverUser -> {
                // 네이버 로그인 일 때
                val naverUser = socialLoginType.userInfo

                bundle.apply {
                    putChar("authType", NAVER)
                    putString("id", naverUser?.email)
                    putString("primaryId", naverUser?.id)
                    putString("name", naverUser?.name)
                    putString("phoneNumber", naverUser?.mobile?.replace("-", ""))
                    putString("gender", naverUser?.gender)
                    putString("birthYear", naverUser?.birthYear)
                }
            }

            is SocialLoginType.KakaoUser -> {
                val kakaUser = socialLoginType.userInfo
                val kakaoUserAccount = kakaUser?.kakaoAccount

                // 한국일 때만 고려해 0을 넣었다.
                val phoneNumber = kakaoUserAccount?.phoneNumber?.split(" ")?.get(1)?.replace("-","")


                bundle.apply {
                    /**
                     * 카카오의 id는 이메일 or 핸드폰이기 때문에 대표 이메일이 없다면 대표 핸드폰번호를 Id로 설정
                     * - 핸드폰번호형식 (kakaoAccount.phoneNumber 로 받아오는)
                     * ex) +82 10-1234-1234
                     *
                     * 핸드폰이 Id 가 될 때는 그대로
                     * 핸드폰 번호 일 때는 01012341234 형태로 변경
                     */
                    putChar("authType", KAKAO)
                    putString("id", kakaoUserAccount?.email ?: kakaoUserAccount?.phoneNumber)
                    putString("primaryId", kakaUser?.id.toString())
                    putString("name", kakaoUserAccount?.name)
                    putString("phoneNumber", "0$phoneNumber")
                    putString("gender", kakaoUserAccount?.gender.toString())
                    putString("birthYear", kakaoUserAccount?.birthyear)
                }
            }
        }

        return bundle
    }

    private companion object {
        val KAKAO = 'K'
        val NAVER = 'N'
    }
}