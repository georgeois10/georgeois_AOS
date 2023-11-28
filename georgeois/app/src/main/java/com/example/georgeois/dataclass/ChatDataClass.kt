package com.example.georgeois.dataclass

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
    val chatLastChatting: String,
    val chatRoomId: String
)