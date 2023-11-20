package com.example.georgeois.ui.myInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.RowChatMainBinding
import com.example.georgeois.ui.chat.SpaceItemDecoration
import com.example.georgeois.ui.main.MainActivity

class MyPostFragment : Fragment() {
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_my_post, container, false)
//
//        recyclerViewChatMain.run {
//            adapter = ChatMainRecyclerView()
//            layoutManager = LinearLayoutManager(mainActivity)
//
//            //row간 여백 설정
//            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_10dp) // 여백 크기를 리소스에서 가져옴
//            addItemDecoration(SpaceItemDecoration(spacingInPixels))
//        }
//    }
//    inner class ChatMainRecyclerView : RecyclerView.Adapter<ChatMainRecyclerView.ViewHolderClass>() {
//        inner class ViewHolderClass(rowChatMainBinding: RowChatMainBinding) :
//            RecyclerView.ViewHolder(rowChatMainBinding.root), View.OnClickListener {
//
//            var rowChatMainTitle: TextView
//            var rowChatMainLastChat: TextView
//
//            init {
//                // 사용하고자 하는 View를 변수에 담아준다.
//                rowChatMainTitle = rowChatMainBinding.rowChatMainTitle
//                rowChatMainLastChat = rowChatMainBinding.rowChatMainLastChat
//            }
//
//            override fun onClick(v: View?) {
//                mainActivity.replaceFragment(MainActivity.CHAT_ROOM_FRAGMENT,true,null)
//            }
//        }
//
//        //viewHolder의 객체를 생성해서 반환한다
//        //전체 행의 개수가 아닌 필요한 만큼만 행으로 사용할 view를 만들고 viewHolder도 생성한다
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
//            //viewBining
//            val rowChatMainBinding = RowChatMainBinding.inflate(layoutInflater)
//            //viewHolder
//            val viewHolderClass = ViewHolderClass(rowChatMainBinding)
//
//            // 클릭 이벤트를 설정해준다.
//            rowChatMainBinding.root.setOnClickListener(viewHolderClass)
//
//            val params = RecyclerView.LayoutParams(
//                //가로길이
//                RecyclerView.LayoutParams.MATCH_PARENT,
//                RecyclerView.LayoutParams.WRAP_CONTENT
//            )
//            rowChatMainBinding.root.layoutParams = params
//
//            return viewHolderClass
//        }
//
//        //전체 행의 개수를 반환
//        override fun getItemCount(): Int {
//            return 10
//        }
//        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
//            holder.rowChatMainTitle.text = "거지방$position"
//            holder.rowChatMainLastChat.text = "채팅$position"
//        }
//    }
}