package com.example.georgeois.ui.myInfo

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentEditProfileBinding
import com.example.georgeois.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditProfileFragment : Fragment() {
    lateinit var fragmentEditProfileBinding: FragmentEditProfileBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentEditProfileBinding = FragmentEditProfileBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentEditProfileBinding.run {
            toolbarEditProfile.run {
                title = "프로필"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.EDIT_PROFILE_FRAGMENT)
                }

                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_edit_profile_save -> {
                            val nickName = textInputEditTextEditProfileInputUserNickName.text.toString()
                            val email = textInputEditTextEditProfileInputUserEmail.text.toString()
                            if(nickName.length == 0) {
                                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                    val customTitle = setCustomTitle("닉네임 입력 오류")
                                    setCustomTitle(customTitle)
                                    setMessage("닉네임을 입력해주세요.")
                                    setPositiveButton("닫기",null)
                                }
                                builder.show()
                            }else{
                                if(email.length == 0){
                                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                        val customTitle = setCustomTitle("이메일 입력 오류")
                                        setCustomTitle(customTitle)
                                        setMessage("이메일을 입력해주세요.")
                                        setPositiveButton("닫기",null)
                                    }
                                    builder.show()
                                }
                                else{
                                    mainActivity.removeFragment(MainActivity.EDIT_PROFILE_FRAGMENT)
                                }
                            }
                        }
                    }
                    true
                }
            }
            buttonEditProfileSetUserImage.setOnClickListener {
                toolbarEditProfile.title = "이미지 변경"
            }
            textInputEditTextEditProfileInputUserEmail.run {
                setText("aaaa@aaaa.com")
            }
            textInputEditTextEditProfileInputUserNickName.run {
                setText("유저1")
            }
            buttonEditProfileSetPassword.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MODIFY_PASSWORD_FRAGMENT,true,null)
            }
        }

        return fragmentEditProfileBinding.root
    }
    fun setCustomTitle(title: String): TextView {
        val customTitle = TextView(context).apply {
            text = title  // 타이틀 텍스트 설정
            textSize = 25f  // 텍스트 사이즈 설정
            typeface = ResourcesCompat.getFont(context, R.font.space)  // 글꼴 스타일 설정
            setTextColor(Color.BLACK)  // 텍스트 색상 설정
            setPadding(100, 100, 0, 20)  // 패딩 설정 (단위: px)
        }
        return customTitle
    }
}