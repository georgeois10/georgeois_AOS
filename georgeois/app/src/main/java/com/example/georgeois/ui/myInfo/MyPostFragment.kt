package com.example.georgeois.ui.myInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentMyPostBinding
import com.example.georgeois.databinding.RowBoardMainBinding
import com.example.georgeois.databinding.RowChatMainBinding
import com.example.georgeois.ui.chat.SpaceItemDecoration
import com.example.georgeois.ui.main.MainActivity

class MyPostFragment : Fragment() {
    lateinit var fragmentMyPostBinding: FragmentMyPostBinding
    lateinit var mainActivity : MainActivity
    var itemList = mutableListOf("Item 1", "Item 2", "Item 3")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentMyPostBinding = FragmentMyPostBinding.inflate(layoutInflater)

        fragmentMyPostBinding.run {
            toolbarMyPost.run {
                title = "내가 쓴 글"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_POST_FRAGMENT)
                }
            }
            recyclerViewMyPost.run {
                adapter = MyPostAdapter(itemList)
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
        return fragmentMyPostBinding.root
    }
    inner class MyPostAdapter(var itemList: MutableList<String>) :
        RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder>() {

        inner class MyPostViewHolder(val rowBoardMainBinding: RowBoardMainBinding) :
            RecyclerView.ViewHolder(rowBoardMainBinding.root) {
            val boardTitle :TextView
            init {

                boardTitle = rowBoardMainBinding.textViewRowBoardMainTitle
                rowBoardMainBinding.root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("boardTitle", boardTitle.text.toString())
                    if(boardTitle.text == "Item 2") bundle.putString("boardOwner","Owner")
                    mainActivity.replaceFragment(MainActivity.BOARD_DETAIL_FRAGMENT,true,bundle)
                }
                rowBoardMainBinding.root.setOnLongClickListener {
                    val popup = PopupMenu(itemView.context, it)
                    popup.inflate(R.menu.menu_edit_post) // 여기에 메뉴 리소스 파일 지정
                    popup.setOnMenuItemClickListener { menuItem ->
                        when(menuItem.itemId){
                            R.id.menu_edit_post_modify ->{
                                fragmentMyPostBinding.toolbarMyPost.title = "수정"
                            }
                            R.id.menu_edit_post_delete ->{
                                fragmentMyPostBinding.toolbarMyPost.title = "삭제"
                            }
                        }
                        true
                    }
                    popup.show()
                    true
                }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
            val rowBoardMainBinding = RowBoardMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val boardMainViewHolder = MyPostViewHolder(rowBoardMainBinding)
            rowBoardMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return boardMainViewHolder
        }

        override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
            holder.rowBoardMainBinding.textViewRowBoardMainTitle.text = itemList[position]
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }
}