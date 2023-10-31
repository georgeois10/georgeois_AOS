package com.example.georgeois.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentMainBinding
import com.example.georgeois.ui.board.BoardMainFragment
import com.example.georgeois.ui.chat.ChatMainFragment
import com.example.georgeois.ui.home.HomeMainFragment
import com.example.georgeois.ui.myInfo.MyInfoMainFragment


class MainFragment : Fragment() {
    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainActivity:MainActivity

    var oldBottom = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentMainBinding.run {
            bottomNavigationViewMainFragment.run {
                selectedItemId = R.id.navigation_home
                setOnItemSelectedListener {
                    when(it.itemId){
                        R.id.navigation_home -> {
                            replaceFragment(HOME_MAIN_FRAGMENT,true,false)
                            oldBottom = "home"
                        }
                        R.id.navigation_chat -> {
                            replaceFragment(CHAT_MAIN_FRAGMENT,true,false)
                            oldBottom = "chat"
                        }
                        R.id.navigation_board -> {
                            replaceFragment(BOARD_MAIN_FRAGMENT,true,false)
                            oldBottom = "board"
                        }
                        R.id.navigation_myInfo -> {
                            replaceFragment(MY_INFO_MAIN_FRAGMENT, true, false)
                            oldBottom = "myInfo"
                        }

                    }
                    true
                }
            }
        }


        return fragmentMainBinding.root

    }
    companion object {
        fun newInstance() = MainFragment()
        val HOME_MAIN_FRAGMENT = "HomeMainFragment"
        val CHAT_MAIN_FRAGMENT = "ChatMainFragment"
        val BOARD_MAIN_FRAGMENT = "BoardMainFragment"
        val MY_INFO_MAIN_FRAGMENT = "MyInfoMainFragment"

    }
    fun replaceFragment(name: String, addToBackStack: Boolean, animate: Boolean) {
        // Fragment 교체 상태로 설정
        val fragmentTransaction = mainActivity.supportFragmentManager.beginTransaction()

        // 새로운 Fragment를 담을 변수
        var newFragment = when (name) {
            HOME_MAIN_FRAGMENT -> HomeMainFragment()
            CHAT_MAIN_FRAGMENT -> ChatMainFragment()
            BOARD_MAIN_FRAGMENT -> BoardMainFragment()
            MY_INFO_MAIN_FRAGMENT -> MyInfoMainFragment()
            else -> {
                Fragment()
            }
        }

        if (newFragment != null) {
            // Fragment 교체
            fragmentTransaction.replace(R.id.fragmentContainerView_MainFragment, newFragment)

            if (animate == true) {
                // 애니메이션 설정
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }

            if (addToBackStack == true) {
                // 이전으로 돌아가는 기능 이용하기 위해 Fragment Backstack에 넣어주기
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령 동작
            fragmentTransaction.commit()
        }
    }

    fun removeFragment(name: String) {
        mainActivity.supportFragmentManager.popBackStack(name, 0)
    }
    override fun onResume() {
        super.onResume()
        when(oldBottom) {
            "home" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_home
            "chat" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_chat
            "board" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_board
            "myInfo" -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_myInfo
            else -> fragmentMainBinding.bottomNavigationViewMainFragment.selectedItemId = R.id.navigation_home
        }
    }


}