package com.example.georgeois.ui.chat

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentChatRoomBinding
import com.example.georgeois.databinding.RowChatMainBinding
import com.example.georgeois.databinding.RowChatRoomMeBinding
import com.example.georgeois.databinding.RowChatRoomUserBinding
import com.example.georgeois.dataclass.ChatingContent
import com.example.georgeois.repository.ChatRepository
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.viewModel.ChatViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.hdodenhof.circleimageview.CircleImageView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRoomFragment : Fragment() {

    lateinit var fragmentChatRoomBinding: FragmentChatRoomBinding
    lateinit var mainActivity: MainActivity
    lateinit var chatViewModel: ChatViewModel
    var userList = mutableListOf<String>()
    var chatList = mutableListOf<ChatingContent>()
    var currnetChatRoomId = ""
    var chatRoomOwner = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatRoomBinding = FragmentChatRoomBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel.run {
            chatRoomName.observe(mainActivity){
                fragmentChatRoomBinding.toolbarChatRoom.title = it
                fragmentChatRoomBinding.textViewChatRoomTitle.text = it
            }
            chatGender.observe(mainActivity){
                fragmentChatRoomBinding.textViewChatRoomGender.text = it
            }
            chatBirth.observe(mainActivity){
                fragmentChatRoomBinding.textViewChatRoomBirth.text = it
            }
            chatBudget.observe(mainActivity){
                fragmentChatRoomBinding.textViewChatRoomBudget.text = "${formatNumberWithCommas(it)}원"
            }
            chatUserList.observe(mainActivity){
                userList = it
                fragmentChatRoomBinding.recyclerViewChatRoomUserList.adapter?.notifyDataSetChanged()
            }
            chatContent.observe(mainActivity){
                chatList = it
                fragmentChatRoomBinding.recyclerViewChatRoom.adapter?.notifyDataSetChanged()
                fragmentChatRoomBinding.recyclerViewChatRoom.scrollToPosition(chatList.size-1)
            }
            chatRoomId.observe(mainActivity){
                currnetChatRoomId = chatRoomId.value.toString()
                Log.d("aaaa","id = ${currnetChatRoomId}")
            }
            chatOwnerNickname.observe(mainActivity){
                chatRoomOwner = it
                fragmentChatRoomBinding.recyclerViewChatRoomUserList.adapter?.notifyDataSetChanged()
            }
        }

        fragmentChatRoomBinding.run {

            toolbarChatRoom.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CHAT_ROOM_FRAGMENT)
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.item_chat_room_menu -> {
                            val focusedView = activity?.currentFocus
                            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            if (focusedView != null) {
                                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                            }

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
                    val inputText = textInputEditTextChatRoomInputMessage.text.toString()

                    val currentTimeMillis = System.currentTimeMillis()
                    val date = Date(currentTimeMillis)
                    // 시간을 원하는 형식의 문자열로 변환
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd / HH:mm:ss", Locale.getDefault())
                    val currnetTime = dateFormat.format(date)

                    val userNickname = "A"

                    textInputEditTextChatRoomInputMessage.setText("")

                    val chatingContent = ChatingContent(inputText,currnetTime,userNickname)

                    chatViewModel.refreshChatting(currnetChatRoomId,chatingContent)
                }
            }

        }

        return fragmentChatRoomBinding.root
    }

    //채팅 리사이클러뷰
    inner class ChatRoomRecyclerView : RecyclerView.Adapter<ChatRoomRecyclerView.ViewHolderClass>() {
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
                rowChatRoomMeBinding.root.setOnClickListener {
                    val focusedView = activity?.currentFocus
                    // 입력 방법 관리자 (InputMethodManager)를 가져옵니다.
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    // 포커스를 가진 뷰의 windowToken을 사용하여 키보드를 숨깁니다.
                    if (focusedView != null) {
                        imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                    }
                }
            }

            override fun onClick(v: View?) {

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
            return chatList.size
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.textViewRowChatRoomMeOutNoti.visibility = View.GONE
            if (chatList[position].chatUserNickname != "A") {
                holder.textViewRowChatRoomMeMyContent.visibility = View.GONE
                holder.textViewRowChatRoomMeTime.visibility = View.GONE

                holder.imageViewRowChatRoomMeOpponentImage.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeOpponentNickname.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeOpponentContent.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeOpponentChatMoment.visibility = View.VISIBLE

                holder.textViewRowChatRoomMeOpponentContent.text = "${chatList[position].chatContent}"
                holder.textViewRowChatRoomMeOpponentChatMoment.text = "${chatList[position].chatTime.substring(0,18)}"
                holder.textViewRowChatRoomMeOpponentNickname.text = "${chatList[position].chatUserNickname}"
            }
            else{
                holder.textViewRowChatRoomMeMyContent.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeTime.visibility = View.VISIBLE

                holder.imageViewRowChatRoomMeOpponentImage.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentNickname.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentContent.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentChatMoment.visibility = View.GONE

                holder.textViewRowChatRoomMeMyContent.text = "${chatList[position].chatContent}"
                holder.textViewRowChatRoomMeTime.text = "${chatList[position].chatTime.substring(0,18)}"
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
            return userList.size
        }
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            if(userList[position] == "A"){
                holder.textViewChatRoomUserNickname.text = "${userList[position]}"
                holder.imageViewChatRoomUserImage.setImageResource(R.drawable.ic_person_24px)
            }
            else{
                if (chatRoomOwner == "A") {
                    holder.textViewChatRoomUserNickname.text = "${userList[position]}"
                    holder.imageViewChatRoomUserImage.setImageResource(R.drawable.ic_person_24px)
                    holder.buttonChatRoomUserExit.text = "추방"
                }
                else{
                    holder.textViewChatRoomUserNickname.text = "${userList[position]}"
                    holder.imageViewChatRoomUserImage.setImageResource(R.drawable.ic_person_24px)
                    holder.buttonChatRoomUserExit.visibility = View.GONE
                }
            }
        }
    }
    fun formatNumberWithCommas(number: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        return formatter.format(number)
    }

    override fun onResume() {
        super.onResume()
        currnetChatRoomId = arguments?.getString("roomId").toString()
        chatViewModel.getChatRoom(currnetChatRoomId,"A")
    }
}