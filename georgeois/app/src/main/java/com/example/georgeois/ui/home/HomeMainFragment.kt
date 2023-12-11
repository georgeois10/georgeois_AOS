package com.example.georgeois.ui.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeMainBinding
import com.example.georgeois.databinding.RowHomeMainBinding
import com.example.georgeois.dataclass.AccountBookClass
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.DialogAccountDetail
import com.example.georgeois.utill.DialogDismissListener
import com.example.georgeois.utill.MoneyType
import com.example.georgeois.viewModel.AccountBookViewModel
import com.example.georgeois.viewModel.UserViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


class HomeMainFragment : Fragment(), DialogDismissListener {
    private lateinit var fragmentHomeMainBinding: FragmentHomeMainBinding
    lateinit var mainActivity: MainActivity
    lateinit var userViewModel: UserViewModel
    lateinit var accountBookViewModel : AccountBookViewModel
    private val today = LocalDate.now()
    private var selectedDate = today

    var uIdx = 0
    var inCategory = ""
    var outCategory = ""
    val moneyType = MoneyType()
    val adapter = HomeMainAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeMainBinding = FragmentHomeMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        accountBookViewModel = ViewModelProvider(requireActivity())[AccountBookViewModel::class.java]

        userViewModel.user.observe(viewLifecycleOwner) {
            uIdx = it!!.u_idx
            inCategory = it!!.u_in_ctgy
            outCategory = it!!.u_out_ctgy
            lifecycleScope.launch {
                accountBookViewModel.fetchInData(uIdx)
                accountBookViewModel.fetchOutData(uIdx)
                accountBookViewModel.getAllAccountBookList(uIdx)
                val month = fragmentHomeMainBinding.calendarViewHomeMain.findFirstVisibleMonth()?.yearMonth
                accountBookViewModel.getMonthAccountBookList(uIdx, month.toString())
                accountBookViewModel.getDayOfMonthAccountBookList(uIdx,month.toString())

            }
        }
        accountBookViewModel.allAccountBookList.observe(requireActivity()){ items->
            adapter.submitList(items)
        }
        accountBookViewModel.monthAccountBook.observe(viewLifecycleOwner) { monthAccountBook ->
            var inMoney = monthAccountBook.inAmount.toString()
            inMoney = moneyType.moneyText(inMoney)
            fragmentHomeMainBinding.textViewHomeMainInMoney.text = inMoney

            var outMoney = monthAccountBook.outAmount.toString()
            outMoney = moneyType.moneyText(outMoney)
            fragmentHomeMainBinding.textViewHomeMainOutMoney.text = outMoney

            var sumMoney = monthAccountBook.totalAmount.toString()
            sumMoney = moneyType.moneyText(sumMoney)
            fragmentHomeMainBinding.textViewHomeMainSumMoney.text = sumMoney
        }

        fragmentHomeMainBinding.run {

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

            val daysOfWeek = daysOfWeek()
            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(100)
            val endMonth = currentMonth.plusMonths(100)
            setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
        }
        return fragmentHomeMainBinding.root
    }

    inner class HomeMainAdapter() : RecyclerView.Adapter<HomeMainAdapter.HomeMainViewHolder>() {
        var accountBookList : List<AccountBookClass> = emptyList()
        var datefilteredList : List<AccountBookClass> = emptyList()
        private var selectedDate: LocalDate? = null
        @SuppressLint("NotifyDataSetChanged")
        fun submitList(items: List<AccountBookClass>) {
            accountBookList = items
            filterListBySelectedDate()
            notifyDataSetChanged()
        }

        fun setSelectedDate(selectedDate: LocalDate) {
            this.selectedDate = selectedDate
            filterListBySelectedDate()
            notifyDataSetChanged()
        }


        inner class HomeMainViewHolder(val rowHomeMainBinding: RowHomeMainBinding) :
            RecyclerView.ViewHolder(rowHomeMainBinding.root) {
            init {
                rowHomeMainBinding.root.setOnClickListener {
                    val dialogAccountDetail = DialogAccountDetail(requireContext(), layoutInflater,inCategory,outCategory)
                    dialogAccountDetail.setOnDialogDismissListener(this@HomeMainFragment)
                    dialogAccountDetail.callFunction(datefilteredList[position])
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
            holder.rowHomeMainBinding.textViewHomeMainContent.text = datefilteredList[position].content
            var amount = datefilteredList[position].amount
            holder.rowHomeMainBinding.textViewHomeMainAmount.text = moneyType.moneyText(amount.toString())
            holder.rowHomeMainBinding.textViewHomeMainCategory.text = datefilteredList[position].category
        }

        override fun getItemCount(): Int {
            return datefilteredList.size
        }
        private fun filterListBySelectedDate() {
            datefilteredList = accountBookList.filter { item ->
                val itemDate = LocalDate.parse(item.date.substring(0, 10))
                itemDate == selectedDate
            }
        }

    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,)
    {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val calendarDay = view.findViewById<TextView>(R.id.textView_calendar_day)
            val calendarDayIn = view.findViewById<TextView>(R.id.textView_calendar_day_in)
            val calendarDayOut = view.findViewById<TextView>(R.id.textView_calendar_day_out)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        fragmentHomeMainBinding.calendarViewHomeMain.dayBinder = object : MonthDayBinder<DayViewContainer>{
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date,container.calendarDay,container.calendarDayIn,container.calendarDayOut,data.position == DayPosition.MonthDate)
            }
            override fun create(view: View) = DayViewContainer(view)
        }
        fragmentHomeMainBinding.calendarViewHomeMain.monthScrollListener = {
            updateTitle()
        }
        fragmentHomeMainBinding.calendarViewHomeMain.setup(startMonth,endMonth,daysOfWeek.first())
        fragmentHomeMainBinding.calendarViewHomeMain.scrollToMonth(currentMonth)
    }
    @SuppressLint("ResourceAsColor")
    private fun bindDate(date: LocalDate, day: TextView, sumOfIn: TextView, sumOfOut: TextView, isSelectable: Boolean) {
        var dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2)
        day.text = date.dayOfMonth.toString()

        accountBookViewModel.dayOfMonthAccountBook.observe(viewLifecycleOwner) { dayAccountBookList ->
            var totalInAmount = 0
            var totalOutAmount = 0

            for (dayAccountBook in dayAccountBookList) {
                if (dayAccountBook.day == date.toString()) {
                    totalInAmount += dayAccountBook.inAmount
                    totalOutAmount += dayAccountBook.outAmount
                }
            }
            var moneyType = MoneyType()

            // Rounding logic for inAmount
            sumOfIn.text = if (totalInAmount > 0) moneyType.formatAmount(totalInAmount.toDouble()) else ""

            // Rounding logic for outAmount
            sumOfOut.text = if (totalOutAmount > 0) moneyType.formatAmount(totalOutAmount.toDouble()) else ""
        }
        if(isSelectable){
            when (selectedDate) {
                date -> {
                    day.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                    fragmentHomeMainBinding.textViewHomeMainDate.text = "${date.dayOfMonth}일 ${dayOfWeek}요일"
                }
                else -> {
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            day.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        }
                        Configuration.UI_MODE_NIGHT_NO -> {
                            day.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        }
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                            day.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        }
                    }
                }
            }
        }else{
            day.setTextColor(Color.GRAY)
        }

    }




    private fun dateClicked(date: LocalDate) {
        val currentSelection = selectedDate
        if (currentSelection == date) {
            fragmentHomeMainBinding.calendarViewHomeMain.notifyDateChanged(currentSelection)
        } else {
            selectedDate = date
            fragmentHomeMainBinding.calendarViewHomeMain.notifyDateChanged(date)
            adapter.setSelectedDate(selectedDate)

            (fragmentHomeMainBinding.recyclerViewHomeMain.adapter as? HomeMainAdapter)?.submitList(
                accountBookViewModel.allAccountBookList.value?: emptyList()
            )
            if (currentSelection != null) {
                fragmentHomeMainBinding.calendarViewHomeMain.notifyDateChanged(currentSelection)
            }
        }
    }



    private fun updateTitle() {
        val month = fragmentHomeMainBinding.calendarViewHomeMain.findFirstVisibleMonth()?.yearMonth ?: return
        fragmentHomeMainBinding.materialToolbarHomeMain.title = "${month.year}년 ${month.month.value}월"
        accountBookViewModel.getMonthAccountBookList(uIdx,month.toString())
        accountBookViewModel.getDayOfMonthAccountBookList(uIdx,month.toString())
    }

    override fun onResume() {
        super.onResume()
        val latestData = accountBookViewModel.allAccountBookList.value?: emptyList()
        adapter.submitList(latestData)
        adapter.setSelectedDate(selectedDate)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogDismissed() {
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.notifyDataSetChanged()
            accountBookViewModel.fetchOutData(uIdx)
            accountBookViewModel.fetchInData(uIdx)
            accountBookViewModel.getAllAccountBookList(uIdx)
            val month = fragmentHomeMainBinding.calendarViewHomeMain.findFirstVisibleMonth()?.yearMonth
            accountBookViewModel.getMonthAccountBookList(uIdx, month.toString())
            accountBookViewModel.getDayOfMonthAccountBookList(uIdx,month.toString())
            val latestData = accountBookViewModel.allAccountBookList.value ?: emptyList()
            adapter.submitList(latestData)
            adapter.setSelectedDate(selectedDate)
        }
    }





}