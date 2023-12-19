package com.example.georgeois.repository

import android.util.Log
import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.dataclass.CommentClass
import com.example.georgeois.module.RetrofitModule
import com.example.georgeois.service.BoardService
import com.example.georgeois.service.CommentService
import okhttp3.ResponseBody
import org.w3c.dom.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class CommentRepository {

    companion object {
        private val retrofit = RetrofitModule.retrofit().create(CommentService::class.java)
        fun inserComment(commentClass: CommentClass): Call<ResponseBody> {
            val result = retrofit.insertComment(commentClass)
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
        suspend fun getComment(bIdx:Int): List<CommentClass> {
            return retrofit.selectComment(bIdx).awaitResponse().body()?.get("comment") ?: emptyList()
        }


        fun deleteComment(idx:Int):Call<ResponseBody>{
            val result = retrofit.delynComment(idx)
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

    }
}