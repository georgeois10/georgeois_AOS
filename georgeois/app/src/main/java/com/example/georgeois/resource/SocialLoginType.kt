package com.example.georgeois.resource

import com.kakao.sdk.user.model.User
import com.navercorp.nid.profile.data.NidProfile

sealed class SocialLoginType {
    data class NaverUser(val userInfo: NidProfile?): SocialLoginType()
    data class KakaoUser(val userInfo: User?): SocialLoginType()
}
