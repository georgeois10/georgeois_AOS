package com.example.georgeois.ui.chat

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentChatMainBinding
import com.example.georgeois.databinding.FragmentMainBinding
import com.example.georgeois.databinding.RowChatMainBinding
import com.example.georgeois.dataclass.ChatList
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.ui.main.MainFragment
import com.example.georgeois.viewModel.ChatViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class ChatMainFragment : Fragment() {
    var size = 10
    lateinit var callback: OnBackPressedCallback
    lateinit var fragmentChatMainBinding: FragmentChatMainBinding
    lateinit var mainActivity: MainActivity
    lateinit var chatViewModel: ChatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel.getMyChatRoomList("A")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // onViewCreated에서 데이터 로딩 및 UI 갱신 수행
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        chatViewModel.run {
            chatRoomList.observe(viewLifecycleOwner) { chatList ->
                // 데이터가 변경될 때마다 submitList를 호출하여 리사이클러뷰 갱신
                (fragmentChatMainBinding.recyclerViewChatMain.adapter as? ChatMainRecyclerView)?.submitList(chatList)
                // 데이터 로딩이 완료되면 로딩 화면을 숨김
                fragmentChatMainBinding.progressBarChatMain.visibility = View.GONE
            }

            // 데이터 로딩 시작 시 로딩 화면을 보임
            fragmentChatMainBinding.progressBarChatMain.visibility = View.VISIBLE

            // 데이터를 비동기적으로 가져오도록 수정
            viewModelScope.launch {
                getMyChatRoomList("A")
            }
        }

        // 기타 초기화 코드 추가 가능
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatMainBinding = FragmentChatMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

//        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
//        chatViewModel.run {
//            chatRoomList.observe(mainActivity){
//                chatList = it
//                fragmentChatMainBinding.recyclerViewChatMain.adapter?.notifyDataSetChanged()
//            }
//            getMyChatRoomList("A")
//        }

        fragmentChatMainBinding.run {
            floatingButtonChatMain.run {
                // 플로팅 액션 버튼 아이콘의 색상을 변경
                val fab: FloatingActionButton = findViewById(R.id.floatingButton_chat_main)
                fab.setColorFilter(ContextCompat.getColor(mainActivity, R.color.white), PorterDuff.Mode.SRC_IN)

                setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.ADD_CHAT_FRAGMENT,true,null)
                }
            }
            searchBarChatMain.run{
                hint = "검색어를 입력해주세요."
            }

            searchViewChatMain.run{
                hint = "검색어를 입력해주세요."

                addTransitionListener { searchView, previousState, newState ->
                    // 서치바를 눌러 서치뷰가 보일 때
                    if(newState == com.google.android.material.search.SearchView.TransitionState.SHOWING){
                        nestedScrollViewChatMain.isNestedScrollingEnabled = false
                        floatingButtonChatMain.visibility = View.GONE
                        editText.post {
                            editText.requestFocus()
                            val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(editText, 0)
                        }
                    }
                    // 서치뷰의 백버튼을 눌러 서치뷰가 사라지고 서치바가 보일 때
                    else if(newState == com.google.android.material.search.SearchView.TransitionState.HIDING){
                        nestedScrollViewChatMain.isNestedScrollingEnabled = true
                        floatingButtonChatMain.visibility = View.VISIBLE
                    }
                }

                editText.setOnEditorActionListener { textView, i, keyEvent ->
                    size = 5
                    recyclerViewChatMainSearchList.adapter?.notifyDataSetChanged()
                    // 키보드 숨기기
                    val imm = textView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(textView.windowToken, 0)
                    true
                }
            }

            recyclerViewChatMain.run {
                adapter = ChatMainRecyclerView()
                layoutManager = LinearLayoutManager(mainActivity)

                //row간 여백 설정
                val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_10dp) // 여백 크기를 리소스에서 가져옴
                addItemDecoration(SpaceItemDecoration(spacingInPixels))
            }

            recyclerViewChatMainSearchList.run {
                adapter = ChatMainSearchRecyclerView()
                layoutManager = LinearLayoutManager(mainActivity)

                //row간 여백 설정
                val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_10dp) // 여백 크기를 리소스에서 가져옴
                addItemDecoration(SpaceItemDecoration(spacingInPixels))
            }
        }

        return fragmentChatMainBinding.root
    }
    inner class ChatMainRecyclerView : RecyclerView.Adapter<ChatMainRecyclerView.ViewHolderClass>() {

        // diffCallback 정의
        private val diffCallback = object : DiffUtil.ItemCallback<ChatList>() {
            override fun areItemsTheSame(oldItem: ChatList, newItem: ChatList): Boolean {
                return oldItem.chatRoomId == newItem.chatRoomId
            }

            override fun areContentsTheSame(oldItem: ChatList, newItem: ChatList): Boolean {
                return oldItem == newItem
            }
        }

        // AsyncListDiffer 초기화
        private val differ = AsyncListDiffer(this, diffCallback)

        inner class ViewHolderClass(rowChatMainBinding: RowChatMainBinding) :
            RecyclerView.ViewHolder(rowChatMainBinding.root), View.OnClickListener {

            var rowChatMainTitle: TextView
            var rowChatMainLastChat: TextView

            init {
                rowChatMainTitle = rowChatMainBinding.rowChatMainTitle
                rowChatMainLastChat = rowChatMainBinding.rowChatMainLastChat
                rowChatMainBinding.root.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val newBundle = Bundle()
                newBundle.putString("roomId", differ.currentList[adapterPosition].chatRoomId)
                mainActivity.replaceFragment(MainActivity.CHAT_ROOM_FRAGMENT, true, newBundle)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            val rowChatMainBinding = RowChatMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolderClass(rowChatMainBinding)
        }

        override fun getItemCount(): Int {
            return differ.currentList.size
        }

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            val chatList = differ.currentList[position]
            holder.rowChatMainTitle.text = chatList.chatRoomName
            holder.rowChatMainLastChat.text = chatList.chatLastChatting
        }

        // 리스트 갱신을 위한 submitList 함수
        fun submitList(newList: List<ChatList>) {
            differ.submitList(newList)
        }
    }

    inner class ChatMainSearchRecyclerView : RecyclerView.Adapter<ChatMainSearchRecyclerView.ViewHolderClass>() {
        inner class ViewHolderClass(rowChatMainBinding: RowChatMainBinding) :
            RecyclerView.ViewHolder(rowChatMainBinding.root), View.OnClickListener {

            var rowChatMainTitle: TextView
            var rowChatMainLastChat: TextView

            init {
                // 사용하고자 하는 View를 변수에 담아준다.
                rowChatMainTitle = rowChatMainBinding.rowChatMainTitle
                rowChatMainLastChat = rowChatMainBinding.rowChatMainLastChat
            }

            override fun onClick(v: View?) {
                mainActivity.replaceFragment(MainActivity.CHAT_ROOM_FRAGMENT,true,null)
            }
        }

        //viewHolder의 객체를 생성해서 반환한다
        //전체 행의 개수가 아닌 필요한 만큼만 행으로 사용할 view를 만들고 viewHolder도 생성한다
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            //viewBining
            val rowChatMainBinding = RowChatMainBinding.inflate(layoutInflater)
            //viewHolder
            val viewHolderClass = ViewHolderClass(rowChatMainBinding)

            // 클릭 이벤트를 설정해준다.
            rowChatMainBinding.root.setOnClickListener(viewHolderClass)

            val params = RecyclerView.LayoutParams(
                //가로길이
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            rowChatMainBinding.root.layoutParams = params

            return viewHolderClass
        }

        //전체 행의 개수를 반환
        override fun getItemCount(): Int {
            return size
        }
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.rowChatMainTitle.text = "거지방$position"
            holder.rowChatMainLastChat.text = "채팅$position"
        }
    }

    override fun onResume() {
        super.onResume()
//        chatViewModel.getMyChatRoomList("A")
    }
}

//리사이클러뷰 row 사이 여백 추가
class SpaceItemDecoration(private val bottomSpace: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        // 마지막 아이템이 아닐 경우에만 바텀 여백을 추가
        if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
            outRect.bottom = bottomSpace
        }
    }
}
