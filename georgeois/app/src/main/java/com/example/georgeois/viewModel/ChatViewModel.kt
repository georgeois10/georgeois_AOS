package com.example.georgeois.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.georgeois.dataclass.ChatingContent
import com.example.georgeois.repository.ChatRepository

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

    fun getChatRoom(){
        ChatRepository.getAllChattingRoom {
            for(document in it.result.documents){
                chatUserList.value = document.get("chatUserList") as ArrayList<String>
                chatRoomName.value = document.getString("chatRoomName")
                chatBirth.value = document.getString("chatBirth")
                chatBudget.value = document.getLong("chatBudget")!!.toInt()
                chatGender.value = document.getString("chatGender")
                chatOwnerNickname.value = document.getString("chatOwnerNickname")
            }
        }
        ChatRepository.getChatting { task, chatContentList ->
            if (task.isSuccessful) {
                // chatContentList를 chatContent LiveData에 할당하여 UI에 업데이트
                chatContent.value = chatContentList
                Log.d("aaaa","${chatContent.value}")
            } else {
                // 실패한 경우 오류 처리
            }
        }
    }

}