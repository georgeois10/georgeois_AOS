package com.example.georgeois.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.georgeois.dataclass.ChatList
import com.example.georgeois.dataclass.ChatRoomInfoSearch
import com.example.georgeois.dataclass.ChatingContent
import com.example.georgeois.dataclass.LastChatting
import com.example.georgeois.repository.ChatRepository
import kotlinx.coroutines.launch
import javax.security.auth.callback.Callback


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
    //검색 채팅방 리스트
    var chatRoomListSearch = MutableLiveData<MutableList<ChatList>>()
    //검색 채팅방 정보
    var chatRoomInfoSearch = MutableLiveData<ChatRoomInfoSearch>()
    //마지막 채팅
    var lastChatting = MutableLiveData<MutableList<LastChatting>>()


    //내가 참여한 채팅방
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMyChatRoomList(userNickname: String,callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val roomIdList = mutableListOf<String>()
            val roomNameList = mutableListOf<String>()
            val tempChatRoomList = mutableListOf<ChatList>()
            try {
                val snapshot = ChatRepository.getChattingRoom(userNickname, "")

                for (document in snapshot.documents) {
                    roomNameList.add(document.getString("chatRoomName")!!)
                    roomIdList.add(document.id)
                }

                for (roomId in roomIdList) {
                    val lastChat = ChatRepository.getLastChatting(roomId)
                        val chatList = ChatList(
                            roomNameList[roomIdList.indexOf(roomId)],
                            lastChat,
                            roomId
                        )
                        tempChatRoomList.add(chatList)
                    }

                chatRoomList.value = (tempChatRoomList)
                callback(true)

            } catch (e: Exception) {
                // 오류 처리
                Log.e("Error", "Error fetching chat room list: ${e.message}")
            }

        }
    }

    var checkRefreshLastChat = false
    fun getLastChattingObserve(){
        viewModelScope.launch {
            val chatRoomLastChat = mutableListOf<LastChatting>()
            for(chat in chatRoomList.value!!){
                ChatRepository.getLastChattingObserve(chat.chatRoomId){ lastChat, id ->
                    Log.d("aaaa","VM roomId = $id")
                    Log.d("aaaa","VM lastChat = $lastChat")
                    chatRoomLastChat.add(LastChatting(lastChat,id))
                    // 모든 getLastChattingObserve 콜백이 완료되었을 때 마지막에 실행
                    if (chatRoomLastChat.size == chatRoomList.value!!.size || checkRefreshLastChat) {
                        checkRefreshLastChat = true

                        lastChatting.value = chatRoomLastChat
                        for(last in lastChatting.value!!){
                            Log.d("aaaa","lastChatting roomId = ${last.chatRoomId}")
                            Log.d("aaaa","lastChatting lastChatContent = ${last.chatLastContent}")
                        }
                        chatRoomLastChat.clear()
                        lastChatting.value?.clear()
                    }
                }
            }
        }
    }

    //검색한 채팅방
    fun getSearchChattingRoom(content : String){
        viewModelScope.launch {
            try {
                val tempChatRoomList = mutableListOf<ChatList>()
                // 비동기적으로 Firestore에서 데이터 가져오기
                val snapshot = ChatRepository.getSearchChattingRoom(content)
                for (document in snapshot) {
                    val tempChatRoomName = document.getString("chatRoomName")!!
                    val roomId = document.id
                    val chatOwner = document.getString("chatOwnerNickname")!! + "님의 오픈채팅"

                    val chatList = ChatList(tempChatRoomName, chatOwner, roomId)
                    tempChatRoomList.add(chatList)
                }
                // LiveData를 사용하여 UI에 데이터 업데이트
                chatRoomListSearch.postValue(tempChatRoomList)
            } catch (e: Exception) {
                // 오류 처리
                Log.e("Error", "Error fetching chat room list: ${e.message}")
            }
        }
    }

    //모든 채팅방
    fun getAllChattingRoom(){
        viewModelScope.launch {
            try {
                val tempChatRoomList = mutableListOf<ChatList>()
                // 비동기적으로 Firestore에서 데이터 가져오기
                val snapshot = ChatRepository.getAllChattingRoom()
                for (document in snapshot.documents) {
                    val tempChatRoomName = document.getString("chatRoomName")!!
                    val roomId = document.id
                    val chatOwner = document.getString("chatOwnerNickname")!! + "님의 오픈채팅"

                    val chatList = ChatList(tempChatRoomName, chatOwner, roomId)
                    tempChatRoomList.add(chatList)
                }
                // LiveData를 사용하여 UI에 데이터 업데이트
                chatRoomListSearch.postValue(tempChatRoomList)
            } catch (e: Exception) {
                // 오류 처리
                Log.e("Error", "Error fetching chat room list: ${e.message}")
            }
        }
    }

    //채팅방 정보&채팅 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    fun getChatRoom(currentChatRoomId:String, userNickname: String){
        viewModelScope.launch {
            ChatRepository.getRoomInfo(currentChatRoomId) { exception, document ->
                if (document != null) {
                    chatUserList.postValue(document.get("chatUserList") as ArrayList<String>)
                    chatRoomName.value = document.getString("chatRoomName")
                    chatBirth.value = document.getString("chatBirth")
                    chatBudget.value = document.getLong("chatBudget")!!.toInt()
                    chatGender.value = document.getString("chatGender")
                    chatOwnerNickname.value = document.getString("chatOwnerNickname")
                    chatRoomId.value = document.id
                    ChatRepository.getChatting(currentChatRoomId){ task, chatContentList ->
                        if (task == null) {
                            // chatContentList를 chatContent LiveData에 할당하여 UI에 업데이트
                            chatContent.value = chatContentList
                        }
                        else {

                        }
                    }
                }
            }
        }
    }


    // 채팅방 정보 가져오기
    fun getChatRoomInfo(roomId: String) {
        viewModelScope.launch {
            try {
                // 비동기적으로 Firestore에서 데이터 가져오기
                val snapshot = ChatRepository.getAllChattingRoom()

                for (document in snapshot.documents) {
                    if (document.id == roomId) {
                        val chatRoomInfo = ChatRoomInfoSearch(
                            document.getString("chatRoomName") ?: "",
                            document.getString("chatBirth") ?: "",
                            document.getLong("chatBudget")?.toInt() ?: 0,
                            (document.get("chatUserList") as? ArrayList<String>)?.size ?: 0,
                            document.getString("chatGender") ?: "",
                            document.id
                        )

                        // UI 업데이트는 여기서 수행
                        chatRoomInfoSearch.value = chatRoomInfo

                        break
                    }
                }
            } catch (e: Exception) {
                // 예외 처리
                Log.e("Error", "Error fetching chat room info: ${e.message}")
            }
        }
    }

    fun addNewMember(roomId : String, userNickname: String){
        viewModelScope.launch{
            ChatRepository.addNewMember(roomId,userNickname)
        }
    }

    fun exitMember(userNickname: String,roomId: String, isSelf : Boolean, isOwner : Boolean){
        viewModelScope.launch {
            ChatRepository.exitMember(userNickname,roomId,isSelf,isOwner)
        }
    }


    //채팅 새로고침
    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshChatting(chatRoomId: String, chatContentInfo: ChatingContent) {
        viewModelScope.launch {
            ChatRepository.addNewChatting(chatRoomId, chatContentInfo) {
                if (it == true) {
                    ChatRepository.getChatting(chatRoomId) { task, chatContentList ->
                        if (task == null) {
                            // chatContentList를 chatContent LiveData에 할당하여 UI에 업데이트
                            chatContent.value = chatContentList
                        } else {
                        }

                    }
                }
            }
        }
    }
}