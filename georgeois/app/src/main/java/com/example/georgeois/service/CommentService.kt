package com.example.georgeois.service

import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.dataclass.CommentClass
import okhttp3.ResponseBody
import org.w3c.dom.Comment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface CommentService {

    @Headers("Content-Type: application/json")
    @POST("insertComment.php")
    fun insertComment(@Body commentClass: CommentClass) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("selectComment.php")
    fun selectComment(@Field("b_Idx") idx: Int): Call<Map<String, List<CommentClass>>>

    @FormUrlEncoded
    @POST("delynComment.php")
    fun delynComment(@Field("idx") idx: Int): Call<ResponseBody>

}