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
                allAccountBookList.add(AccountBookClass('i',i.i_amount,i.i_content,i.i_category,i.i_date,i.i_imgpath,i.i_budregi_yn,null))
            }
            for( o in outAccountBookList){
                allAccountBookList.add(AccountBookClass('o',o.o_amount,o.o_content,o.o_category,o.o_date,o.o_imgpath,o.o_budregi_yn,o.o_property))
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
                }.sumOf { it.i_amount }

                outAmount = outAccountBookList.filter {
                    LocalDate.parse("${it.o_date.substring(0, 7)}-01", formatter) == LocalDate.parse("${month}-01", formatter)
                }.sumOf { it.o_amount }

                val sum = inAmount - outAmount
                _monthAccountBook.value = MonthAccountBookClass(inAmount, outAmount, sum, month)
                Log.e("테스트_view", "$sum")
            } catch (e: DateTimeParseException) {
                Log.e("테스트_view", "Error parsing date: ${e.message}")
            }




        }
    }


}


