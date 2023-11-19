package com.example.georgeois.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.georgeois.R
import com.example.georgeois.databinding.ActivityMainBinding
import com.example.georgeois.ui.board.BoardDetailFragment
import com.example.georgeois.ui.board.BoardRegisterFragment
import com.example.georgeois.ui.home.HomeAnalysisCategoryFragment
import com.example.georgeois.ui.home.HomeAnalysisFragment
import com.example.georgeois.ui.home.HomeRegisterFragment
import com.example.georgeois.ui.home.HomeMainFragment
import com.example.georgeois.ui.user.LoginMainFragment
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
        val HOME_MAIN_FRAGMENT = "HomeMainFragment"
        val HOME_REGISTER_FRAGMENT = "HomeRegisterFragment"
        val HOME_ANALYSIS_CATEGORY_FRAGMENT = "HomeAnalysisCategoryFragment"
        val HOME_ANALYSIS_FRAGMENT = "HomeAnalysisFragment"
        val BOARD_REGISTER_FRAGMENT = "BoardRegisterFragment"
        val BOARD_DETAIL_FRAGMENT = "BoardDetailFragment"
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
            HOME_MAIN_FRAGMENT -> HomeMainFragment()
            HOME_REGISTER_FRAGMENT -> HomeRegisterFragment()
            HOME_ANALYSIS_CATEGORY_FRAGMENT -> HomeAnalysisCategoryFragment()
            HOME_ANALYSIS_FRAGMENT -> HomeAnalysisFragment()
            BOARD_REGISTER_FRAGMENT -> BoardRegisterFragment()
            BOARD_DETAIL_FRAGMENT -> BoardDetailFragment()
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