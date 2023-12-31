package com.example.georgeois.repository

import android.util.Log
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.module.RetrofitModule
import com.example.georgeois.service.InAccountBookService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class InAccountBookRepository {
    companion object {
        private val retrofit = RetrofitModule.retrofit().create(InAccountBookService::class.java)

        fun insertInAccountBook(inAccountBook:InAccountBookClass):Call<ResponseBody>{
            val result = retrofit.insertInAccountBook(inAccountBook)
            result.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.d("php 추가", "${response.body()?.string()}")
                    } else {
                        Log.d("php 추가", "에러남${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("php 추가", "실패")
                }

            })
            return result
        }
        suspend fun getInAccountBook(idx: Int): List<InAccountBookClass> {
            return retrofit.selectInAccountBook(idx).awaitResponse().body()?.get("inAccountBook") ?: emptyList()
        }

        fun deleteInAccountBook(idx:Int):Call<ResponseBody>{
            val result = retrofit.delynInAccountBook(idx)
            result.enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.d("php delyn변경", "${response.body()?.string()}")
                    } else {
                        Log.e("php delyn변경", "에러남 ${response.code()}: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("php delyn변경", "실패")
                }

            })
            return result
        }

        fun updateInAccountBook(inAccountBook: InAccountBookClass):Call<ResponseBody> {
            val result = retrofit.updateInAccountBook(inAccountBook)
            result.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.d("php update", "${response.body()?.string()}")
                    } else {
                        Log.e("php update", "에러남 ${response.code()}: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("php update", "실패")
                }

            })
            return result
        }

    }
}
