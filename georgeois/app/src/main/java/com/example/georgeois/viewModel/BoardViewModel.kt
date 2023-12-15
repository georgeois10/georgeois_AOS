package com.example.georgeois.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.repository.BoardRepository
import com.example.georgeois.repository.InAccountBookRepository
import kotlinx.coroutines.async

class BoardViewModel  : ViewModel(){
    private val _allBoardList = MutableLiveData<List<BoardClass>>()
    val allBoardList: LiveData<List<BoardClass>> get() = _allBoardList

    suspend fun fetchAllBoard(): List<BoardClass> = viewModelScope.async {
        try {
            val result = BoardRepository.getBoardList()
            Log.e("테스트", "Data loaded successfully: $result")
            _allBoardList.value = result
            result
        } catch (e: Exception) {
            Log.e("테스트", "Error loading data: ${e.message}")
            // Handle exceptions
            emptyList()
        }
    }.await()

}