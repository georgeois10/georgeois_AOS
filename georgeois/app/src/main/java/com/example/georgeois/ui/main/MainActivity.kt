package com.example.georgeois.ui.main

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.georgeois.R
import com.example.georgeois.databinding.ActivityMainBinding
import com.example.georgeois.ui.chat.AddChatFragment
import com.example.georgeois.ui.chat.ChatAccountBookFragment
import com.example.georgeois.ui.chat.ChatRoomFragment
import com.example.georgeois.ui.myInfo.EditProfileFragment
import com.example.georgeois.ui.myInfo.InCategoryFragment
import com.example.georgeois.ui.myInfo.ModifyPasswordFragment
import com.example.georgeois.ui.myInfo.MyPostFragment
import com.example.georgeois.ui.myInfo.OutCategoryFragment
import com.example.georgeois.ui.user.JoinMainFragment
import com.example.georgeois.ui.user.JoinSocialFragment
import com.example.georgeois.ui.user.LoginFindIdFragment
import com.example.georgeois.ui.user.LoginFindPasswordFragment
import com.example.georgeois.ui.user.LoginMainFragment
import com.example.georgeois.ui.user.LoginResetPasswordFragment
import com.google.android.material.transition.MaterialSharedAxis

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    var newFragment: Fragment? = null
    var oldFragment: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        replaceFragment(LOGIN_MAIN_FRAGMENT,false,null)
    }

    // 화면 분기
    companion object{
        val MAIN_FRAGMENT = "MainFragment"
        val LOGIN_MAIN_FRAGMENT = "LoginMainFragment"
        val ADD_CHAT_FRAGMENT = "AddChatFragment"
        val CHAT_ROOM_FRAGMENT = "ChatRoomFragment"
        val CHAT_ACCOUT_BOOK_FRAGMENT = "ChatAccountBookFragment"
        val EDIT_PROFILE_FRAGMENT = "EditProfileFragment"
        val MODIFY_PASSWORD_FRAGMENT = "ModifyPasswordFragment"
        val MY_POST_FRAGMENT = "MyPostFragment"
        val IN_CATEGORY_FRAGMENT = "InCategoryFragment"
        val OUT_CATEGORY_FRAGMENT = "OutCategoryFragment"
        val JOIN_MAIN_FRAGMENT = "JoinMainFragment"
        val JOIN_SOCIAL_FRAGMENT = "JoinSocialFragment"
        val LOGIN_FIND_PASSWORD_FRAGMENT = "LoginFindPasswordFragment"
        val LOGIN_FIND_ID_FRAGMENT = "LoginFindIdFragment"
        val LOGIN_RESET_PASSWORD_FRAGMENT = "LoginResetPasswordFragment"
    }
    fun replaceFragment(name:String, addToBackStack:Boolean, bundle:Bundle?){
        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // newFragment 에 Fragment가 들어있으면 oldFragment에 넣어준다.
        if(newFragment != null){
            oldFragment = newFragment
        }

        // 새로운 Fragment를 담을 변수
        newFragment = when(name){
            MAIN_FRAGMENT -> MainFragment()
            LOGIN_MAIN_FRAGMENT -> LoginMainFragment()
            ADD_CHAT_FRAGMENT -> AddChatFragment()
            CHAT_ROOM_FRAGMENT -> ChatRoomFragment()
            CHAT_ACCOUT_BOOK_FRAGMENT -> ChatAccountBookFragment()
            EDIT_PROFILE_FRAGMENT -> EditProfileFragment()
            MODIFY_PASSWORD_FRAGMENT -> ModifyPasswordFragment()
            MY_POST_FRAGMENT -> MyPostFragment()
            IN_CATEGORY_FRAGMENT -> InCategoryFragment()
            OUT_CATEGORY_FRAGMENT -> OutCategoryFragment()
            JOIN_MAIN_FRAGMENT -> JoinMainFragment()
            JOIN_SOCIAL_FRAGMENT -> JoinSocialFragment()
            LOGIN_FIND_PASSWORD_FRAGMENT -> LoginFindPasswordFragment()
            LOGIN_FIND_ID_FRAGMENT -> LoginFindIdFragment()
            LOGIN_RESET_PASSWORD_FRAGMENT -> LoginResetPasswordFragment()
            else -> Fragment()
        }

        newFragment?.arguments = bundle

        if(newFragment != null) {

            // 애니메이션 설정

            if(oldFragment != null){
                oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                oldFragment?.enterTransition = null
                oldFragment?.returnTransition = null
            }

            newFragment?.exitTransition = null
            newFragment?.reenterTransition = null
            newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

            // Fragment를 교채한다.
            fragmentTransaction.replace(R.id.container_main, newFragment!!)

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    // Fragment를 BackStack에서 제거한다.
    fun removeFragment(name:String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}