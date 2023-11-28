package com.example.georgeois.repository

import com.example.georgeois.dataclass.User
import com.example.georgeois.module.RetrofitModule
import com.example.georgeois.service.UserApiService
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

        fun requestJoin(user: User) {

        }
    }
}