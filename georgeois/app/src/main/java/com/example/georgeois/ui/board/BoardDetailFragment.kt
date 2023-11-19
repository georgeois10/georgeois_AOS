package com.example.georgeois.ui.board

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentBoardDetailBinding
import com.example.georgeois.databinding.RowBoardDetailCommentBinding
import com.example.georgeois.databinding.RowHomeAnalysisCategoryBinding
import com.example.georgeois.ui.main.MainActivity

class BoardDetailFragment : Fragment() {
    lateinit var boardDetailBinding: FragmentBoardDetailBinding
    lateinit var mainActivity: MainActivity
    private var isClicked = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        boardDetailBinding = FragmentBoardDetailBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        boardDetailBinding.run {
            var boardTitle = arguments?.getString("boardTitle")!!
            var owner = arguments?.getString("boardOwner")
            materialToolbarBoardDetail.run {
                title = boardTitle
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_DETAIL_FRAGMENT)
                }
                if(owner!=null) {
                    inflateMenu(R.menu.menu_board_update)
                    setOnMenuItemClickListener{
                        when(it.itemId){
                            R.id.edit_menu-> Toast.makeText(requireContext(),"수정버튼이용",Toast.LENGTH_SHORT).show()
                            R.id.delete_menu-> Toast.makeText(requireContext(),"삭제버튼이용",Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    linearLayoutBoardDetailReport.visibility = View.GONE
                }
                
            }
            textViewBoardDetailTitle.text = boardTitle
            imageButtonBoardDetailRecommend.setOnClickListener {
                isClicked = !isClicked // 클릭 상태 토글

                if (isClicked) {
                    // 클릭된 경우: 배경색 변경
                    imageButtonBoardDetailRecommend.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                    )
                } else {
                    // 클릭이 해제된 경우: 원래 배경색으로 복원
                    imageButtonBoardDetailRecommend.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.accentGray)
                    )
                }

            }

            imageButtonBoardDetailReport.setOnClickListener {
                Toast.makeText(requireContext(),"신고버튼",Toast.LENGTH_SHORT).show()
            }

            var itemList = mutableListOf(
                "내역",
                "내역2",
                "내역3",
                "내역4",
                "내역5",
                "내역6",
                "내역7",
                "내역8",
                "내역9",
                "내역10",
                "내역11",
                "내역12",
                "내역123",
                "내역14",
                "내역15",

                )

            val adapter = BoardDetailAdapter(itemList)
            recyclerviewBoardDetailComment.layoutManager = LinearLayoutManager(requireContext())
            recyclerviewBoardDetailComment.adapter = adapter
        }
        return boardDetailBinding.root
    }
    inner class BoardDetailAdapter(var itemList: MutableList<String>) :
        RecyclerView.Adapter<BoardDetailAdapter.BoardDetailViewHolder>() {

        inner class BoardDetailViewHolder(val rowBoardDetailCommentBinding: RowBoardDetailCommentBinding) :
            RecyclerView.ViewHolder(rowBoardDetailCommentBinding.root) {


            init {
                rowBoardDetailCommentBinding.root.setOnClickListener {

                }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardDetailViewHolder {
            val rowBoardDetailCommentBinding = RowBoardDetailCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val homeAnalysisCategoryViewHolder = BoardDetailViewHolder(rowBoardDetailCommentBinding)
            rowBoardDetailCommentBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return homeAnalysisCategoryViewHolder
        }

        override fun onBindViewHolder(holder: BoardDetailViewHolder, position: Int) {
            holder.rowBoardDetailCommentBinding.textViewBoardDetailCommentContent.text = itemList[position]
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }


}