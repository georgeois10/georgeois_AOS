package com.example.georgeois.ui.chat

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
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
    var userNickname = ""
    private var isUserBeingRemoved = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatRoomBinding = FragmentChatRoomBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel.run {
            chatRoomName.observe(viewLifecycleOwner){
                fragmentChatRoomBinding.toolbarChatRoom.title = it
                fragmentChatRoomBinding.textViewChatRoomTitle.text = it
            }
            chatGender.observe(viewLifecycleOwner){
                fragmentChatRoomBinding.textViewChatRoomGender.text = it
            }
            chatBirth.observe(viewLifecycleOwner){
                fragmentChatRoomBinding.textViewChatRoomBirth.text = it
            }
            chatBudget.observe(viewLifecycleOwner){
                fragmentChatRoomBinding.textViewChatRoomBudget.text = "${formatNumberWithCommas(it)}원"
            }
            chatUserList.observe(viewLifecycleOwner){
                userList = it
                var isHereMe = userNickname in userList

                if(isHereMe){
                    fragmentChatRoomBinding.recyclerViewChatRoomUserList.adapter?.notifyDataSetChanged()
                }
                else{
                    Toast.makeText(mainActivity, "채팅방에서 추방되었습니다.", Toast.LENGTH_SHORT).show()
                    mainActivity.removeFragment(MainActivity.CHAT_ROOM_FRAGMENT)

                }
            }
            chatContent.observe(mainActivity){
                chatList = it
                fragmentChatRoomBinding.recyclerViewChatRoom.adapter?.notifyDataSetChanged()
                fragmentChatRoomBinding.recyclerViewChatRoom.scrollToPosition(chatList.size-1)
            }
            chatRoomId.observe(mainActivity){
                currnetChatRoomId = chatRoomId.value.toString()

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
            if (chatList[position].chatUserNickname != userNickname
                && chatList[position].chatUserNickname != "Notification from the Admin") {
                holder.textViewRowChatRoomMeOutNoti.visibility = View.GONE
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
            else if(chatList[position].chatUserNickname == "Notification from the Admin"){
                holder.textViewRowChatRoomMeOutNoti.visibility = View.VISIBLE
                holder.textViewRowChatRoomMeMyContent.visibility = View.GONE
                holder.textViewRowChatRoomMeTime.visibility = View.GONE
                holder.imageViewRowChatRoomMeOpponentImage.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentNickname.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentContent.visibility = View.GONE
                holder.textViewRowChatRoomMeOpponentChatMoment.visibility = View.GONE
                holder.textViewRowChatRoomMeOutNoti.text = "${chatList[position].chatContent}"
            }
            else if(chatList[position].chatUserNickname == userNickname){
                holder.textViewRowChatRoomMeOutNoti.visibility = View.GONE
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
        @RequiresApi(Build.VERSION_CODES.O)
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
                buttonChatRoomUserExit.setOnClickListener {
                    if(userNickname == chatRoomOwner){
                        if(userList[adapterPosition] == userNickname){
                            val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                val customTitle = setCustomTitle("채팅방 나가기")
                                setCustomTitle(customTitle)
                                setMessage("현재 채팅방에서 나가시겠습니까?")
                                setNegativeButton("취소",null)
                                setPositiveButton("나가기") { dialogInterface: DialogInterface, i: Int ->
                                    chatViewModel.exitMember(userList[adapterPosition], currnetChatRoomId, true,true)
                                    chatViewModel.getMyChatRoomList(userNickname)
                                    mainActivity.removeFragment(MainActivity.CHAT_ROOM_FRAGMENT)
                                }
                            }
                            builder.show()
                        }
                        else{
                            val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                val customTitle = setCustomTitle("추방")
                                setCustomTitle(customTitle)
                                setMessage("${userList[adapterPosition]}님을 현재 채팅방에서 추방 시키겠습니까?")
                                setNegativeButton("취소",null)
                                setPositiveButton("추방") { dialogInterface: DialogInterface, i: Int ->
                                    chatViewModel.exitMember(userList[adapterPosition], currnetChatRoomId, false,false)
                                    chatViewModel.getMyChatRoomList(userNickname)
                                }
                            }
                            builder.show()
                        }
                    }
                    else{
                        val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                            val customTitle = setCustomTitle("채팅방 나가기")
                            setCustomTitle(customTitle)
                            setMessage("현재 채팅방에서 나가시겠습니까?")
                            setNegativeButton("취소",null)
                            setPositiveButton("나가기") { dialogInterface: DialogInterface, i: Int ->
                                chatViewModel.exitMember(userList[adapterPosition], currnetChatRoomId, true, false)
                                chatViewModel.getMyChatRoomList(userNickname)
                                mainActivity.removeFragment(MainActivity.CHAT_ROOM_FRAGMENT)
                            }
                        }
                        builder.show()
                    }
                }
            }

            override fun onClick(v: View?) {
            }
        }

        //viewHolder의 객체를 생성해서 반환한다
        //전체 행의 개수가 아닌 필요한 만큼만 행으로 사용할 view를 만들고 viewHolder도 생성한다
        @RequiresApi(Build.VERSION_CODES.O)
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
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            if(userList[position] == userNickname){
                holder.textViewChatRoomUserNickname.text = "${userList[position]}"
                holder.imageViewChatRoomUserImage.setImageResource(R.drawable.ic_person_24px)
                holder.buttonChatRoomUserExit.visibility = View.VISIBLE
                holder.buttonChatRoomUserExit.text = "나가기"

            }
            else{
                if (chatRoomOwner == userNickname) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        currnetChatRoomId = arguments?.getString("roomId").toString()
        userNickname = arguments?.getString("userNickname").toString()
        chatViewModel.getChatRoom(currnetChatRoomId,userNickname)
    }
    fun setCustomTitle(title: String): TextView {
        val customTitle = TextView(context).apply {
            text = title  // 타이틀 텍스트 설정
            textSize = 25f  // 텍스트 사이즈 설정
            typeface = ResourcesCompat.getFont(context, R.font.space)  // 글꼴 스타일 설정
            setTextColor(Color.BLACK)  // 텍스트 색상 설정
            setPadding(100, 100, 0, 20)  // 패딩 설정 (단위: px)
        }
        return customTitle
    }

}