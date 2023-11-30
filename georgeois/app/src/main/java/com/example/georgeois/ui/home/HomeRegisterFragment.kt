package com.example.georgeois.ui.home

import DateTimePicker
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeRegisterBinding
import com.example.georgeois.databinding.ItemSpinnerHomeRegisterBinding
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.dataclass.OutAccountBookClass
import com.example.georgeois.repository.InAccountBookRepository
import com.example.georgeois.repository.OutAccountBookRepository
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.MoneyTextWatcher
import com.example.georgeois.viewModel.UserViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeRegisterFragment : Fragment() {
    lateinit var fragmentHomeRegisterBinding: FragmentHomeRegisterBinding
    lateinit var mainActivity: MainActivity
    lateinit var inCategoryList :List<String>
    lateinit var outCategoryList :List<String>
    lateinit var userViewModel: UserViewModel
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    lateinit var moneyTextWatcher : MoneyTextWatcher

    // 유저정보
    var uIdx = 0
    var uId = ""
    var uNicknm = ""
    var uCategory = ""
    var uAmount = 0
    var uContent = ""
    var uDate = ""
    var uProperty = 'H'
    var uBudregiYn = false
    var inCategory = ""
    var outCategory = ""
    var isCategory = ""

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeRegisterBinding = FragmentHomeRegisterBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // 로그인한 유저 정보 가져오기
        userViewModel = ViewModelProvider(mainActivity)[UserViewModel::class.java]
        userViewModel.run {
            user.observe(requireActivity()){
                uIdx = it!!.u_idx
                uId = it!!.u_id
                uNicknm = it!!.u_nickNm
                inCategory = it!!.u_in_ctgy
                outCategory = it!!.u_out_ctgy
            }
        }
        // 카테고리 List로
        inCategoryList = inCategory.split(",")
        outCategoryList = outCategory.split(",")

        fragmentHomeRegisterBinding.run {
            // 초기 수입으로 설정
            inCategory()

            materialToolbarHomeRegister.run{
                title = "등록"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.HOME_REGISTER_FRAGMENT)
                }
                inflateMenu(R.menu.menu_save)
                setOnMenuItemClickListener{
                    when(it.itemId){
                        R.id.save_menu->  {
                            viewLifecycleOwner.lifecycleScope.launch {
                                save()
                            }
                        }
                    }
                    true
                }
            }
            // 수입 버튼
            buttonHomeRegisterIn.setOnClickListener {
                buttonHomeRegisterOut.setBackgroundColor(resources.getColor(R.color.accentGray))
                buttonHomeRegisterIn.setBackgroundColor(resources.getColor(R.color.accentGreen))
                inCategory()
            }
            // 지출 버튼
            buttonHomeRegisterOut.setOnClickListener {
                buttonHomeRegisterOut.setBackgroundColor(resources.getColor(R.color.accentRed))
                buttonHomeRegisterIn.setBackgroundColor(resources.getColor(R.color.accentGray))
                outCategory()
                // 자산 설정
                propertySetting()
            }

            // 날짜 설정
            dateSetting()
            moneyTextWatcher = MoneyTextWatcher(textInputEditTextHomeRegisterMoney)
            textInputLayoutHomeRegisterMoney.editText?.addTextChangedListener(moneyTextWatcher)



        }
        return fragmentHomeRegisterBinding.root
    }

    // 수입일때 카테고리
    fun inCategory(){
        fragmentHomeRegisterBinding.run {
            linearLayoutHomeRegisterProperty.visibility = View.GONE
            spinnerHomeRegister.adapter = CategorySpinnerAdapter(requireContext(),R.layout.item_spinner_home_register,inCategoryList)
            spinnerHomeRegister.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    uCategory = spinnerHomeRegister.getItemAtPosition(p2).toString()
                    isCategory = "in"
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    isCategory = "out"
                }
            }
        }
    }

    // 지출일때 카테고리
    fun outCategory() {
        fragmentHomeRegisterBinding.run {
            linearLayoutHomeRegisterProperty.visibility = View.VISIBLE
            spinnerHomeRegister.adapter = CategorySpinnerAdapter(requireContext(),R.layout.item_spinner_home_register,outCategoryList)
            spinnerHomeRegister.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    uCategory = spinnerHomeRegister.getItemAtPosition(p2).toString()
                    isCategory = "out"
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    isCategory = "in"

                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateSetting() {

        fragmentHomeRegisterBinding.run {
            val currentDateTime = LocalDateTime.now()
            val currentDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
            uDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00.000"))
            val dateTimePicker = DateTimePicker(requireContext())
            textViewHomeRegisterDate.text = currentDate

            textViewHomeRegisterDate.setOnClickListener {
                dateTimePicker.setDateTimeListener(object : DateTimePicker.DateTimeListener {
                    override fun onDateTimeSelected(dateTime: String) {
                        textViewHomeRegisterDate.text = dateTime
                        uDate = dateTimePicker.updateDate(dateTime)
                    }
                })
                dateTimePicker.showDateTimePicker()
            }
        }
    }


    // 자산 설정
    fun propertySetting(){
        fragmentHomeRegisterBinding.run {
            // 자산 하나만 선택
            val chips = fragmentHomeRegisterBinding.chipGroupHomeRegister.children.toList()
            var previousCheckedChip:Chip? = chipHomeRegisterCash
            for (chip in chips) {
                if (chip is Chip) {
                    chip.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            previousCheckedChip?.isChecked = false
                            previousCheckedChip = chip
                            uProperty = when(chip.text.toString()){
                                "현금"-> 'H'
                                "체크카드" -> 'C'
                                "신용카드" -> 'S'
                                else -> 'H'
                            }
                        }
                        else{
                            if (chips.none { it is Chip && it.isChecked }) {
                                chip.isChecked = true
                            }
                        }
                    }
                }
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun save() {
        fragmentHomeRegisterBinding.run {
            val amountText = textInputEditTextHomeRegisterMoney.text?.toString()
            val contentText = textInputEditTextHomeRegisterContent.text?.toString()

            if (!amountText.isNullOrBlank() && !contentText.isNullOrBlank()) {
                val formattedText = moneyTextWatcher.formattedText.replace(",", "")
                uAmount = formattedText.toInt()
                uContent = contentText
                uBudregiYn = checkBoxHomeRegisterNoBudget.isChecked
                Log.e("php_입력", uDate)
                try {
                    when (isCategory) {
                        "in" -> {
                            val insertAccountBook = InAccountBookClass(
                                uIdx,
                                uId,
                                uNicknm,
                                uAmount,
                                uContent,
                                uCategory,
                                uDate,
                                "none",
                                uBudregiYn
                            )
                            InAccountBookRepository.insertInAccountBook(insertAccountBook)
                        }
                        "out" -> {

                            val outAccountBookClass = OutAccountBookClass(
                                uIdx,
                                uId,
                                uNicknm,
                                uAmount,
                                uContent,
                                uCategory,
                                uDate,
                                "none",
                                uBudregiYn,
                                uProperty
                            )
                            OutAccountBookRepository.insertOutAccountBook(outAccountBookClass)
                        }
                    }

                    mainActivity.removeFragment(MainActivity.HOME_REGISTER_FRAGMENT)
                    Snackbar.make(requireView(), "저장했습니다.", Snackbar.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("php", "데이터 저장 오류: ${e.message}")
                    Snackbar.make(requireView(), "저장에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                // amount 또는 content가 빈 경우
                var toastText = if(amountText.isNullOrBlank()){
                    "금액"
                }else{
                    "내용"
                }
                Toast.makeText(requireContext(), "${toastText}을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }



}

// 카테고리 스피너 Adapter
class CategorySpinnerAdapter(context: Context, @LayoutRes private val resId:Int, private val categoryList : List<String>):ArrayAdapter<String>(context,resId,categoryList){
    // 드롭다운하지 않은 상태의 Spinner 항목 뷰
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemSpinnerHomeRegisterBinding = ItemSpinnerHomeRegisterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        itemSpinnerHomeRegisterBinding.textViewHomeRegisterSpinnerItem.text = categoryList[position]
        return itemSpinnerHomeRegisterBinding.root
    }

    // 드롭다운된 항목들 리스트의 뷰
    @SuppressLint("ViewHolder")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemSpinnerHomeRegisterBinding = ItemSpinnerHomeRegisterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        itemSpinnerHomeRegisterBinding.textViewHomeRegisterSpinnerItem.text = categoryList[position]
        return itemSpinnerHomeRegisterBinding.root
    }

    override fun getCount() = categoryList.size

}



