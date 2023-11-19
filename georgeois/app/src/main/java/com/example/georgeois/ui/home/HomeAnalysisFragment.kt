package com.example.georgeois.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeAnalysisBinding
import com.example.georgeois.databinding.RowHomeMainBinding
import com.example.georgeois.databinding.RowMainAnalysisBinding
import com.example.georgeois.ui.main.MainActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat
import java.util.Locale

class HomeAnalysisFragment : Fragment() {
    lateinit var fragmentHomeAnalysisBinding: FragmentHomeAnalysisBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeAnalysisBinding = FragmentHomeAnalysisBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentHomeAnalysisBinding.pieChartHomeAnalysis.setUsePercentValues(true)

        // 넣을 데이터 설정
        val dataList = ArrayList<PieEntry>()
        var tempTotalCategory1 = 145000f
        var tempTotalCategory2 = 2000f
        var tempTotalCategory3 = 300000f
        var tempTotalCategory4 = 60000f
        var tempTotalCategory5 = 70000f

        dataList.add(PieEntry(tempTotalCategory1, "감자"))
        dataList.add(PieEntry(tempTotalCategory2, "사과"))
        dataList.add(PieEntry(tempTotalCategory3, "바나나"))
        dataList.add(PieEntry(tempTotalCategory4, "딸기"))
        dataList.add(PieEntry(tempTotalCategory5, "오렌지"))
        dataList.add(PieEntry(40000f, "감자1"))
        dataList.add(PieEntry(80000f, "사과2"))
        dataList.add(PieEntry(123400f, "바나나3"))
        dataList.add(PieEntry(345523f, "딸기4"))
        dataList.add(PieEntry(1234f, "오렌지5"))
        // 값 받아오면 정렬하고 넣기
        dataList.sortedBy { it.y }
        val dataSet = PieDataSet(dataList,"")
        with(dataSet){
            sliceSpace = 1f
            setColors(*ColorTemplate.PASTEL_COLORS)
        }
        dataSet.setDrawValues(false)
        fragmentHomeAnalysisBinding.run {
            materialToolbarHomeAnalysis.run {
                title = "분석"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.HOME_ANALYSIS_FRAGMENT)
                }
            }
            var data = PieData(dataSet)
            pieChartHomeAnalysis.data =data
            // 차트 설명
            pieChartHomeAnalysis.description.isEnabled = false
            // 차트 회전 비활성화
            pieChartHomeAnalysis.isRotationEnabled = false
            // 하단 설명 비활성화
            pieChartHomeAnalysis.legend.isEnabled = false
            // 구멍 없애기
            pieChartHomeAnalysis.isDrawHoleEnabled = false
            // 터치 막기
            pieChartHomeAnalysis.setTouchEnabled(false)

            // 애니메이션 설정
            pieChartHomeAnalysis.animateY(1000) // 애니메이션 설정
            pieChartHomeAnalysis.invalidate() // 차트 갱신


            val adapter = HomeAnalysisAdapter(dataList)
            recyclerViewHomeAnalysis.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewHomeAnalysis.adapter = adapter
        }


        return fragmentHomeAnalysisBinding.root
    }
    inner class HomeAnalysisAdapter(var itemList: ArrayList<PieEntry>) :
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
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val formattedValue = currencyFormat.format(itemList[position].value.toInt())

            holder.rowHomeAnalysisBinding.textViewMainAnalysisCategory.text = itemList[position].label
            holder.rowHomeAnalysisBinding.textViewMainAnalysisTotalAmount.text = formattedValue
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }


}

