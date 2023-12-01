package com.example.georgeois.repository

import android.util.Log
import com.example.georgeois.dataclass.JoinUser
import com.example.georgeois.module.RetrofitModule
import com.example.georgeois.service.UserApiService
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class UserRepository {

    companion object {
        private val retrofit = RetrofitModule.retrofit().create(UserApiService::class.java)

        suspend fun checkIdDuplication(id: String) : Map<String, Boolean> {
            return retrofit.checkIdDuplication(id).await()
        }

        suspend fun checkNickNmDuplication(nickNm: String) : Map<String, Boolean> {
            return retrofit.checkNickNmDuplication(nickNm).await()
        }

        suspend fun login(id: String, pw: String) : Map<String, Map<String, Any>> {
            return retrofit.login(id, pw).await()
        }

        fun join(joinUser: JoinUser) : Call<ResponseBody> {

//            val result = retrofit.join(joinUser)
//
//
//            result.enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
//                    if (response.isSuccessful) {
//                        Log.d("mytag- 성공", "${response.body()?.string()}")
//                    } else {
//                        Log.d("mytag", "에러남${response.message()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    Log.d("mytag", "실패")
//                }
//
//            })
            return retrofit.join(joinUser)


//            return retrofit.join(joinUser)
        }
//            return retrofit.join(
//                joinUser.auth,
//                joinUser.profile,
//                joinUser.id,
//                joinUser.pw,
//                joinUser.nm,
//                joinUser.nicknm,
//                joinUser.pnumber,
//                joinUser.gender,
//                joinUser.birth.toString(),
//                joinUser.email
//            ).await()
//        suspend fun join(joinUser: JoinUser) : Map<String, Boolean> { //
//            return retrofit.join(joinUser).await()
//        }
    }
}