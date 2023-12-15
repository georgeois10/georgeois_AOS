package com.example.georgeois.repository

import android.util.Log
import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.module.RetrofitModule
import com.example.georgeois.service.BoardService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class BoardRepository {
    companion object {
        private val retrofit = RetrofitModule.retrofit().create(BoardService::class.java)
        fun insertBoard(boardClass: BoardClass): Call<ResponseBody> {
            val result = retrofit.insertBoard(boardClass)
            result.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.d("php board 추가", "${response.body()?.string()}")
                    } else {
                        Log.d("php board 추가", "에러남${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("php 추가", "실패")
                }

            })
            return result
        }
        suspend fun getBoardList(): List<BoardClass> {
            return retrofit.selectBoard().awaitResponse().body()?.get("board") ?: emptyList()
        }
    }


}