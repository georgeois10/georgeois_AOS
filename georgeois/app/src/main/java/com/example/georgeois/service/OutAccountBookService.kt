package com.example.georgeois.service

import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.dataclass.OutAccountBookClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OutAccountBookService {
    @Headers("Content-Type: application/json")
    @POST("insertOutAccountBook.php")
    fun insertOutAccountBook(@Body outAccountBook: OutAccountBookClass) : Call<ResponseBody>
}
