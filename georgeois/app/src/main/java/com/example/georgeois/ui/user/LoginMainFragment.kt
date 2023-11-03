package com.example.georgeois.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentLoginMainBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.ui.main.MainFragment
import kotlin.math.log


class LoginMainFragment : Fragment() {
    lateinit var loginMainBinding: FragmentLoginMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginMainBinding = FragmentLoginMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        loginMainBinding.run {
            buttonLoginMainLogin.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MAIN_FRAGMENT,true,null)
            }

            // 아이디 찾기
            textViewLoginMainFindId.setOnClickListener {
                Toast.makeText(requireContext(),"아이디 찾기 클릭 ",Toast.LENGTH_SHORT).show()
            }
            // 비밀번호 찾기
            textViewLoginMainFindPw.setOnClickListener {
                Toast.makeText(requireContext(),"비밀번호 찾기 클릭 ",Toast.LENGTH_SHORT).show()
            }
            // 회원가입
            textViewLoginMainJoin.setOnClickListener {
                Toast.makeText(requireContext(),"회원가입 클릭 ",Toast.LENGTH_SHORT).show()
            }

            // 카카오 로그인
            linearLayoutLoginMainKakaoLogin.setOnClickListener {
                Toast.makeText(requireContext(),"카카오 로그인 클릭",Toast.LENGTH_SHORT).show()
            }
            // 네이버 로그인
            linearLayoutLoginMainNaverLogin.setOnClickListener {
                Toast.makeText(requireContext(),"네이버 로그인 클릭",Toast.LENGTH_SHORT).show()
            }
        }





        return loginMainBinding.root
    }

}