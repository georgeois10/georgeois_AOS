package com.example.georgeois.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeRegisterBinding
import com.example.georgeois.databinding.ItemSpinnerHomeRegisterBinding
import com.example.georgeois.ui.main.MainActivity
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class HomeRegisterFragment : Fragment() {
    lateinit var fragmentHomeRegisterBinding: FragmentHomeRegisterBinding
    lateinit var mainActivity: MainActivity
    // 카테고리 list값 받아오기
    var inCategoryList = listOf(
        "수입 카테고리1",
        "수입 카테고리2",
        "수입 카테고리3",
        "수입 카테고리4",
        "수입 카테고리5",
        "수입 카테고리6",
        "수입 카테고리7",
        "수입 카테고리8",
        "수입 카테고리9",
        "수입 카테고리10"
    )
    val outCategoryList = listOf(
        "운동",
        "책",
        "지출 카테고리3",
        "지출 카테고리4",
        "지출 카테고리5",
        "지출 카테고리6",
        "지출 카테고리7",
        "지출 카테고리8",
        "지출 카테고리9",
        "지출 카테고리10",
    )
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeRegisterBinding = FragmentHomeRegisterBinding.inflate(inflater)
        mainActivity = activity as MainActivity
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
                        R.id.save_menu->save()
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
            }

            // 날짜 설정
            dateSetting()
            // 자산 설정
            assetSetting()

            textInputLayoutHomeRegisterMoney.editText?.addTextChangedListener(MoneyTextWatcher(textInputLayoutHomeRegisterMoney))



        }
        return fragmentHomeRegisterBinding.root
    }

    // 수입일때 카테고리
    fun inCategory(){
        fragmentHomeRegisterBinding.run {

            spinnerHomeRegister.adapter = CategorySpinnerAdapter(requireContext(),R.layout.item_spinner_home_register,inCategoryList)
            spinnerHomeRegister.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val value = spinnerHomeRegister.getItemAtPosition(p2).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // 선택되지 않은 경우
                }
            }
        }
    }

    // 지출일때 카테고리
    fun outCategory() {
        fragmentHomeRegisterBinding.run {

            spinnerHomeRegister.adapter = CategorySpinnerAdapter(requireContext(),R.layout.item_spinner_home_register,outCategoryList)
            spinnerHomeRegister.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val value = spinnerHomeRegister.getItemAtPosition(p2).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // 선택되지 않은 경우
                }
            }

        }
    }

    // 날짜설정
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateSetting(){

        fragmentHomeRegisterBinding.run {
            // 오늘 날짜
            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            textViewHomeRegisterDate.text = currentDate

            // 날짜 클릭시 날짜 변경 다이얼로그 후 변경
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
    }

    // 자산 설정
    fun assetSetting(){
        fragmentHomeRegisterBinding.run {
            // 자산 하나만 선택
            val chips = fragmentHomeRegisterBinding.chipGroupHomeRegister.children.toList()
            var previousCheckedChip:Chip? = chipHomeRegisterCash
            for (chip in chips) {
                if (chip is Chip) {
                    chip.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            previousCheckedChip?.isChecked = false
                            previousCheckedChip = chip
                        }
                        else{
                            previousCheckedChip = null
                        }
                    }
                }
            }
            // 하나도 선택되지 않았을때
            // if(checkedChipId == View.NO_ID)
            // if(previousCheckedChip==null) {
            // Toast.makeText(requireContext(), "최소 하나의 항목을 선택하세요", Toast.LENGTH_SHORT).show()
            //  }
        }
    }



    // 입력된 거 저장
    fun save(){
        fragmentHomeRegisterBinding.run {
            mainActivity.removeFragment(MainActivity.HOME_REGISTER_FRAGMENT)
            Snackbar.make(fragmentHomeRegisterBinding.root, "저장되었습니다.", Snackbar.LENGTH_SHORT).show()
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


// 금액 textWatcher
class MoneyTextWatcher(private val textInputLayout: TextInputLayout) : TextWatcher {
    private val numberFormat = NumberFormat.getInstance(Locale.getDefault())

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // 이 메서드는 텍스트 변경 전에 호출됩니다.
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // 이 메서드는 텍스트가 변경될 때 호출됩니다.
        val text = s.toString().replace(",", "").trim()
        val parsed = if (text.isNotEmpty()) text.toDouble() else 0.0
        val formatted = numberFormat.format(parsed)

        textInputLayout.editText?.removeTextChangedListener(this)
        textInputLayout.editText?.setText(formatted)
        textInputLayout.editText?.setSelection(formatted.length)
        textInputLayout.editText?.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        // 이 메서드는 텍스트 변경 후에 호출됩니다.
    }
}

