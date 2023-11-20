package com.example.georgeois.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentLoginFindPasswordBinding
import com.example.georgeois.ui.main.MainActivity

class LoginFindPasswordFragment : Fragment() {
    private lateinit var loginFindPasswordBinding: FragmentLoginFindPasswordBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        loginFindPasswordBinding = FragmentLoginFindPasswordBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        loginFindPasswordBinding.run {

            // 툴바
            materialToolbarLoginFindPassword.run {
                title = "비밀번호 찾기"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.LOGIN_FIND_PASSWORD_FRAGMENT)
                }
            }

            // 전화번호로 인증번호 발송 클릭
            buttonLoginFindPasswordSendVerificationNumber.setOnClickListener {
                // TODO : 인증번호 발송 기능 구현
            }
            // 인증번호 확인 클릭
            buttonLoginFindPasswordVerificationNumber.setOnClickListener {
                // TODO : 인증번호 확인 기능 구현

                // 인증 성공 시
                mainActivity.replaceFragment(MainActivity.LOGIN_RESET_PASSWORD_FRAGMENT,true,null)
            }

        }

        return loginFindPasswordBinding.root
    }
}