package com.example.georgeois.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.dataclass.CommentClass
import com.example.georgeois.repository.BoardRepository
import com.example.georgeois.repository.CommentRepository
import kotlinx.coroutines.async

class CommentViewModel : ViewModel() {
    private val _commtentList = MutableLiveData<List<CommentClass>>()
    val commentList: LiveData<List<CommentClass>> get() = _commtentList
    suspend fun fetchComment(bIdx:Int): List<CommentClass> = viewModelScope.async {
        try {
            val result = CommentRepository.getComment(bIdx)
            Log.e("테스트", "Data loaded successfully: $result")
            _commtentList.value = result
            result
        } catch (e: Exception) {
            Log.e("테스트", "Error loading data: ${e.message}")
            emptyList()
        }
    }.await()
}