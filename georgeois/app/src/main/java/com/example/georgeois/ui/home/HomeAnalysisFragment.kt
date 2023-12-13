package com.example.georgeois.ui.home

import android.icu.util.LocaleData
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeAnalysisBinding
import com.example.georgeois.databinding.RowMainAnalysisBinding
import com.example.georgeois.dataclass.CategoryAccountBookClass
import com.example.georgeois.ui.main.MainActivity
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
import java.util.Locale

class HomeAnalysisFragment : Fragment() {
    lateinit var fragmentHomeAnalysisBinding: FragmentHomeAnalysisBinding
    lateinit var mainActivity: MainActivity
    lateinit var userViewModel: UserViewModel
    lateinit var accountBookViewModel : AccountBookViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeAnalysisBinding = FragmentHomeAnalysisBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.setUsePercentValues(true)
        var isChartInitialized = false
        var uIdx = 0
        var inOrOut = 'i'
        // 2023-12
        var yearMonth = LocalDate.now().yearMonth
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        accountBookViewModel = ViewModelProvider(requireActivity())[AccountBookViewModel::class.java]
        var inCategoryABList = listOf<CategoryAccountBookClass>()
        var outCategoryABList = listOf<CategoryAccountBookClass>()
        userViewModel.user.observe(viewLifecycleOwner) {
            uIdx = it!!.u_idx
            lifecycleScope.launch {
                accountBookViewModel.fetchInData(uIdx)
                accountBookViewModel.fetchOutData(uIdx)
                accountBookViewModel.getAllAccountBookList(uIdx)
                accountBookViewModel.getMonthAccountBookList(uIdx, yearMonth.toString())
                accountBookViewModel.getDayOfMonthAccountBookList(uIdx,yearMonth.toString())
            }
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
            }


            textViewHomeAnalysisDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
            imageButtonHomeAnalysisRight.setOnClickListener {
                yearMonth = yearMonth.plusMonths(1)
                var tempList = mutableListOf<CategoryAccountBookClass>()
                textViewHomeAnalysisDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"

                when(inOrOut){
                    'i' -> {
                        for( i in inCategoryABList){
                            if(i.date == "$yearMonth"){
                                tempList.add(i)
                            }
                        }
                    }
                    'o' -> {
                        for( o in outCategoryABList){
                            if(o.date == "$yearMonth"){
                                tempList.add(o)
                            }
                        }
                    }
                }
                setPieChart(tempList,yearMonth)
            }
            imageButtonHomeAnalysisLeft.setOnClickListener {
                yearMonth = yearMonth.minusMonths(1)
                var tempList = mutableListOf<CategoryAccountBookClass>()
                textViewHomeAnalysisDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
                when(inOrOut){
                    'i' -> {
                        for( i in inCategoryABList){
                            if(i.date == "$yearMonth"){
                                tempList.add(i)
                            }
                        }
                    }
                    'o' -> {
                        for( o in outCategoryABList){
                            if(o.date == "$yearMonth"){
                                tempList.add(o)
                            }
                        }
                    }
                }
                setPieChart(tempList,yearMonth)

            }




            buttonHomeAnalysis.setOnClickListener {
                var tempList = mutableListOf<CategoryAccountBookClass>()
                when(inOrOut){
                    'i' -> {
                        buttonHomeAnalysis.setBackgroundColor(resources.getColor(R.color.accentRed))
                        buttonHomeAnalysis.setText("지출")
                        inOrOut = 'o'
                        for( o in outCategoryABList){
                            if(o.date == "$yearMonth"){
                                tempList.add(o)
                            }
                        }
                    }
                    'o' ->{
                        buttonHomeAnalysis.setBackgroundColor(resources.getColor(R.color.accentGreen))
                        buttonHomeAnalysis.setText("수입")
                        inOrOut = 'i'
                        for( i in inCategoryABList){
                            if(i.date == "$yearMonth"){
                                tempList.add(i)
                            }
                        }
                    }
                }
                setPieChart(tempList,yearMonth)

            }

        }


        return fragmentHomeAnalysisBinding.root
    }
    inner class HomeAnalysisAdapter(var itemList: MutableList<CategoryAccountBookClass>) :
        RecyclerView.Adapter<HomeAnalysisAdapter.HomeAnalysisViewHolder>() {

        inner class HomeAnalysisViewHolder(val rowHomeAnalysisBinding: RowMainAnalysisBinding) :
            RecyclerView.ViewHolder(rowHomeAnalysisBinding.root) {
            val category : TextView
            init {

                category = rowHomeAnalysisBinding.textViewMainAnalysisCategory
                // 클릭시
                rowHomeAnalysisBinding.root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("category", category.text.toString())

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
            holder.rowHomeAnalysisBinding.textViewMainAnalysisTotalAmount.text = formattedMoney
            holder.rowHomeAnalysisBinding.textViewMainAnalysisCount.text = "${item.count}건"
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
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
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.animateY(1000) // 애니메이션 설정
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.invalidate() // 차트 갱신

        val adapter = HomeAnalysisAdapter(adapterList)
        fragmentHomeAnalysisBinding.recyclerViewHomeAnalysis.layoutManager = LinearLayoutManager(requireContext())
        fragmentHomeAnalysisBinding.recyclerViewHomeAnalysis.adapter = adapter

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
}

