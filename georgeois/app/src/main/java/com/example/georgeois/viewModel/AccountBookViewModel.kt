package com.example.georgeois.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.dataclass.OutAccountBookClass
import com.example.georgeois.repository.InAccountBookRepository
import kotlinx.coroutines.launch
import org.json.JSONObject


class AccountBookViewModel : ViewModel() {
    private val _inAccountBooks = MutableLiveData<List<InAccountBookClass>>()
    val inAccountBooks: LiveData<List<InAccountBookClass>> = _inAccountBooks

    fun getInAccountBooks(idx: Int) {
        viewModelScope.launch {
            try {
                val result = InAccountBookRepository.selectAllInAccountBook(idx)

                if (result is List<*>) {
                    val inAccountBooksList = mutableListOf<InAccountBookClass>()

                    for (item in result) {
                        if (item is Map<*, *>) {
                            val inAccountBookMap = item["inAccountBook"] as? Map<*, *>

                            if (inAccountBookMap != null) {
                                val json = JSONObject(inAccountBookMap)

                                val inMap = InAccountBookClass(
                                    json.getInt("u_idx"),
                                    json.getString("u_id"),
                                    json.getString("u_nicknm"),
                                    json.getInt("i_amount"),
                                    json.getString("i_content"),
                                    json.getString("i_category"),
                                    json.getString("i_date"),
                                    json.getString("i_imgpath"),
                                    json.getBoolean("i_budregiyn")
                                )
                                inAccountBooksList.add(inMap)
                            }
                        }
                    }

                    _inAccountBooks.postValue(inAccountBooksList)
                } else {
                    Log.e("테스트", "Unexpected result type: $result")
                }
            } catch (e: Exception) {
                Log.e("테스트", "Error: ${e.message}")
            }
        }
    }
}
