package com.example.georgeois.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeAnalysisCategoryBinding
import com.example.georgeois.databinding.RowHomeAnalysisCategoryBinding
import com.example.georgeois.dataclass.AccountBookClass
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.DialogAccountDetail
import com.example.georgeois.utill.DialogClickYearMonth
import com.example.georgeois.utill.DialogDismissListener
import com.example.georgeois.utill.MoneyType
import com.example.georgeois.viewModel.AccountBookViewModel
import com.example.georgeois.viewModel.UserViewModel
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class HomeAnalysisCategoryFragment : Fragment(), DialogDismissListener {
    lateinit var fragmentHomeAnalysisCategoryBinding: FragmentHomeAnalysisCategoryBinding
    lateinit var mainActivity: MainActivity
    lateinit var userViewModel: UserViewModel
    lateinit var accountBookViewModel: AccountBookViewModel
    var uIdx = 0
    var inCategory = ""
    var outCategory = ""
    var adapter = HomeAnalysisCategoryAdapter()
    var yearMonth = LocalDate.now().yearMonth
    var category = ""
    var inOrOut = 'i'
    var categoryList = listOf<AccountBookClass>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeAnalysisCategoryBinding =
            FragmentHomeAnalysisCategoryBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        accountBookViewModel = ViewModelProvider(requireActivity())[AccountBookViewModel::class.java]

        var tempYearMonth = arguments?.getString("date")!!
        yearMonth = YearMonth.of(
            tempYearMonth.split("-")[0].toInt(),
            tempYearMonth.split("-")[1].toInt()
        )


        category = arguments?.getString("category")!!
        inOrOut = arguments?.getChar("inOrOut")!!

        userViewModel.user.observe(viewLifecycleOwner) {
            uIdx = it!!.u_idx
            inCategory = it!!.u_in_ctgy
            outCategory = it!!.u_out_ctgy
            accountBookViewModel.getMonthCategoryAccountBookList(uIdx, inOrOut, category)
            accountBookViewModel.getAllAccountBookList(uIdx)
        }
        accountBookViewModel.monthCategoryAccountBook.observe(viewLifecycleOwner) { newList ->
            categoryList = newList
            adapter.submitList(categoryList)
        }

        fragmentHomeAnalysisCategoryBinding.run {
            materialToolbarHomeAnalysisCategory.run {
                title = category
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.HOME_ANALYSIS_CATEGORY_FRAGMENT)
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_home_today -> {
                            yearMonth = LocalDate.now().yearMonth
                            textViewHomeAnalysisCategoryDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
                            accountBookViewModel.getMonthCategoryAccountBookList(uIdx, inOrOut, category)
                        }
                    }
                    true
                }
            }
            textViewHomeAnalysisCategoryDate.setOnClickListener {
                val dialogClickYearMonth = DialogClickYearMonth(requireContext())
                if (yearMonth != null) {
                    dialogClickYearMonth.setYearMonthListener(object : DialogClickYearMonth.YearMonthListner {
                        override fun onYearMonthSelected(year: String, month: String) {
                            textViewHomeAnalysisCategoryDate.text = "${year}년 ${month}월"
                            yearMonth = YearMonth.of(year.toInt(), month.toInt())
                            accountBookViewModel.getMonthCategoryAccountBookList(uIdx, inOrOut, category)
                        }
                    })

                    dialogClickYearMonth.callFunction(yearMonth.year, yearMonth.monthValue, layoutInflater)

                }
            }
            textViewHomeAnalysisCategoryDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
            imageButtonHomeAnalysisCategoryLeft.setOnClickListener {
                yearMonth = yearMonth.minusMonths(1)
                textViewHomeAnalysisCategoryDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
                accountBookViewModel.getMonthCategoryAccountBookList(uIdx, inOrOut, category)
            }
            imageButtonHomeAnalysisCategoryRight.setOnClickListener {
                yearMonth = yearMonth.plusMonths(1)
                textViewHomeAnalysisCategoryDate.text = "${yearMonth.year}년 ${yearMonth.month.value}월"
                accountBookViewModel.getMonthCategoryAccountBookList(uIdx, inOrOut, category)
            }
            recyclerViewHomeAnalysisCategory.adapter = adapter
            recyclerViewHomeAnalysisCategory.layoutManager = LinearLayoutManager(requireContext())
        }
        return fragmentHomeAnalysisCategoryBinding.root
    }

    inner class HomeAnalysisCategoryAdapter() :
        RecyclerView.Adapter<HomeAnalysisCategoryAdapter.HomeAnalysisCategoryViewHolder>() {
        var accountBookList: List<AccountBookClass> = emptyList()
        var datefilteredList: List<AccountBookClass> = emptyList()

        @SuppressLint("NotifyDataSetChanged")
        fun submitList(items: List<AccountBookClass>) {
            accountBookList = items
            filterListBySelectedDate()
            noData()
            notifyDataSetChanged()
        }

        private fun filterListBySelectedDate() {
            datefilteredList = accountBookList.filter { item ->
                // 2023-12
                val itemDate = LocalDate.parse(item.date.substring(0, 10)).toString()
                itemDate.substring(0, 7) == yearMonth.toString()
            }
            fragmentHomeAnalysisCategoryBinding.textViewHomeAnalysisCategorySumOfRow.text =
                "${datefilteredList.size}건"
            var amount = datefilteredList.filter {
                it.budregi_yn == 0
            }.sumOf { it.amount }
            var moneyType = MoneyType()
            fragmentHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryTotalOfRow.text =
                moneyType.moneyText(amount.toString())
        }

        private fun noData() {
            if (datefilteredList.isEmpty()) {
                fragmentHomeAnalysisCategoryBinding.recyclerViewHomeAnalysisCategory.visibility =
                    View.GONE
                fragmentHomeAnalysisCategoryBinding.linearLayoutHomeAnalysisCategoryCount.visibility =
                    View.GONE
                fragmentHomeAnalysisCategoryBinding.linearLayoutHomeAnalysisCategoryTextNodata.visibility =
                    View.VISIBLE
            } else {
                fragmentHomeAnalysisCategoryBinding.recyclerViewHomeAnalysisCategory.visibility =
                    View.VISIBLE
                fragmentHomeAnalysisCategoryBinding.linearLayoutHomeAnalysisCategoryCount.visibility =
                    View.VISIBLE
                fragmentHomeAnalysisCategoryBinding.linearLayoutHomeAnalysisCategoryTextNodata.visibility =
                    View.GONE
            }
        }

        inner class HomeAnalysisCategoryViewHolder(val rowHomeAnalysisCategoryBinding: RowHomeAnalysisCategoryBinding) :
            RecyclerView.ViewHolder(rowHomeAnalysisCategoryBinding.root) {

            init {
                rowHomeAnalysisCategoryBinding.root.setOnClickListener {
                    val dialogAccountDetail = DialogAccountDetail(requireContext(), layoutInflater,inCategory,outCategory)
                    dialogAccountDetail.setOnDialogDismissListener(this@HomeAnalysisCategoryFragment)
                    dialogAccountDetail.callFunction(datefilteredList[position])
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): HomeAnalysisCategoryViewHolder {
            val rowHomeAnalysisCategoryBinding = RowHomeAnalysisCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            val homeAnalysisCategoryViewHolder =
                HomeAnalysisCategoryViewHolder(rowHomeAnalysisCategoryBinding)
            rowHomeAnalysisCategoryBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return homeAnalysisCategoryViewHolder
        }

        override fun onBindViewHolder(holder: HomeAnalysisCategoryViewHolder, position: Int) {
            holder.rowHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryContent.text =
                datefilteredList[position].content
            var moneyType = MoneyType()
            var amount =
                moneyType.moneyText(datefilteredList[position].amount.toString())

            var settingDate = LocalDateTime.parse(datefilteredList[position].date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            holder.rowHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryDate.text = settingDate
            when (datefilteredList[position].isInorOut) {
                'i' -> {
                    holder.rowHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryAmount.text =
                        "+$amount"

                    holder.rowHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryAmount.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.accentGreen)
                    )
                }
                'o' -> {
                    holder.rowHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryAmount.text =
                        "-$amount"

                    holder.rowHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryAmount.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.accentRed)
                    )
                }
            }
            if (datefilteredList[position].budregi_yn == 1) {
                holder.rowHomeAnalysisCategoryBinding.textViewHomeAnalysisCategoryAmount.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.lightGray)
                )
            }
        }

        override fun getItemCount(): Int {
            return datefilteredList.size
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        accountBookViewModel.getAllAccountBookList(uIdx)
        accountBookViewModel.getMonthAccountBookList(uIdx, yearMonth.toString())
        accountBookViewModel.getDayOfMonthAccountBookList(uIdx, yearMonth.toString())
        accountBookViewModel.getMonthCategoryAccountBookList(uIdx, inOrOut, category)

        accountBookViewModel.allAccountBookList.observe(viewLifecycleOwner) {
            adapter.submitList(categoryList)
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogDismissed() {
        viewLifecycleOwner.lifecycleScope.launch {
            accountBookViewModel.fetchOutData(uIdx)
            accountBookViewModel.fetchInData(uIdx)
            accountBookViewModel.getAllAccountBookList(uIdx)
            accountBookViewModel.getMonthAccountBookList(uIdx, yearMonth.toString())
            accountBookViewModel.getDayOfMonthAccountBookList(uIdx, yearMonth.toString())
            accountBookViewModel.getMonthCategoryAccountBookList(uIdx, inOrOut, category)
            accountBookViewModel.allAccountBookList.observe(viewLifecycleOwner){
                val latestData = accountBookViewModel.monthCategoryAccountBook.value ?: emptyList()
                adapter.submitList(latestData)
                adapter.notifyDataSetChanged()
            }
        }


    }



}
