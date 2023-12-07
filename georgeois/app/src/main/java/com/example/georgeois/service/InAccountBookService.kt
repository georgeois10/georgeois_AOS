package com.example.georgeois.service

import com.example.georgeois.dataclass.InAccountBookClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface InAccountBookService {
    @Headers("Content-Type: application/json")
    @POST("insertInAccountBook.php")
    fun insertInAccountBook(@Body inAccountBook: InAccountBookClass) : Call<ResponseBody>
    @FormUrlEncoded
    @POST("selectInAccountBook.php")
    fun selectInAccountBook(@Field("idx") idx: Int): Call<Map<String, Map<String, Any>>>


}
