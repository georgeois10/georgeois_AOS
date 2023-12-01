package com.example.georgeois.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.georgeois.dataclass.ChatRoomInfo
import com.example.georgeois.dataclass.ChatingContent
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
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
        suspend fun addNewMember(roomId: String, userNickname: String) {
            try {
                // Firestore 초기화
                val db = FirebaseFirestore.getInstance()

                // 해당 방의 문서 가져오기
                val roomDocument = db.collection("ChatRoom").document(roomId).get().await()

                // 해당 방의 문서가 존재하면
                if (roomDocument.exists()) {
                    // chatUserList에 userNickname 추가
                    db.collection("ChatRoom").document(roomId)
                        .update("chatUserList", FieldValue.arrayUnion(userNickname))
                        .await()
                } else {
                    // 방이 존재하지 않는 경우에 대한 처리
                    // 예를 들어, 사용자에게 알림 등을 보낼 수 있음
                }
            } catch (e: Exception) {
                // 예외 처리 (Firebase Firestore 작업 중 발생한 예외 처리)
                e.printStackTrace()
            }
        }

        //채팅 추가
        fun addNewChatting(chatRoomId: String, chatContent: ChatingContent, callback: (Boolean) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            // ChatRoom 컬렉션의 특정 문서에 대한 참조를 얻어옴
            val chatRoomRef = db.collection("ChatRoom").document(chatRoomId)

            // "chattingContent" 하위 컬렉션에 새로운 데이터 추가
            chatRoomRef.collection("chattingContent")
                .add(
                    mapOf(
                        "chatContent" to chatContent.chatContent,
                        "chatTime" to chatContent.chatTime,
                        "userNickname" to chatContent.chatUserNickname
                    )
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        callback(true)
                    } else {
                        // 데이터 추가 실패
                        // 오류 처리를 수행
                        callback(false)
                    }
                }
        }

        fun observeChattingUpdates(chatRoomId: String, callback: (List<ChatingContent>) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val chatRoomRef = db.collection("ChatRoom").document(chatRoomId)

            chatRoomRef.collection("chattingContent")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // 오류 처리
                        return@addSnapshotListener
                    }

                    val chattingContents = mutableListOf<ChatingContent>()

                    for (doc in snapshot!!.documents) {
                        val content = doc.toObject(ChatingContent::class.java)
                        content?.let { chattingContents.add(it) }
                    }

                    // 새로운 채팅 내용을 전달
                    callback(chattingContents)
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
                    chatRoomName?.replace(" ", "")!!.contains(content) == true
                }
            } catch (e: Exception) {
                // 오류 처리
                Log.e("Error", "Error fetching chat room list: ${e.message}")
                throw e
            }
        }

        //모든 채팅방
        suspend fun getAllChattingRoom(): QuerySnapshot {
            val db = FirebaseFirestore.getInstance()
            try {
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
        @RequiresApi(Build.VERSION_CODES.O)
        fun getChatting(
            roomId: String,
            callback: (Exception?, MutableList<ChatingContent>) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val chatContentList = mutableListOf<ChatingContent>()

            // ChatRoom 컬렉션의 특정 문서에 대한 참조를 얻어옴
            val chatRoomRef = db.collection("ChatRoom").document(roomId)

            // "chattingContent" 하위 컬렉션에 대한 실시간 업데이트를 감시
            chatRoomRef.collection("chattingContent")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // 오류 처리
                        callback(e, chatContentList)
                        return@addSnapshotListener
                    }

                    chatContentList.clear()

                    for (chatDocument in snapshot!!) {
                        val chatContent = chatDocument.getString("chatContent")!!
                        val chatTime = chatDocument.getString("chatTime")!!
                        val chatUserNickname = chatDocument.getString("userNickname")!!

                        val chatting = ChatingContent(chatContent, chatTime, chatUserNickname)

                        chatContentList.add(chatting)
                    }

                    // 콜백 함수 호출 시 chatContentList를 함께 전달
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm:ss")

                    val sortedList = chatContentList.sortedBy { LocalDateTime.parse(it.chatTime, formatter) }.toMutableList()
                    for(i in sortedList){
                        Log.d("bbbb","${i.chatContent}")
                        Log.d("bbbb","${i.chatUserNickname}")
                        Log.d("bbbb","${i.chatTime}")
                        Log.d("bbbb","+++++++++++++++++++++++++=============================+++++++++++++++++++++")
                    }
                    callback(null, sortedList)
                }
        }

        //마지막 채팅 가져오기
        suspend fun getLastChatting(roomId: String): MutableList<ChatingContent> =
            suspendCoroutine { continuation ->
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
                            continuation.resumeWithException(
                                subTask.exception ?: Exception("Task failed")
                            )
                        }
                    }
            }
    }
}