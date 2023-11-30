package com.example.georgeois.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.databinding.FragmentHomeMainBinding
import com.example.georgeois.databinding.RowHomeMainBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.DialogAccountDetail
import com.example.georgeois.utill.MoneyType


class HomeMainFragment : Fragment() {
    lateinit var fragmentHomeMainBinding: FragmentHomeMainBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeMainBinding = FragmentHomeMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentHomeMainBinding.run {
            materialToolbarHomeMain.run {
                title = "거르주아"
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
            val adapter = HomeMainAdapter(itemList)
            recyclerViewHomeMain.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewHomeMain.adapter = adapter

            // 분석
            linearLayoutHomeMainAnalysis.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.HOME_ANALYSIS_FRAGMENT,true,null)
            }

            // 등록
            floatingActionButtonHomeMainRegister.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.HOME_REGISTER_FRAGMENT,true,null)
            }

            var inMoney = textViewHomeMainInMoney.text
            val moneyType = MoneyType()
            inMoney = moneyType.moneyText(inMoney.toString())
            textViewHomeMainInMoney.text = inMoney

            var outMoney = textViewHomeMainOutMoney.text
            outMoney = moneyType.moneyText(outMoney.toString())
            textViewHomeMainOutMoney.text = outMoney

        }
        return fragmentHomeMainBinding.root
    }

    inner class HomeMainAdapter(var itemList: MutableList<String>) :
        RecyclerView.Adapter<HomeMainAdapter.HomeMainViewHolder>() {

        inner class HomeMainViewHolder(val rowHomeMainBinding: RowHomeMainBinding) :
            RecyclerView.ViewHolder(rowHomeMainBinding.root) {
            init {

                rowHomeMainBinding.root.setOnClickListener {
                    val dialogAccountDetail = DialogAccountDetail(requireContext(), layoutInflater)
                    dialogAccountDetail.callFunction()
                }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeMainViewHolder {
            val rowHomeMainBinding =
                RowHomeMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val boardMainViewHolder = HomeMainViewHolder(rowHomeMainBinding)
            rowHomeMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return boardMainViewHolder
        }

        override fun onBindViewHolder(holder: HomeMainViewHolder, position: Int) {
            holder.rowHomeMainBinding.textViewHomeMainContent.text = itemList[position]
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }
}