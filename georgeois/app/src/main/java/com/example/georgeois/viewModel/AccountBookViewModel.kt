package com.example.georgeois.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.georgeois.dataclass.AccountBookClass
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.dataclass.MonthAccountBookClass
import com.example.georgeois.dataclass.OutAccountBookClass
import com.example.georgeois.dataclass.DayAccountBookClass
import com.example.georgeois.repository.InAccountBookRepository
import com.example.georgeois.repository.OutAccountBookRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class AccountBookViewModel : ViewModel() {
    private val _inAccountBook = MutableLiveData<List<InAccountBookClass>>()
    val inAccountBook: LiveData<List<InAccountBookClass>> get() = _inAccountBook

    private val _outAccountBook = MutableLiveData<List<OutAccountBookClass>>()
    val outAccountBook: LiveData<List<OutAccountBookClass>> get() = _outAccountBook

    private val _allAccountBookList = MutableLiveData<List<AccountBookClass>>()
    val allAccountBookList: LiveData<List<AccountBookClass>> get() = _allAccountBookList

    private val _monthAccountBook = MutableLiveData<MonthAccountBookClass>()
    val monthAccountBook: LiveData<MonthAccountBookClass> get() = _monthAccountBook

    private val _dayOfMonthAccountBook = MutableLiveData<List<DayAccountBookClass>>()
    val dayOfMonthAccountBook : LiveData<List<DayAccountBookClass>> get() = _dayOfMonthAccountBook

    suspend fun fetchInData(idx: Int): List<InAccountBookClass> = viewModelScope.async {
        try {
            val result = InAccountBookRepository.getInAccountBook(idx)
            Log.e("테스트", "Data loaded successfully: $result")
            _inAccountBook.value = result
            result
        } catch (e: Exception) {
            Log.e("테스트", "Error loading data: ${e.message}")
            // Handle exceptions
            emptyList()
        }
    }.await()

    suspend fun fetchOutData(idx: Int): List<OutAccountBookClass> = viewModelScope.async {
        try {
            val result = OutAccountBookRepository.getOutAccountBook(idx)
            Log.e("테스트", "Data loaded successfully: $result")
            _outAccountBook.value = result
            result
        } catch (e: Exception) {
            Log.e("테스트", "Error loading data: ${e.message}")
            // Handle exceptions
            emptyList()
        }
    }.await()

    fun getAllAccountBookList(uIdx:Int) {
        viewModelScope.launch {
            val inAccountBookList = fetchInData(uIdx)
            val outAccountBookList = fetchOutData(uIdx)
            val allAccountBookList = mutableListOf<AccountBookClass>()
            for( i in inAccountBookList){
                i.i_idx?.let {
                    AccountBookClass('i',
                        it,uIdx,i.cre_user,i.u_nicknm,i.i_amount,i.i_content,i.i_category,i.i_date,i.i_imgpath,i.i_budregi_yn,null)
                }?.let { allAccountBookList.add(it) }
            }
            for( o in outAccountBookList){
                o.o_idx?.let {
                    AccountBookClass('o',
                        it,uIdx,o.cre_user,o.u_nicknm,o.o_amount,o.o_content,o.o_category,o.o_date,o.o_imgpath,o.o_budregi_yn,o.o_property)
                }?.let { allAccountBookList.add(it) }
            }
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            allAccountBookList.sortBy { LocalDateTime.parse(it.date, formatter) }
            _allAccountBookList.value = allAccountBookList
        }
    }

    fun getMonthAccountBookList(uIdx: Int, month: String) {
        viewModelScope.launch {
            val inAccountBookList = fetchInData(uIdx)
            val outAccountBookList = fetchOutData(uIdx)
            var inAmount = 0
            var outAmount = 0

            // month = 2023-12
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                inAmount = inAccountBookList.filter {
                    LocalDate.parse("${it.i_date.substring(0, 7)}-01", formatter) == LocalDate.parse("${month}-01", formatter)
                }.filter {
                    it.i_budregi_yn == 0
                }.sumOf { it.i_amount }


                outAmount = outAccountBookList.filter {
                    LocalDate.parse("${it.o_date.substring(0, 7)}-01", formatter) == LocalDate.parse("${month}-01", formatter)
                }.filter {
                    it.o_budregi_yn == 0
                }.sumOf { it.o_amount }

                val sum = inAmount - outAmount
                _monthAccountBook.value = MonthAccountBookClass(inAmount, outAmount, sum, month)
                Log.e("테스트_view", "$sum")
            } catch (e: DateTimeParseException) {
                Log.e("테스트_view", "Error parsing date: ${e.message}")
            }
        }
    }
    fun getDayOfMonthAccountBookList(uIdx: Int, month: String) {
        viewModelScope.launch {
            val inAccountBookList = fetchInData(uIdx)
            val outAccountBookList = fetchOutData(uIdx)

            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val dailyList = mutableListOf<DayAccountBookClass>()

                // 수입 항목 저장
                inAccountBookList.filter { it.i_budregi_yn == 0 }.forEach { inItem ->
                    try {
                        val transactionDate = LocalDate.parse(inItem.i_date.substring(0, 10), formatter)
                        if (transactionDate.monthValue == LocalDate.parse(month + "-01").monthValue) {
                            val dayKey = transactionDate.format(formatter)
                            val existingDay = dailyList.find { it.day == dayKey }
                            if (existingDay == null) {
                                dailyList.add(DayAccountBookClass(inItem.i_amount, 0, dayKey))
                            } else {
                                existingDay.inAmount += inItem.i_amount
                            }
                        }
                    } catch (e: DateTimeParseException) {
                        Log.e("테스트_month", "Error parsing date: ${e.message}")
                    }
                }

                // 지출 항목 저장
                outAccountBookList.filter { it.o_budregi_yn == 0 }.forEach { outItem ->
                    try {
                        val transactionDate = LocalDate.parse(outItem.o_date.substring(0, 10), formatter)
                        if (transactionDate.monthValue == LocalDate.parse(month + "-01").monthValue) {
                            val dayKey = transactionDate.format(formatter)
                            val existingDay = dailyList.find { it.day == dayKey }
                            if (existingDay == null) {
                                dailyList.add(DayAccountBookClass(0, outItem.o_amount, dayKey))
                            } else {
                                existingDay.outAmount += outItem.o_amount
                            }
                        }
                    } catch (e: DateTimeParseException) {
                        Log.e("테스트_month", "Error parsing date: ${e.message}")
                    }
                }

                _dayOfMonthAccountBook.value = dailyList
            } catch (e: DateTimeParseException) {
                Log.e("테스트_month", "Error parsing date: ${e.message}")
            }
        }
    }




}


