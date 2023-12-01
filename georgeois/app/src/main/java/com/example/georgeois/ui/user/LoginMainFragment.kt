package com.example.georgeois.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.georgeois.databinding.FragmentLoginMainBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.viewModel.UserViewModel


class LoginMainFragment : Fragment() {
    lateinit var loginMainBinding: FragmentLoginMainBinding
    lateinit var mainActivity: MainActivity

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginMainBinding = FragmentLoginMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        userViewModel = ViewModelProvider(mainActivity)[UserViewModel::class.java]
        userViewModel.run {
            user.observe(mainActivity){
                if (it != null) {
                    mainActivity.replaceFragment(MainActivity.MAIN_FRAGMENT,true,null)
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
                    Toast.makeText(requireContext(), "체크됨", Toast.LENGTH_SHORT).show()
                }
            }
            // 카카오 로그인
            linearLayoutLoginMainKakaoLogin.setOnClickListener {
                Toast.makeText(requireContext(),"카카오 로그인 클릭",Toast.LENGTH_SHORT).show()
                moveToSocialLogin(KAKAO, mainActivity)
            }
            // 네이버 로그인
            linearLayoutLoginMainNaverLogin.setOnClickListener {
                Toast.makeText(requireContext(),"네이버 로그인 클릭",Toast.LENGTH_SHORT).show()
                moveToSocialLogin(NAVER, mainActivity)
            }
        }

        return loginMainBinding.root
    }

    /**
     * 다른 소셜 로그인 추가 시
     * - companion object 에 Char 작성 후
     * - 함수명 : moveTo'Social'Login(mainActivity: MainActivity)
     *
     * 등록된 소셜 로그인 목록
     * - 카카오 로그인 : [moveToKakaoLogin]
     * - 네이버 로그인 : [moveToNaverLogin]
     */
    private fun moveToSocialLogin(authType: Char, mainActivity: MainActivity) {
        when (authType) {
            KAKAO -> moveToKakaoLogin(mainActivity)
            NAVER -> moveToNaverLogin(mainActivity)

            else -> Unit
        }
    }

    /**
     * [moveToSocialLogin] 에서 호출된다.
     */
    private fun moveToNaverLogin(mainActivity: MainActivity) {
        // TODO : 카카오 로그인 화면으로 이동 구현

        // 네이버 로그인 성공 시 실행될 코드
        mainActivity.replaceFragment(MainActivity.JOIN_SOCIAL_FRAGMENT,true,null)
    }

    /**
     * [moveToSocialLogin] 에서 호출된다.
     */
    private fun moveToKakaoLogin(mainActivity: MainActivity) {
        // TODO : 카카오 로그인 화면으로 이동 구현

        // 카카오 로그인 성공 시 실행될 코드
        mainActivity.replaceFragment(MainActivity.JOIN_SOCIAL_FRAGMENT,true,null)
    }

    private companion object {
        val KAKAO = 'K'
        val NAVER = 'N'
    }

}