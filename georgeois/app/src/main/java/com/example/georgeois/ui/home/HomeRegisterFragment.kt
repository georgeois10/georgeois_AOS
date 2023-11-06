package com.example.georgeois.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeRegisterBinding
import com.example.georgeois.ui.main.MainActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HomeRegisterFragment : Fragment() {
    lateinit var fragmentHomeRegisterBinding: FragmentHomeRegisterBinding
    lateinit var mainActivity: MainActivity

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeRegisterBinding = FragmentHomeRegisterBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentHomeRegisterBinding.run {
            materialToolbarHomeRegister.run{
                title = "등록"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.HOME_REGISTER_FRAGMENT)
                }
            }
            buttonHomeRegisterIn.setOnClickListener {
                buttonHomeRegisterOut.setBackgroundColor(resources.getColor(R.color.accentGray))
                buttonHomeRegisterIn.setBackgroundColor(resources.getColor(R.color.accentGreen))
                inRegister()
            }
            buttonHomeRegisterOut.setOnClickListener {
                buttonHomeRegisterOut.setBackgroundColor(resources.getColor(R.color.accentRed))
                buttonHomeRegisterIn.setBackgroundColor(resources.getColor(R.color.accentGray))
                outRegister()
            }
            // 오늘 날짜
            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            textViewHomeRegisterDate.text = currentDate

            // 날짜 클릭시 날짜 변경
            val todayInUtcMillis = MaterialDatePicker.todayInUtcMilliseconds()
            textViewHomeRegisterDate.setOnClickListener {
                val materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setCalendarConstraints(CalendarConstraints.Builder().setEnd(todayInUtcMillis).build())
                    .build()
                materialDatePicker.addOnPositiveButtonClickListener { selection ->
                    // 선택한 날짜를 처리
                    val selectedDate = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(Date(selection))
                    textViewHomeRegisterDate.text = selectedDate
                }

                materialDatePicker.show(mainActivity.supportFragmentManager, "DATE_PICKER_TAG")
            }

        }
        return fragmentHomeRegisterBinding.root
    }

    // 수입일때 등록
    fun inRegister(){
        fragmentHomeRegisterBinding.run {
        }
    }

    // 지출일때 등록
    fun outRegister(){
        fragmentHomeRegisterBinding.run {
        }
    }


}