package com.example.georgeois.service

import com.example.georgeois.dataclass.BoardClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface BoardService {
    @Headers("Content-Type: application/json")
    @POST("insertBoard.php")
    fun insertBoard(@Body boardClass: BoardClass) : Call<ResponseBody>


    @GET("selectBoard.php")
    fun selectBoard(): Call<Map<String, List<BoardClass>>>


}