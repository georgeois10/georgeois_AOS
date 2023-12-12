package com.example.georgeois.service

import com.example.georgeois.dataclass.JoinUser
import com.example.georgeois.resource.FindUserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface UserApiService {

    @GET("checkUserIdDuplication.php")
    fun checkIdDuplication(@Query("id") id: String) : Call<Map<String, Boolean>>

    @GET("checkUserNickNmDuplication.php")
    fun checkNickNmDuplication(@Query("nickNm") nickNm: String) : Call<Map<String, Boolean>>

    @FormUrlEncoded
    @POST("login.php")
    fun login(@Field("id") id: String, @Field("pw") pw: String) : Call<Map<String, Map<String, Any>>>

    @POST("joinUser.php")
    fun join(@Body joinUser: JoinUser) : Call<ResponseBody>

    @Multipart
    @POST("saveProfileImage.php")
    fun saveProfile(@Part("id") id: RequestBody, @Part profile: MultipartBody.Part) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("findIdByPhoneNumber.php")
    fun findIdByPhoneNumber(@Field("pnumber") pnumber: String) : Call<FindUserResponse>

    @FormUrlEncoded
    @POST("findByIdAndPhoneNumber.php")
    fun findByIdAndPhoneNumber(@Field("id") id: String, @Field("pnumber") pnumber: String) : Call<FindUserResponse>

    @FormUrlEncoded
    @POST("resetPassword.php")
    fun resetPassword(@Field("idx") idx: Int,@Field("nicknm") nickname: String, @Field("pw") password: String) : Call<ResponseBody>


}