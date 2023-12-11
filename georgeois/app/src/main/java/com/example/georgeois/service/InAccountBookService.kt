package com.example.georgeois.service

import com.example.georgeois.dataclass.InAccountBookClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface InAccountBookService {
    @Headers("Content-Type: application/json")
    @POST("insertInAccountBook.php")
    fun insertInAccountBook(@Body inAccountBook: InAccountBookClass) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("selectInAccountBook.php")
    fun selectInAccountBook(@Field("uIdx") idx: Int): Call<Map<String, List<InAccountBookClass>>>

    @FormUrlEncoded
    @POST("delynInAccountBook.php")
    fun delynInAccountBook(@Field("idx") idx: Int): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("updateInAccountBook.php")
    fun updateInAccountBook(@Body inAccountBook:InAccountBookClass): Call<ResponseBody>
}
