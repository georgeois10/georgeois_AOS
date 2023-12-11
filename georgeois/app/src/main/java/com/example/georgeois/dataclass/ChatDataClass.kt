package com.example.georgeois.dataclass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.ArrayList

data class ChatingContent(
    val chatContent: String,
    val chatTime: String,
    val chatUserNickname: String
)

data class ChatRoomInfo(
    val chatBirth: String,
    val chatBudget: Int,
    val chatGender: String,
    val chatOwnerNickname: String,
    val chatRoomName: String,
    val chatUserList: ArrayList<String>
)

data class ChatList(
    val chatRoomName: String,
    var chatLastChatting: String,
    var chatRoomId: String
)

data class ChatRoomInfoSearch(
    var chatRoomName:String,
    var chatRoomBirth : String,
    var chatRoomBudget : Int,
    var chatRoomCount : Int,
    var chatRoomGender : String,
    var chatRoomId : String
)

data class LastChatting(
    var chatLastContent : String,
    var chatRoomId : String
)