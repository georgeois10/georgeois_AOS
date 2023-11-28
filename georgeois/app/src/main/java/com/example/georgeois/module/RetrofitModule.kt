package com.example.georgeois.module

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitModule {
    private const val BASE_URL = "http://3.34.98.165/"

    private val _retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    /**
     * Retrofit.Build() 반환
     * 사용방법 : retrofit().create(Api Interface)
     *
     * 예시
     * @see com.example.georgeois.repository.UserRepository 중 일부
     * val retrofit = RetrofitModule.retrofit().create(UserApiService::class.java)
     * retrofit.checkIdDuplication(id)
     */
    fun retrofit(): Retrofit {
        return _retrofit
    }
}