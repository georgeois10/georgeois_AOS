package com.example.georgeois.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentJoinSocialBinding
import com.example.georgeois.ui.main.MainActivity
import java.util.Calendar

class JoinSocialFragment : Fragment() {
    private lateinit var joinSocialBinding: FragmentJoinSocialBinding
    private lateinit var mainActivity: MainActivity
    private var selectedYear: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        joinSocialBinding = FragmentJoinSocialBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        joinSocialBinding.run {

            // 툴바
            materialToolbarJoinSocial.run {
                title = "회원가입"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.JOIN_SOCIAL_FRAGMENT)
                }
            }

            // profile 이미지 클릭
            circleImageViewJoinSocialProfile.setOnClickListener {
                // TODO : 갤러리 권한 확인 후 갤러리로 이동 기능 구현
                Toast.makeText(requireContext(), "이미지 클릭", Toast.LENGTH_SHORT).show()

            }

            // 닉네임 중복 확인 클릭
            buttonJoinSocialCheckNickNmDuplication.setOnClickListener {
                // TODO : 닉네임 중복 확인 기능 구현
            }

            // 성별 선택
            radioGroupJoinSocialGender.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    // 남성 선택
                    radioButtonJoinSocialMale.id -> {
                        Toast.makeText(requireContext(), "남성 선택", Toast.LENGTH_SHORT).show()
                    }
                    // 여성 선택
                    radioButtonJoinSocialFemale.id -> {
                        Toast.makeText(requireContext(), "여성 선택", Toast.LENGTH_SHORT).show()
                    }

                }
            }

            // 출생년도 스피너 클릭
            setBirthYearSpinner()

            // 회원가입 클릭
            buttonJoinSocialJoin.setOnClickListener {
                // TODO : 회원가입 기능 구현 (회원 정보 bundle 사용할지 ViewModel 사용할지 확인 필요)
                mainActivity.removeFragment(MainActivity.JOIN_SOCIAL_FRAGMENT)
                mainActivity.replaceFragment(MainActivity.LOGIN_MAIN_FRAGMENT,false,null)
            }

        }

        return joinSocialBinding.root
    }

    private fun setBirthYearSpinner() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear downTo 1900).map { it.toString() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, years)

        joinSocialBinding.spinnerJoinSocialBirthYear.run {
            this.adapter = adapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedYear = years[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

}