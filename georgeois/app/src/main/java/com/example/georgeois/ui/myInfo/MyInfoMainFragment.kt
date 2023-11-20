package com.example.georgeois.ui.myInfo

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentMyInfoMainBinding
import com.example.georgeois.databinding.LayoutEditBudgetBinding
import com.example.georgeois.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale
import kotlin.concurrent.thread

class MyInfoMainFragment : Fragment() {
    lateinit var fragmentMyInfoMainBinding: FragmentMyInfoMainBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentMyInfoMainBinding = FragmentMyInfoMainBinding.inflate(layoutInflater)

        fragmentMyInfoMainBinding.run {
            toolbarMyInfoMain.run {
                title = "마이페이지"
            }

            // 프로필
            layoutMyInfoMainEditMyInfo.run {
                buttonMyInfoMainEditInfo.isEnabled = false

                setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.EDIT_PROFILE_FRAGMENT,true,null)
                }
            }

            // 푸시 설정
            switchMyInfoPush.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    Toast.makeText(context, "Push 알림이 활성화 되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Push 알림이 비활성화 되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            // 예산 변경
            layoutMyInfoMainBudget.run {
                val budget = formatNumberWithCommas(0)
                textViewMyInfoMainBudget.text = "${budget} 원"
                setOnClickListener {
                    val dialogBinding = LayoutEditBudgetBinding.inflate(layoutInflater)

                    val builder =
                        MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                            val customTitle = setCustomTitle("예산변경(최대 1,000만원)")
                            setCustomTitle(customTitle)
                        }

                    builder.setView(dialogBinding.root)

                    var inputBudget = dialogBinding.editTextInputBudget

                    inputBudget.requestFocus()

                    var currentBudget = textViewMyInfoMainBudget.text.toString()
                    val result = currentBudget.map { char ->
                        if (char.isDigit()) char else ""
                    }.joinToString("")
                    inputBudget.setText("${result}")
                    inputBudget.typeface = ResourcesCompat.getFont(mainActivity, R.font.space)

                    thread {
                        inputBudget.post {
                            val length = inputBudget.text.length
                            inputBudget.setSelection(length)
                            val imm =
                                mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(inputBudget, InputMethodManager.SHOW_IMPLICIT)
                        }
                    }
                    builder.setNegativeButton("취소", null)
                    builder.setPositiveButton("저장") { dialogInterface: DialogInterface, i: Int ->
                        val inputText = inputBudget.text.toString()
                        if(inputText.isEmpty()){
                            Toast.makeText(context, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        }else {
                            var budget = inputText
                                if(budget.toInt() > 10000000){
                                Toast.makeText(context, "예산은 1,000만원 아래로 설정해주세요.", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                budget = formatNumberWithCommas(inputText.toInt())
                                textViewMyInfoMainBudget.text = "${budget} 원"
                            }
                        }
                    }

                    builder.show()
                }
            }
            layoutMyInfoMainOutCategory.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.OUT_CATEGORY_FRAGMENT,true,null)
            }
            layoutMyInfoMainInCategory.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.IN_CATEGORY_FRAGMENT,true,null)
            }
            layoutMyInfoMainMyPost.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_POST_FRAGMENT,true,null)
            }

            // 로그아웃
            layoutMyInfoMainLogOut.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                    val customTitle = setCustomTitle("로그아웃")
                    setCustomTitle(customTitle)
                    setMessage("현재 로그인 된 계정에서 로그아웃 됩니다.")
                    setNegativeButton("취소", null)
                    setPositiveButton("로그아웃") { dialogInterface: DialogInterface, i: Int ->

                    }
                }
                builder.show()
            }

            // 회원탈퇴
            layoutMyInfoMainDeleteAccount.setOnClickListener {
                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                    val customTitle = setCustomTitle("회원탈퇴")
                    setCustomTitle(customTitle)
                    setMessage("회원탈퇴를 하시면 저장된 모든 정보가 삭제되며 삭제된 정보는 복구할 수 없습니다.")
                    setNegativeButton("취소", null)
                    setPositiveButton("회원탈퇴") { dialogInterface: DialogInterface, i: Int ->

                    }
                }
                builder.show()
            }
        }

        return fragmentMyInfoMainBinding.root
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

    fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        return formatter.format(number)
    }
}