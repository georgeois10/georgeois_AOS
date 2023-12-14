package com.example.georgeois.ui.home

import android.accounts.Account
import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.LocaleData
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeAnalysisBinding
import com.example.georgeois.databinding.RowMainAnalysisBinding
import com.example.georgeois.dataclass.AccountBookClass
import com.example.georgeois.dataclass.CategoryAccountBookClass
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.dataclass.OutAccountBookClass
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.DialogClickYearMonth
import com.example.georgeois.utill.MoneyType
import com.example.georgeois.viewModel.AccountBookViewModel
import com.example.georgeois.viewModel.UserViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeAnalysisFragment : Fragment() {
    lateinit var fragmentHomeAnalysisBinding: FragmentHomeAnalysisBinding
    lateinit var mainActivity: MainActivity
    lateinit var userViewModel: UserViewModel
    lateinit var accountBookViewModel : AccountBookViewModel
    var yearMonth = LocalDate.now().yearMonth
    var inCategoryABList = listOf<CategoryAccountBookClass>()
    var outCategoryABList = listOf<CategoryAccountBookClass>()
    var inOrOut = 'i'
    var uIdx = 0
    var isChartInitialized = false
    var adapter = HomeAnalysisAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeAnalysisBinding = FragmentHomeAnalysisBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.setUsePercentValues(true)

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        accountBookViewModel = ViewModelProvider(requireActivity())[AccountBookViewModel::class.java]

        userViewModel.user.observe(viewLifecycleOwner) {
            uIdx = it!!.u_idx
            accountBookViewModel.getAllAccountBookList(uIdx)
            accountBookViewModel.getMonthAccountBookList(uIdx, yearMonth.toString())
            accountBookViewModel.getDayOfMonthAccountBookList(uIdx,yearMonth.toString())
        }

        accountBookViewModel.inCategoryAccountBookList.observe(viewLifecycleOwner) {
            inCategoryABList = it
            if (!isChartInitialized) {
                setPieChart(inCategoryABList, yearMonth)
                isChartInitialized = true
            }

        }
        accountBookViewModel.outCategoryAccountBookList.observe(viewLifecycleOwner){
            outCategoryABList = it
        }


        fragmentHomeAnalysisBinding.run {
            materialToolbarHomeAnalysis.run {
                title = "분석"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.HOME_ANALYSIS_FRAGMENT)
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_home_today -> {
                            yearMonth = LocalDate.now().yearMonth
                            textViewHomeAnalysisDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
                            when(inOrOut){
                                'i' -> {
                                    inCategory()
                                }
                                'o' -> {
                                    outCategory()
                                }
                            }
                        }
                    }
                    true
                }
            }

            textViewHomeAnalysisDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
            textViewHomeAnalysisDate.setOnClickListener {
                val dialogClickYearMonth = DialogClickYearMonth(requireContext())
                if (yearMonth != null) {
                    dialogClickYearMonth.setYearMonthListener(object : DialogClickYearMonth.YearMonthListner{
                        override fun onYearMonthSelected(year: String, month: String) {
                            textViewHomeAnalysisDate.text = "${year}년 ${month}월"
                            yearMonth = YearMonth.of(year.toInt(),month.toInt())
                            when(inOrOut){
                                'i' -> inCategory()
                                'o' -> outCategory()
                            }
                        }
                    })
                    dialogClickYearMonth.callFunction(yearMonth.year,yearMonth.monthValue,layoutInflater)
                }
            }

            imageButtonHomeAnalysisRight.setOnClickListener {
                yearMonth = yearMonth.plusMonths(1)
                textViewHomeAnalysisDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
                when(inOrOut){
                    'o' -> {
                        outCategory()
                    }
                    'i' -> {
                        inCategory()
                    }
                }
            }
            imageButtonHomeAnalysisLeft.setOnClickListener {
                yearMonth = yearMonth.minusMonths(1)
                textViewHomeAnalysisDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
                when(inOrOut){
                    'i' -> {
                        inCategory()
                    }
                    'o' -> {
                        outCategory()
                    }
                }

            }



            buttonHomeAnalysisIn.setOnClickListener {
                buttonHomeAnalysisOut.setBackgroundColor(resources.getColor(R.color.accentGray))
                buttonHomeAnalysisIn.setBackgroundColor(resources.getColor(R.color.accentGreen))
                inOrOut = 'i'
                inCategory()
            }
            // 지출 버튼
            buttonHomeAnalysisOut.setOnClickListener {
                buttonHomeAnalysisOut.setBackgroundColor(resources.getColor(R.color.accentRed))
                buttonHomeAnalysisIn.setBackgroundColor(resources.getColor(R.color.accentGray))
                inOrOut = 'o'
                outCategory()
            }

        }

        fragmentHomeAnalysisBinding.recyclerViewHomeAnalysis.layoutManager = LinearLayoutManager(requireContext())
        fragmentHomeAnalysisBinding.recyclerViewHomeAnalysis.adapter = adapter


        return fragmentHomeAnalysisBinding.root
    }
    inner class HomeAnalysisAdapter() :
        RecyclerView.Adapter<HomeAnalysisAdapter.HomeAnalysisViewHolder>() {
        var itemList: List<CategoryAccountBookClass> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun submitList(tempList:List<CategoryAccountBookClass>){
            itemList = tempList
            notifyDataSetChanged()
        }


        inner class HomeAnalysisViewHolder(val rowHomeAnalysisBinding: RowMainAnalysisBinding) :
            RecyclerView.ViewHolder(rowHomeAnalysisBinding.root) {
            val category : TextView
            init {
                category = rowHomeAnalysisBinding.textViewMainAnalysisCategory
                // 클릭시
                rowHomeAnalysisBinding.root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("category", category.text.toString())
                    bundle.putString("date",yearMonth.toString())
                    bundle.putChar("inOrOut",inOrOut)
                    mainActivity.replaceFragment(MainActivity.HOME_ANALYSIS_CATEGORY_FRAGMENT,true,bundle)
                }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAnalysisViewHolder {
            val rowHomeAnalysisBinding = RowMainAnalysisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val homeAnalysisViewHolder = HomeAnalysisViewHolder(rowHomeAnalysisBinding)
            rowHomeAnalysisBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return homeAnalysisViewHolder
        }

        override fun onBindViewHolder(holder: HomeAnalysisViewHolder, position: Int) {
            var item = itemList[position]
            val moneyType = MoneyType()
            val formattedMoney = moneyType.moneyText("${item.amount}")
            holder.rowHomeAnalysisBinding.textViewMainAnalysisCategory.text = item.category

            when(item.inOrOut){
                'i'-> {
                    holder.rowHomeAnalysisBinding.textViewMainAnalysisTotalAmount.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.accentGreen))
                    holder.rowHomeAnalysisBinding.textViewMainAnalysisTotalAmount.text = "+$formattedMoney"
                }
                'o' -> {
                    holder.rowHomeAnalysisBinding.textViewMainAnalysisTotalAmount.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.accentRed))
                    holder.rowHomeAnalysisBinding.textViewMainAnalysisTotalAmount.text = "-$formattedMoney"
                }
            }
            holder.rowHomeAnalysisBinding.textViewMainAnalysisCount.text = "${item.count}건"
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }

    fun inCategory(){
        var tempList = inCategoryABList.filter {
            it.date == "$yearMonth"
        }
        setPieChart(tempList,yearMonth)
    }
    fun outCategory(){
        var tempList = outCategoryABList.filter {
            it.date == "$yearMonth"
        }
        setPieChart(tempList,yearMonth)
    }

    private fun setPieChart(categoryList : List<CategoryAccountBookClass>, yearMonth: YearMonth){

        val adapterList = mutableListOf<CategoryAccountBookClass>()
        val dataList = ArrayList<PieEntry>()
        dataList.clear()
        for( i in categoryList){
            if(i.date == "$yearMonth"){
                dataList.add(PieEntry(i.amount.toFloat(),i.category))
                adapterList.add(i)
            }
        }
        // 값 받아오면 정렬하고 넣기
        dataList.sortedBy { it.y }
        val dataSet = PieDataSet(dataList,"")
        with(dataSet){
            sliceSpace = 1f
            setColors(*ColorTemplate.PASTEL_COLORS)
        }
        dataSet.setDrawValues(false)
        var data = PieData(dataSet)
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.data =data
        // 차트 설명
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.description.isEnabled = false
        // 차트 회전 비활성화
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.isRotationEnabled = false
        // 하단 설명 비활성화
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.legend.isEnabled = false
        // 구멍 없애기
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.isDrawHoleEnabled = false
        // 터치 막기
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.setTouchEnabled(false)

        // 애니메이션 설정
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.animateY(550) // 애니메이션 설정
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.invalidate() // 차트 갱신

        adapter.submitList(adapterList.toList())

        if(dataList.isEmpty()){
            fragmentHomeAnalysisBinding.pieChartHomeAnalysis.visibility = View.GONE
            fragmentHomeAnalysisBinding.LinearLayoutHomeAnalysis.visibility = View.GONE
            fragmentHomeAnalysisBinding.linearLayoutHomeAnalysisTextNodata.visibility = View.VISIBLE
        }else{
            fragmentHomeAnalysisBinding.pieChartHomeAnalysis.visibility = View.VISIBLE
            fragmentHomeAnalysisBinding.LinearLayoutHomeAnalysis.visibility = View.VISIBLE
            fragmentHomeAnalysisBinding.linearLayoutHomeAnalysisTextNodata.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        accountBookViewModel.getAllAccountBookList(uIdx)
        when (inOrOut) {
            'i' -> {
                fragmentHomeAnalysisBinding.buttonHomeAnalysisOut.setBackgroundColor(resources.getColor(R.color.accentGray))
                fragmentHomeAnalysisBinding.buttonHomeAnalysisIn.setBackgroundColor(resources.getColor(R.color.accentGreen))
                accountBookViewModel.allAccountBookList.observe(viewLifecycleOwner){
                    inCategory()
                }

            }
            'o' -> {
                fragmentHomeAnalysisBinding.buttonHomeAnalysisOut.setBackgroundColor(resources.getColor(R.color.accentRed))
                fragmentHomeAnalysisBinding.buttonHomeAnalysisIn.setBackgroundColor(resources.getColor(R.color.accentGray))
                accountBookViewModel.allAccountBookList.observe(viewLifecycleOwner){
                    outCategory()
                }

            }
        }


    }

}

