package com.example.georgeois.service

import com.example.georgeois.dataclass.InAccountBookClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface InAccountBookService {
    @Headers("Content-Type: application/json")
    @POST("insertInAccountBook.php")
    fun insertInAccountBook(@Body inAccountBook: InAccountBookClass) : Call<ResponseBody>
}
