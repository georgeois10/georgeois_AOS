package com.example.georgeois.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.georgeois.dataclass.ChatRoomInfo
import com.example.georgeois.dataclass.ChatingContent
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.coroutines.coroutineContext
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
                        .addOnCompleteListener { userUpdateTask ->
                            if (userUpdateTask.isSuccessful) {
                                // chatUserList 업데이트 성공 시 chattingContent 추가
                                val currentTimeMillis = System.currentTimeMillis()
                                val date = Date(currentTimeMillis)
                                val dateFormat =
                                    SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", Locale.getDefault())
                                val currentTime = dateFormat.format(date)

                                db.collection("ChatRoom").document(roomId)
                                    .collection("chattingContent")
                                    .add(
                                        mapOf(
                                            "chatContent" to "${userNickname}님이 참여하였습니다.🎺🎺🎺",
                                            "chatTime" to currentTime,
                                            "userNickname" to "Notification from the Admin"
                                        )
                                    )
                                    .addOnCompleteListener { contentAddTask ->
                                        if (contentAddTask.isSuccessful) {
                                        } else {
                                            // chattingContent 추가 실패
                                        }
                                    }
                            } else {
                                // chatUserList 업데이트 실패
                            }
                        }

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

        //채팅방 가져오기
        suspend fun getChattingRoom(userNickname: String, roomId: String): QuerySnapshot {
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

        //채팅방 들어갈 때 정보 가져오기
        suspend fun getRoomInfo(
            roomId: String,
            callback: (Exception?, DocumentSnapshot?) -> Unit
        ): DocumentSnapshot? {
            val db = FirebaseFirestore.getInstance()

            return try {
                // 해당 문서를 가져옴
                val documentSnapshot = db.collection("ChatRoom")
                    .document(roomId)
                    .get()
                    .await()

                // addSnapshotListener를 사용하여 chatUserList의 실시간 업데이트 감시
                val listenerRegistration = db.collection("ChatRoom")
                    .document(roomId)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            callback(e, snapshot)
                            return@addSnapshotListener
                        }

                        // 변경된 데이터 처리 후, 콜백 호출
                        callback(null, snapshot)
                    }

                // 필요에 따라 리스너를 언제든지 해제할 수 있음
                // listenerRegistration.remove()

                documentSnapshot // 필요에 따라 반환값을 조정
            } catch (e: Exception) {
                // 오류 처리
                callback(e, null)
                null
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
                        // 에러가 발생한 경우
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        // 컬렉션이 존재하고 비어있지 않은 경우

                        chatContentList.clear()
                        // Process each document in the snapshot and add it to chatContentList
                        for (document in snapshot.documents) {
                            val chatContent = document.getString("chatContent") ?: ""
                            val chatUserNickname = document.getString("userNickname") ?: ""
                            val chatTime = document.getString("chatTime") ?: ""

                            // Create ChatingContent object and add it to the list
                            val chatingContent =
                                ChatingContent(chatContent, chatTime, chatUserNickname)
                            chatContentList.add(chatingContent)
                        }

                        val sortedList = chatContentList.sortedBy { it.chatTime }.toMutableList()
//                        for(i in sortedList){
//                            Log.d("aaaa","${i.chatTime}")
//                            Log.d("aaaa","${i.chatContent}")
//                        }
//                        val sortedList = chatContentList.sortedBy { chatContent ->
//                            SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", Locale.getDefault())
//                                .parse(chatContent.chatTime) ?: Date(0)
//                        }.toMutableList()

                        callback(null, sortedList)
                    } else {
                        // 컬렉션이 존재하지 않거나 비어있는 경우

                        callback(null, mutableListOf()) // 빈 리스트 전달 또는 적절한 처리 수행
                    }
                }
        }

        suspend fun getLastChatting(roomId: String): String {

            val db = FirebaseFirestore.getInstance()
            val chatContentList = mutableListOf<ChatingContent>()
            var sortedList = mutableListOf<ChatingContent>()
            val snapshot = db.collection("ChatRoom")
                .document(roomId)
                .collection("chattingContent")
                .get()
                .await()

            for (document in snapshot) {
                val chatContent = document.getString("chatContent") ?: ""
                val chatUserNickname = document.getString("userNickname") ?: ""
                val chatTime = document.getString("chatTime") ?: ""

                val chatingContent = ChatingContent(chatContent, chatTime, chatUserNickname)
                chatContentList.add(chatingContent)
            }
            sortedList = chatContentList.sortedBy { it.chatTime }.toMutableList()


//            sortedList = chatContentList.sortedBy { chatContent ->
//                SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", Locale.getDefault())
//                    .parse(chatContent.chatTime) ?: Date(0)
//            }.toMutableList()

            if (sortedList.isEmpty()) {

                return ""
            } else {

                return sortedList.last().chatContent
            }
        }

        suspend fun getLastChattingObserve(roomId: String, onChatContentChange: (String, String) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val chatContentList = mutableListOf<ChatingContent>()

            // Firestore에서 데이터 변경을 실시간으로 감지하는 리스너 등록
            db.collection("ChatRoom")
                .document(roomId)
                .collection("chattingContent")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // 오류 처리
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        chatContentList.clear()

                        for (document in snapshot.documents) {
                            val chatContent = document.getString("chatContent") ?: ""
                            val chatUserNickname = document.getString("userNickname") ?: ""
                            val chatTime = document.getString("chatTime") ?: ""

                            val chatingContent = ChatingContent(chatContent, chatTime, chatUserNickname)
                            chatContentList.add(chatingContent)
                        }

                        val sortedList = chatContentList.sortedBy { it.chatTime }

                        if (sortedList.isEmpty()) {
                            onChatContentChange("", roomId)
                        } else {
                            Log.d("aaaa","Repo roomId = $roomId")
                            Log.d("aaaa","Repo lastChat = ${sortedList.last().chatContent}")
                            onChatContentChange(sortedList.last().chatContent, roomId)
                        }
                    }
                }
        }

        fun exitMember(userNickname: String, roomId: String, isSelf: Boolean, isOwner: Boolean) {
            val db = FirebaseFirestore.getInstance()

            // ChatRoom 컬렉션의 특정 문서에 대한 참조를 얻어옴
            val chatRoomRef = db.collection("ChatRoom").document(roomId)
            var chatContent = ""

            val currentTimeMillis = System.currentTimeMillis()
            val date = Date(currentTimeMillis)
            // 시간을 원하는 형식의 문자열로 변환
            val dateFormat = SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", Locale.getDefault())
            val currnetTime = dateFormat.format(date)

            if (isSelf) {
                chatContent = "${userNickname}님이 나가셨습니다.😟"
            } else {
                chatContent = "${userNickname}님이 추방당했습니다."
            }


            if (isOwner) {
                chatRoomRef.update("chatUserList", FieldValue.arrayRemove(userNickname))
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            chatRoomRef.collection("chattingContent")
                                .add(
                                    mapOf(
                                        "chatContent" to chatContent,
                                        "chatTime" to currnetTime,
                                        "userNickname" to "Notification from the Admin"
                                    )
                                )
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        chatRoomRef.get().addOnSuccessListener {
                                            if (it.exists()) {
                                                val chatUserList =
                                                    it.get("chatUserList") as ArrayList<String>
                                                val nextOwner = chatUserList[0]
                                                val updates = hashMapOf<String, Any>(
                                                    "chatOwnerNickname" to nextOwner
                                                )
                                                chatRoomRef.update(updates).addOnSuccessListener {
                                                    chatRoomRef.collection("chattingContent")
                                                        .add(
                                                            mapOf(
                                                                "chatContent" to "${nextOwner}님이 방장이 되었습니다.🙏",
                                                                "chatTime" to currnetTime,
                                                                "userNickname" to "Notification from the Admin"
                                                            )
                                                        )
                                                        .addOnCompleteListener {

                                                        }
                                                }
                                            }
                                        }
                                    } else {
                                    }
                                }
                        }
                    }

            } else {
                chatRoomRef.update("chatUserList", FieldValue.arrayRemove(userNickname))
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            chatRoomRef.collection("chattingContent")
                                .add(
                                    mapOf(
                                        "chatContent" to chatContent,
                                        "chatTime" to currnetTime,
                                        "userNickname" to "Notification from the Admin"
                                    )
                                )
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                    } else {
                                    }
                                }
                        }
                    }
            }
        }
    }
}
