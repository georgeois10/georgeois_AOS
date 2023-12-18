package com.example.georgeois.service

import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.repository.BoardRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
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

    @FormUrlEncoded
    @POST("selectIdxBoard.php")
    fun selectIdxBoard(@Field("u_Idx") idx: Int): Call<Map<String, List<BoardClass>>>

    @FormUrlEncoded
    @POST("delynBoard.php")
    fun delynBoard(@Field("idx") idx: Int): Call<ResponseBody>


    @Headers("Content-Type: application/json")
    @POST("updateBoard.php")
    fun updateBoard(@Body boardClass: BoardClass): Call<ResponseBody>
}