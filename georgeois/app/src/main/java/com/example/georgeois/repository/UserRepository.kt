package com.example.georgeois.repository

import com.example.georgeois.dataclass.JoinUser
import com.example.georgeois.module.RetrofitModule
import com.example.georgeois.service.UserApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.await

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
            return retrofit.join(joinUser)

//            return retrofit.join(joinUser)
        }

        fun saveProfile(id: RequestBody, profile: MultipartBody.Part): Call<ResponseBody> {
            return retrofit.saveProfile(id, profile)
        }

    }
}