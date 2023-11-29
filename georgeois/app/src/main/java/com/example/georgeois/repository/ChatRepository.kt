package com.example.georgeois.repository

import android.util.Log
import com.example.georgeois.dataclass.ChatRoomInfo
import com.example.georgeois.dataclass.ChatingContent
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatRepository {
    companion object {
        //채팅방 생성
        fun addNewChatRoom(
            chatRoomInfo: ChatRoomInfo,
            callback: (Task<DocumentReference>) -> Unit
        ) {
            // Firestore 초기화
            val db = FirebaseFirestore.getInstance()

            // 사용자 정보 입력
            val chatRoom = HashMap<String, Any>()
            chatRoom["chatBirth"] = chatRoomInfo.chatBirth
            chatRoom["chatBudget"] = chatRoomInfo.chatBudget
            chatRoom["chatGender"] = chatRoomInfo.chatGender
            chatRoom["chatOwnerNickname"] = chatRoomInfo.chatOwnerNickname
            chatRoom["chatRoomName"] = chatRoomInfo.chatRoomName
            chatRoom["chatUserList"] = chatRoomInfo.chatUserList

            // Firestore 컬렉션에 추가
            db.collection("ChatRoom")
                .add(chatRoom)
                .addOnCompleteListener(callback)
        }

        //채팅 추가
        fun addNewChatting(
            chatRoomId: String,
            chatContent: ChatingContent,
            callback: (Boolean) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()

            // ChatRoom 컬렉션의 특정 문서에 대한 참조를 얻어옴
            val chatRoomRef = db.collection("ChatRoom").document(chatRoomId)

            // "chattingContent" 하위 컬렉션에 새로운 데이터 추가
            chatRoomRef.collection("chattingContent")
                .add(mapOf(
                        "chatContent" to chatContent.chatContent,
                        "chatTime" to chatContent.chatTime,
                        "userNickname" to chatContent.chatUserNickname
                    )
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 데이터 추가 성공
                        callback(true)
                    } else {
                        // 데이터 추가 실패
                        // 오류 처리를 수행
                        callback(false)
                    }
                }
        }

        //채팅방 가져오기
        suspend fun getChattingRoom(userNickname: String): QuerySnapshot {
            val db = FirebaseFirestore.getInstance()
            try {
                // whereArrayContains를 사용하여 chatUserList에 userNickname이 포함된 문서 가져오기
                return db.collection("ChatRoom")
                    .whereArrayContains("chatUserList", userNickname)
                    .get()
                    .await()
            } catch (e: Exception) {
                // 오류 처리
                Log.e("Error", "Error fetching chat room list: ${e.message}")
                throw e
            }
        }

        //검색한 오픈채팅방
        suspend fun getSearchChattingRoom(content: String): List<DocumentSnapshot> {
            val db = FirebaseFirestore.getInstance()
            try {
                val querySnapshot = db.collection("ChatRoom")
                    .get()
                    .await()

                // 결과를 필터링하여 부분적으로 일치하는 경우만 반환
                return querySnapshot.documents.filter { document ->
                    val chatRoomName = document.getString("chatRoomName")
                    chatRoomName?.replace(" ","")!!.contains(content) == true
                }
            } catch (e: Exception) {
                // 오류 처리
                Log.e("Error", "Error fetching chat room list: ${e.message}")
                throw e
            }
        }

        //모든 채팅방
        suspend fun getAllChattingRoom():QuerySnapshot{
            val db = FirebaseFirestore.getInstance()
            try {
                // whereArrayContains를 사용하여 chatUserList에 userNickname이 포함된 문서 가져오기
                return db.collection("ChatRoom")
                    .get()
                    .await()
            } catch (e: Exception) {
                // 오류 처리
                Log.e("Error", "Error fetching chat room list: ${e.message}")
                throw e
            }
        }


        //채팅 가져오기
        fun getChatting(roomId: String,callback: (Task<QuerySnapshot>, MutableList<ChatingContent>) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val chatContentList = mutableListOf<ChatingContent>() // 채팅 내용을 저장할 리스트

            db.collection("ChatRoom").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // ChatRoom 컬렉션의 각 문서에 대한 참조를 얻어옴
                    val chatRoomId = roomId
                    val chatRoomRef = db.collection("ChatRoom").document(chatRoomId)

                    // "chattingContent" 하위 컬렉션에서 데이터 가져오기
                    chatRoomRef.collection("chattingContent").get()
                        .addOnCompleteListener { subTask ->
                            if (subTask.isSuccessful) {
                                for (chatDocument in subTask.result!!) {
                                    val chatContent = chatDocument.getString("chatContent")!!
                                    val chatTime = chatDocument.getString("chatTime")!!
                                    val chatUserNickname =
                                        chatDocument.getString("userNickname")!!

                                    val chatting = ChatingContent(
                                        chatContent,
                                        chatTime,
                                        chatUserNickname
                                    )

                                    // chatMessage를 chatContentList에 추가
                                    chatContentList.add(chatting)
                                }
                                // 콜백 함수 호출 시 chatContentList를 함께 전달
                                chatContentList.sortBy { it.chatTime }
                                callback(task, chatContentList)
                            } else {
                                chatContentList.clear()
                                callback(task, chatContentList)
                            }
                        }
                }
            }
        }

        //채팅 가져오기
        suspend fun getLastChatting(roomId: String): MutableList<ChatingContent> = suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val chatContentList = mutableListOf<ChatingContent>()

            val chatRoomRef = db.collection("ChatRoom").document(roomId)

            // "chattingContent" 하위 컬렉션에서 데이터 가져오기
            chatRoomRef.collection("chattingContent").get()
                .addOnCompleteListener { subTask ->
                    if (subTask.isSuccessful) {
                        for (chatDocument in subTask.result!!) {
                            val chatContent = chatDocument.getString("chatContent")!!
                            val chatTime = chatDocument.getString("chatTime")!!
                            val chatUserNickname = chatDocument.getString("userNickname")!!

                            val chatting = ChatingContent(
                                chatContent,
                                chatTime,
                                chatUserNickname
                            )

                            // chatMessage를 chatContentList에 추가
                            chatContentList.add(chatting)
                        }
                        // 콜백 함수 호출 시 chatContentList를 반환
                        chatContentList.sortBy { it.chatTime }
                        continuation.resume(chatContentList)
                    } else {
                        // 실패한 경우 오류 처리
                        chatContentList.clear()
                        continuation.resumeWithException(subTask.exception ?: Exception("Task failed"))
                    }
                }
        }
    }
}