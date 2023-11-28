package com.example.georgeois.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.georgeois.dataclass.ChatList
import com.example.georgeois.dataclass.ChatingContent
import com.example.georgeois.repository.ChatRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {
    //유저 리스트
    var chatUserList = MutableLiveData<ArrayList<String>>()
    //채팅방 이름
    var chatRoomName = MutableLiveData<String>()
    //설정한 출생년도
    var chatBirth = MutableLiveData<String>()
    //예산
    var chatBudget = MutableLiveData<Int>()
    //설정한 성별
    var chatGender = MutableLiveData<String>()
    //방장이름
    var chatOwnerNickname = MutableLiveData<String>()
    //채팅 내용
    var chatContent = MutableLiveData<MutableList<ChatingContent>>()
    //채팅방 문서 ID
    var chatRoomId = MutableLiveData<String>()
    //채팅방 리스트
    var chatRoomList = MutableLiveData<MutableList<ChatList>>()

    fun getMyChatRoomList(userNickname: String) {
        viewModelScope.launch {
            try {
                val tempChatRoomList = mutableListOf<ChatList>()
                // 비동기적으로 Firestore에서 데이터 가져오기
                val snapshot = ChatRepository.getAllChattingRoom(userNickname)
                for (document in snapshot.documents) {
                    val tempChatRoomName = document.getString("chatRoomName")!!
                    val roomId = document.id

                    val chatingContents = ChatRepository.getLastChatting(roomId)

                    val tempLastChatting = if (chatingContents.isNotEmpty()) {
                        chatingContents.last().chatContent
                    } else {
                        ""
                    }
                    val chatList = ChatList(tempChatRoomName, tempLastChatting, roomId)
                    tempChatRoomList.add(chatList)
                }
                // LiveData를 사용하여 UI에 데이터 업데이트
                chatRoomList.postValue(tempChatRoomList)
            } catch (e: Exception) {
                // 오류 처리
                Log.e("Error", "Error fetching chat room list: ${e.message}")
            }
        }
    }

    fun getChatRoom(currentChatRoomId:String, userNickname: String){
        viewModelScope.launch {
            val snapshot = async { ChatRepository.getAllChattingRoom(userNickname)}
                for (document in snapshot.await().documents) {
                    if (document.id == currentChatRoomId) {
                        chatUserList.value = document.get("chatUserList") as ArrayList<String>
                        chatRoomName.value = document.getString("chatRoomName")
                        chatBirth.value = document.getString("chatBirth")
                        chatBudget.value = document.getLong("chatBudget")!!.toInt()
                        chatGender.value = document.getString("chatGender")
                        chatOwnerNickname.value = document.getString("chatOwnerNickname")
                        chatRoomId.value = document.id
                    }
                }
            val chatingContents = ChatRepository.getLastChatting(currentChatRoomId)
            chatContent.value = chatingContents
        }
    }

    fun refreshChatting(chatRoomId: String, chatContentInfo: ChatingContent){
        ChatRepository.addNewChatting(chatRoomId,chatContentInfo){
            if(it == true){
                ChatRepository.getChatting(chatRoomId){ task, chatContentList ->
                    if (task.isSuccessful) {
                        // chatContentList를 chatContent LiveData에 할당하여 UI에 업데이트
                        chatContent.value = chatContentList
                    } else {
                    }

                }
            }
        }
    }

}