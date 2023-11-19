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
import com.example.georgeois.databinding.FragmentHomeAnalysisCategoryBinding
import com.example.georgeois.databinding.RowBoardMainBinding
import com.example.georgeois.databinding.RowHomeAnalysisCategoryBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.DialogAccountDetail


class HomeAnalysisCategoryFragment : Fragment() {
    lateinit var fragmentHomeAnalysisCategoryBinding: FragmentHomeAnalysisCategoryBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeAnalysisCategoryBinding = FragmentHomeAnalysisCategoryBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentHomeAnalysisCategoryBinding.run {
            var category = arguments?.getString("category")!!
            materialToolbarHomeAnalysisCategory.run {
                title = category
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.HOME_ANALYSIS_CATEGORY_FRAGMENT)
                }
            }
            var itemList = mutableListOf(
                "내역",
                "내역2",
                "내역3",
                "내역4",
                "내역5",
                "내역6",
                "내역7",
                "내역8",
                "내역9",
                "내역10",
                "내역11",
                "내역12",
                "내역123",
                "내역14",
                "내역15",

                )

            val adapter = HomeAnalysisCategoryAdapter(itemList)
            recyclerViewHomeAnalysisCategory.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewHomeAnalysisCategory.adapter = adapter
        }
        return fragmentHomeAnalysisCategoryBinding.root
    }
    inner class HomeAnalysisCategoryAdapter(var itemList: MutableList<String>) :
        RecyclerView.Adapter<HomeAnalysisCategoryAdapter.HomeAnalysisCategoryViewHolder>() {

        inner class HomeAnalysisCategoryViewHolder(val rowHomeAnalysisCategoryBinding: RowHomeAnalysisCategoryBinding) :
            RecyclerView.ViewHolder(rowHomeAnalysisCategoryBinding.root) {


            init {
                rowHomeAnalysisCategoryBinding.root.setOnClickListener {
                    val dialogAccountDetail = DialogAccountDetail(requireContext(),layoutInflater)
                    dialogAccountDetail.callFunction()
                }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAnalysisCategoryViewHolder {
            val rowHomeAnalysisCategoryBinding = RowHomeAnalysisCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val homeAnalysisCategoryViewHolder = HomeAnalysisCategoryViewHolder(rowHomeAnalysisCategoryBinding)
            rowHomeAnalysisCategoryBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return homeAnalysisCategoryViewHolder
        }

        override fun onBindViewHolder(holder: HomeAnalysisCategoryViewHolder, position: Int) {
            holder.rowHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryContent.text = itemList[position]
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }

}