package com.example.georgeois.service

import com.example.georgeois.dataclass.OutAccountBookClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface OutAccountBookService {
    @Headers("Content-Type: application/json")
    @POST("insertOutAccountBook.php")
    fun insertOutAccountBook(@Body outAccountBook: OutAccountBookClass) : Call<ResponseBody>


    @FormUrlEncoded
    @POST("selectOutAccountBook.php")
    fun selectOutAccountBook(@Field("uIdx") idx: Int): Call<Map<String, List<OutAccountBookClass>>>


    @FormUrlEncoded
    @POST("delynOutAccountBook.php")
    fun delynOutAccountBook(@Field("idx") idx: Int): Call<ResponseBody>


}
