package com.example.georgeois.ui.chat

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentChatRoomBinding
import com.example.georgeois.databinding.RowChatMainBinding
import com.example.georgeois.databinding.RowChatRoomMeBinding
import com.example.georgeois.databinding.RowChatRoomUserBinding
import com.example.georgeois.ui.main.MainActivity
import de.hdodenhof.circleimageview.CircleImageView

class ChatRoomFragment : Fragment() {
    lateinit var fragmentChatRoomBinding: FragmentChatRoomBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatRoomBinding = FragmentChatRoomBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentChatRoomBinding.run {
            toolbarChatRoom.run {
                title = "거지방0~1 어딘가"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CHAT_ROOM_FRAGMENT)
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_chat_room_menu -> {
                            drawerLayoutChatRoom.openDrawer(Gravity.RIGHT)
                        }

                        R.id.item_chat_room_household_ledger -> {
                            mainActivity.replaceFragment(MainActivity.CHAT_ACCOUT_BOOK_FRAGMENT,true,null)
                        }
                    }
                    true
                }
            }
            recyclerViewChatRoom.run {
                adapter = ChatRoomRecyclerView()
                val manager = LinearLayoutManager(mainActivity)
                manager.stackFromEnd = true
                layoutManager = manager

            }
            recyclerViewChatRoomUserList.run {
                adapter = ChatRoomUserRecyclerView()
                val manager = LinearLayoutManager(mainActivity)
                manager.stackFromEnd = true
                layoutManager = manager
            }
            linearLayoutChatRoomSend.setOnClickListener {
                val content = textInputEditTextChatRoomInputMessage.text.toString()
                if(content != ""){
                    toolbarChatRoom.title = content
                    textInputEditTextChatRoomInputMessage.setText("")
                }
            }
        }

        return fragmentChatRoomBinding.root
    }

    //채팅 리사이클러뷰
    inner class ChatRoomRecyclerView :
        RecyclerView.Adapter<ChatRoomRecyclerView.ViewHolderClass>() {
        inner class ViewHolderClass(rowChatRoomMeBinding: RowChatRoomMeBinding) :
            RecyclerView.ViewHolder(rowChatRoomMeBinding.root), View.OnClickListener {

            val textViewRowChatRoomMeMyContent: TextView
            val textViewRowChatRoomMeTime: TextView
            val imageViewRowChatRoomMeOpponentImage: CircleImageView
            val textViewRowChatRoomMeOpponentNickname: TextView
            val textViewRowChatRoomMeOpponentContent: TextView
            val textViewRowChatRoomMeOpponentChatMoment: TextView
            val textViewRowChatRoomMeOutNoti: TextView

            init {
                //내 채팅
                textViewRowChatRoomMeMyContent = rowChatRoomMeBinding.textViewRowChatRoomMeMyContent
                //내 채팅 시간대
                textViewRowChatRoomMeTime = rowChatRoomMeBinding.textViewRowChatRoomMeTime
                //상대방 이미지
                imageViewRowChatRoomMeOpponentImage = rowChatRoomMeBinding.imageViewRowChatRoomMeOpponentImage
                //상대방 이름
                textViewRowChatRoomMeOpponentNickname = rowChatRoomMeBinding.textViewRowChatRoomMeOpponentNickname
                //상대방 채팅
                textViewRowChatRoomMeOpponentContent = rowChatRoomMeBinding.textViewRowChatRoomMeOpponentContent
                //상대방 채팅 시간대
                textViewRowChatRoomMeOpponentChatMoment = rowChatRoomMeBinding.textViewRowChatRoomMeOpponentChatMoment
                //상대방 나갔을 때 나오는 텍스트뷰
                textViewRowChatRoomMeOutNoti = rowChatRoomMeBinding.textViewRowChatRoomMeOutNoti
            }

            override fun onClick(v: View?) {
                mainActivity.replaceFragment(MainActivity.CHAT_ROOM_FRAGMENT, true, null)
            }
        }

        //viewHolder의 객체를 생성해서 반환한다
        //전체 행의 개수가 아닌 필요한 만큼만 행으로 사용할 view를 만들고 viewHolder도 생성한다
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            //viewBining
            val rowChatRoomMeBinding = RowChatRoomMeBinding.inflate(layoutInflater)
            //viewHolder
            val viewHolderClass = ViewHolderClass(rowChatRoomMeBinding)

            // 클릭 이벤트를 설정해준다.
//            rowChatRoomMeBinding.root.setOnClickListener(viewHolderClass)

            val params = RecyclerView.LayoutParams(
                //가로길이
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            rowChatRoomMeBinding.root.layoutParams = params

            return viewHolderClass
        }

        //전체 행의 개수를 반환
        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.textViewRowChatRoomMeOutNoti.visibility = View.GONE
            if (position % 2 != 0) {
                holder.textViewRowChatRoomMeMyContent.visibility = View.GONE
                holder.textViewRowChatRoomMeTime.visibility = View.GONE

                holder.imageViewRowChatRoomMeOpponentImage.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeOpponentNickname.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeOpponentContent.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeOpponentChatMoment.visibility = View.VISIBLE
            }
            else{
                holder.textViewRowChatRoomMeMyContent.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeTime.visibility = View.VISIBLE

                holder.imageViewRowChatRoomMeOpponentImage.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentNickname.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentContent.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentChatMoment.visibility = View.GONE
            }
        }
    }

    //참여 유저 리사이클러뷰
    inner class ChatRoomUserRecyclerView : RecyclerView.Adapter<ChatRoomUserRecyclerView.ViewHolderClass>() {
        inner class ViewHolderClass(rowChatRoomUserBinding: RowChatRoomUserBinding) :
            RecyclerView.ViewHolder(rowChatRoomUserBinding.root), View.OnClickListener {

            var textViewChatRoomUserNickname : TextView
            var imageViewChatRoomUserImage : ImageView
            var buttonChatRoomUserExit : Button

            init {
                // 사용하고자 하는 View를 변수에 담아준다.
                textViewChatRoomUserNickname = rowChatRoomUserBinding.textViewChatRoomUserNickname
                imageViewChatRoomUserImage = rowChatRoomUserBinding.imageViewChatRoomUserImage
                buttonChatRoomUserExit = rowChatRoomUserBinding.buttonChatRoomUserExit
            }

            override fun onClick(v: View?) {
                mainActivity.replaceFragment(MainActivity.CHAT_ROOM_FRAGMENT,true,null)
            }
        }

        //viewHolder의 객체를 생성해서 반환한다
        //전체 행의 개수가 아닌 필요한 만큼만 행으로 사용할 view를 만들고 viewHolder도 생성한다
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            //viewBining
            val rowChatRoomUserBinding = RowChatRoomUserBinding.inflate(layoutInflater)
            //viewHolder
            val viewHolderClass = ViewHolderClass(rowChatRoomUserBinding)

            // 클릭 이벤트를 설정해준다.
            rowChatRoomUserBinding.root.setOnClickListener(viewHolderClass)

            val params = RecyclerView.LayoutParams(
                //가로길이
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            rowChatRoomUserBinding.root.layoutParams = params

            return viewHolderClass
        }

        //전체 행의 개수를 반환
        override fun getItemCount(): Int {
            return 5
        }
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            if(position == 0) {
                holder.textViewChatRoomUserNickname.text = "유저${position}"
                holder.imageViewChatRoomUserImage.setImageResource(R.drawable.ic_person_24px)
            }
            else{
                holder.textViewChatRoomUserNickname.text = "유저${position}"
                holder.imageViewChatRoomUserImage.setImageResource(R.drawable.ic_person_24px)
                holder.buttonChatRoomUserExit.text = "추방"
                //holder.buttonChatRoomUserExit.visibility = View.GONE
            }
        }
    }
}