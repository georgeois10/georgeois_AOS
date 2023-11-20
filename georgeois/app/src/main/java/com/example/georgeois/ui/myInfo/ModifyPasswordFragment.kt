package com.example.georgeois.ui.myInfo

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentModifyPasswordBinding
import com.example.georgeois.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ModifyPasswordFragment : Fragment() {
    lateinit var fragmentModifyPasswordBinding: FragmentModifyPasswordBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentModifyPasswordBinding = FragmentModifyPasswordBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentModifyPasswordBinding.run {
            textInputLayoutModifyPasswordInputNewPassword.isEnabled = false
            textInputLayoutModifyPasswordInputNewPasswordCheck.isEnabled = false
            var sendCode = 0
            var checkCode = 0
            var checkPw = 0
            toolbarModifyPassword.run {
                title = "비밀번호 변경"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MODIFY_PASSWORD_FRAGMENT)
                }
            }
            buttonModifyPasswordSendCode.setOnClickListener {
                val phoneNumber = textInputEdixTextModifyPasswordInputPhoneNumber.text.toString()
                if(containsSpecialCharacters(phoneNumber) == true || phoneNumber.length != 11 || phoneNumber.startsWith("010") == false){
                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        val customTitle = setCustomTitle("휴대폰 번호 입력 오류")
                        setCustomTitle(customTitle)
                        setMessage("올바른 형식의 휴대폰 번호를 입력해주세요.")
                        setNegativeButton("확인", null)
                    }
                    builder.show()
                }
                else{
                    sendCode = 1
                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        val customTitle = setCustomTitle("인증 번호 전송 완료")
                        setCustomTitle(customTitle)
                        setMessage("인증 번호가 전송되었습니다.")
                        setNegativeButton("확인", null)
                    }
                    builder.show()
                }
            }
            buttonModifyPasswordCheckCode.setOnClickListener {
                val code = "1234"
                if(textInputEditTextModifyPasswordInputCode.text.toString() != code){
                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        val customTitle = setCustomTitle("인증 번호 입력 오류")
                        setCustomTitle(customTitle)
                        setMessage("인증 번호를 확인해주세요.")
                        setNegativeButton("확인", null)
                    }
                    builder.show()
                }
                else{
                    checkCode = 1
                    buttonModifyPasswordSendCode.isEnabled = false
                    buttonModifyPasswordCheckCode.isEnabled = false
                    textInputLayoutModifyPasswordInputNewPassword.isEnabled = true
                    textInputLayoutModifyPasswordInputNewPasswordCheck.isEnabled = true
                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        val customTitle = setCustomTitle("인증 완료")
                        setCustomTitle(customTitle)
                        setMessage("인증이 완료되었습니다.")
                        setNegativeButton("확인", null)
                    }
                    builder.show()
                }
            }
            buttonModifyPasswordSave.setOnClickListener {
                if(sendCode != 1 || checkCode != 1){
                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                        val customTitle = setCustomTitle("본인 인증 오류")
                        setCustomTitle(customTitle)
                        setMessage("본인 인증을 진행해주세요.")
                        setNegativeButton("확인", null)
                    }
                    builder.show()
                }
                else{
                    val newPw = textInputEditTextModifyPasswordInputNewPassword.text.toString()
                    val newPwCheck = textInputEditTextModifyPasswordInputNewPasswordCheck.text.toString()
                    if(newPw != newPwCheck){
                        val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                            val customTitle = setCustomTitle("비밀번호 입력 오류")
                            setCustomTitle(customTitle)
                            setMessage("입력한 비밀번호를 확인해주세요.")
                            setNegativeButton("확인", null)
                        }
                        builder.show()
                    }
                    else{
                        if(isValidPassword(newPw) == false){
                            val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                val customTitle = setCustomTitle("비밀번호 입력 오류")
                                setCustomTitle(customTitle)
                                setMessage("비밀번호는 특수 문자를 포함하여 입력해주세요.")
                                setNegativeButton("확인", null)
                            }
                            builder.show()
                        }
                        else{
                            if(newPw.length > 16 || newPw.length < 8){
                                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                    val customTitle = setCustomTitle("비밀번호 입력 오류")
                                    setCustomTitle(customTitle)
                                    setMessage("비밀번호는 8~16자 사이로 입력해주세요.")
                                    setNegativeButton("확인", null)
                                }
                                builder.show()
                            }
                            else{
                                checkPw = 1
                                if(checkCodeAndPassword(sendCode,checkCode,checkPw)){
                                    val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                        val customTitle = setCustomTitle("비밀번호 변경 완료")
                                        setCustomTitle(customTitle)
                                        setMessage("비밀번호가 변경되었습니다.")
                                        setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                                            mainActivity.removeFragment(MainActivity.MODIFY_PASSWORD_FRAGMENT)
                                        }
                                    }
                                    builder.show()
                                }
                                else{

                                }
                            }
                        }
                    }
                }
            }
        }

        return fragmentModifyPasswordBinding.root
    }

    fun containsSpecialCharacters(phoneNumber: String): Boolean {
        // 정규 표현식을 사용하여 특수 문자가 있는지 검사합니다.
        // 숫자, 공백, 더하기(+), 하이픈(-)을 제외한 모든 문자를 특수 문자로 간주합니다.
        val regex = Regex("[^\\d\\s+\\-]")
        return regex.containsMatchIn(phoneNumber)
    }

    fun checkCodeAndPassword(sendCode:Int, checkCode:Int,pw:Int) : Boolean{
        if(sendCode == checkCode && pw == checkCode && sendCode == 1)
            return true
        else return false
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
    fun isValidPassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,16}$".toRegex()
        return password.matches(passwordRegex)
    }
}