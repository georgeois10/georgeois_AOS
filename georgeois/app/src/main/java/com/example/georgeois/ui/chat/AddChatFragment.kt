package com.example.georgeois.ui.chat

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentAddChatBinding
import com.example.georgeois.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale

class AddChatFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var fragmentAddChatBinding: FragmentAddChatBinding
    var yearList = mutableListOf<Int>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentAddChatBinding = FragmentAddChatBinding.inflate(layoutInflater)

        for (i in 2023 downTo 1960){
            yearList.add(i)
        }

        fragmentAddChatBinding.run {
            toolbarAddChat.run {
                title = "채팅방 생성"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ADD_CHAT_FRAGMENT)
                }
            }

            checkBoxAddChatMale.setOnClickListener {
                changeCheckBox()
            }
            checkBoxAddChatFemale.setOnClickListener {
                changeCheckBox()
            }
            checkBoxAddChatNone.setOnClickListener {
                changeCheckBox()
            }


            spinnerAddChatStartYear.run{
                // 어뎁터 설정
                val a1 = ArrayAdapter(mainActivity, android.R.layout.simple_spinner_item, yearList)
                // Spinner가 펼쳐져 있을 때의 항목 모양
                a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                adapter = a1

                // Spinner의 항목을 코드로 선택한다.
                // 0부터 시작하는 순서값을 넣어준다.
                //setSelection(2)

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    // 항목을 선택했을 호출되는 메서드
                    // 3 번째 : 선택한 항목의 순서 값(0부터..)
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                        textView2.text = "${dataList[position]} 항목을 선택했습니다"
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // TODO("Not yet implemented")
                    }
                }
            }
            spinnerAddChatEndYear.run{
                val a1 = ArrayAdapter(mainActivity, android.R.layout.simple_spinner_item, yearList)
                a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                adapter = a1

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // TODO("Not yet implemented")
                    }
                }
            }
            buttonAddChatCreateRoom.run {
                var roomName = ""
                var roomBudget = 0
                var roomGender = ""
                var roomStartYear = ""
                var roomEndYear = ""
                setOnClickListener {
                    //채팅방 입력 체크
                    if(textInputEditTextAddChatRoomName.text.toString() == ""){
                        val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                            val customTitle = setCustomTitle("채팅방 이름 입력")
                            setCustomTitle(customTitle)
                            setMessage("생성할 채팅방의 이름을 입력해주세요.")
                            setPositiveButton("확인",null)
                        }
                        builder.show()
                    }
                    else{
                        roomName = textInputEditTextAddChatRoomName.text.toString()
                        //예산 입력 체크
                        if(textInputEditTextAddChatBudget.text.toString() == ""){
                            val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                val customTitle = setCustomTitle("예산 입력")
                                setCustomTitle(customTitle)
                                setMessage("생성할 채팅방의 예산을 입력해주세요.")
                                setPositiveButton("확인",null)
                            }
                            builder.show()
                        }
                        else{
                            roomBudget = textInputEditTextAddChatBudget.text.toString().toInt()
                            //성별 입력 체크
                            if(checkBoxAddChatMale.isChecked == false && checkBoxAddChatFemale.isChecked == false && checkBoxAddChatNone.isChecked == false){
                                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                    val customTitle = setCustomTitle("성별 입력")
                                    setCustomTitle(customTitle)
                                    setMessage("생성할 채팅방의 허용 성별을 입력해주세요.")
                                    setPositiveButton("확인",null)
                                }
                                builder.show()
                            }
                            else{
                                if(checkBoxAddChatMale.isChecked == true)
                                    roomGender = "남성"
                                else if(checkBoxAddChatFemale.isChecked == true)
                                    roomGender = "여성"
                                else if(checkBoxAddChatNone.isChecked == true)
                                    roomGender = "성별 무관"

                                roomStartYear = spinnerAddChatStartYear.selectedItem.toString()
                                roomEndYear = spinnerAddChatEndYear.selectedItem.toString()

                                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                    val customTitle = setCustomTitle("채팅방 생성")
                                    setCustomTitle(customTitle)
                                    val budget = formatNumberWithCommas(BigInteger(roomBudget.toString()))
                                    setMessage("입력한 정보를 통해 채팅방을 생성하시겠습니까?\n방이름 = $roomName\n예산 = ${budget}원\n성별 = $roomGender\n출생년도 = ${roomStartYear}년 ~ ${roomEndYear}년")
                                    setNegativeButton("취소",null)
                                    setPositiveButton("확인"){ dialogInterface: DialogInterface, i: Int ->

                                    }
                                }
                                builder.show()
                            }
                        }
                    }
                }
            }
        }

        return fragmentAddChatBinding.root
    }
    fun changeCheckBox(){
        val checkBoxListener = CompoundButton.OnCheckedChangeListener { checkBox, isChecked ->
            if (isChecked) {
                if (checkBox != fragmentAddChatBinding.checkBoxAddChatMale) fragmentAddChatBinding.checkBoxAddChatMale.isChecked = false
                if (checkBox != fragmentAddChatBinding.checkBoxAddChatFemale) fragmentAddChatBinding.checkBoxAddChatFemale.isChecked = false
                if (checkBox != fragmentAddChatBinding.checkBoxAddChatNone) fragmentAddChatBinding.checkBoxAddChatNone.isChecked = false
            }
        }
        fragmentAddChatBinding.checkBoxAddChatMale.setOnCheckedChangeListener(checkBoxListener)
        fragmentAddChatBinding.checkBoxAddChatFemale.setOnCheckedChangeListener(checkBoxListener)
        fragmentAddChatBinding.checkBoxAddChatNone.setOnCheckedChangeListener(checkBoxListener)
    }

    fun formatNumberWithCommas(number: BigInteger): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        return formatter.format(number)
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