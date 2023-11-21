package com.example.georgeois.repository

import com.example.georgeois.dataclass.ChatingContent
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ChatRepository {
    companion object{
        fun getAllChattingRoom(callback: (Task<QuerySnapshot>) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection("ChatRoom")
                .get()
                .addOnCompleteListener(callback)
        }
        fun getChatting(callback: (Task<QuerySnapshot>, MutableList<ChatingContent>) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val chatContentList = mutableListOf<ChatingContent>() // 채팅 내용을 저장할 리스트

            db.collection("ChatRoom")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            // ChatRoom 컬렉션의 각 문서에 대한 참조를 얻어옴
                            val chatRoomId = document.id
                            val chatRoomRef = db.collection("ChatRoom").document(chatRoomId)

                            // "chattingContent" 하위 컬렉션에서 데이터 가져오기
                            chatRoomRef.collection("chattingContent")
                                .get()
                                .addOnCompleteListener { subTask ->
                                    if (subTask.isSuccessful) {
                                        for (chatDocument in subTask.result!!) {
                                            val chatContent = chatDocument.getString("chatContent")!!
                                            val chatTime = chatDocument.getString("chatTime")!!
                                            val chatUserNickname = chatDocument.getString("userNickname")!!

                                            val chatting = ChatingContent(chatContent,chatTime,chatUserNickname)

                                            // chatMessage를 chatContentList에 추가
                                            chatContentList.add(chatting)
                                        }
                                    } else {
                                        // "chattingContent" 데이터를 가져오는데 실패한 경우
                                        // 오류 처리를 수행
                                    }

                                    // 콜백 함수 호출 시 chatContentList를 함께 전달
                                    chatContentList.sortBy {it.chatTime}
                                    callback(task, chatContentList)
                                }
                        }
                    } else {
                        // ChatRoom 컬렉션 데이터를 가져오는데 실패한 경우
                        // 오류 처리를 수행
                        // 콜백 함수 호출
                        chatContentList.sortBy {it.chatTime}
                        callback(task, chatContentList)
                    }
                }
        }
    }
}