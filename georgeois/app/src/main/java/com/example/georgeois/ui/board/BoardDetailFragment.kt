package com.example.georgeois.ui.board

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentBoardDetailBinding
import com.example.georgeois.databinding.RowBoardDetailCommentBinding
import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.repository.BoardRepository
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.viewModel.BoardViewModel
import com.example.georgeois.viewModel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BoardDetailFragment : Fragment() {
    lateinit var boardDetailBinding: FragmentBoardDetailBinding
    lateinit var mainActivity: MainActivity
    lateinit var userViewModel: UserViewModel
    lateinit var boardViewModel: BoardViewModel
    private var isClicked = false
    var uIdx = 0
    var uNickName = ""
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        boardDetailBinding = FragmentBoardDetailBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        boardViewModel = ViewModelProvider(requireActivity())[BoardViewModel::class.java]
        boardDetailBinding.run {
            userViewModel.user.observe(requireActivity()){
                uIdx = it!!.u_idx
                uNickName = it!!.u_nickNm
            }

            val board = arguments?.getSerializable("board") as? BoardClass

            materialToolbarBoardDetail.run {
                title = board!!.b_title
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_DETAIL_FRAGMENT)
                }
                Log.e("게시판-테스트","$board")
                Log.e("게시판-user","$uIdx")
                if(board.u_idx==uIdx) {
                    inflateMenu(R.menu.menu_board_update)
                    setOnMenuItemClickListener{
                        when(it.itemId){
                            R.id.edit_menu-> {

                            }
                            R.id.delete_menu-> {
                                val deleteBuilder = MaterialAlertDialogBuilder(context, R.style.DialogTheme).apply {
                                    val customTitle = setCustomTitle("정말로 삭제하시겠습니까?")
                                    setCustomTitle(customTitle)
                                    setNegativeButton("삭제"){ dialogInterface: DialogInterface, i: Int ->
                                        BoardRepository.deleteBoard(board.b_idx!!)
                                        dialogInterface.dismiss()
                                        mainActivity.removeFragment(MainActivity.BOARD_DETAIL_FRAGMENT)
                                    }
                                    setPositiveButton("취소",null)
                                }
                                deleteBuilder.show()
                            }
                        }
                        true
                    }
                    linearLayoutBoardDetailReport.visibility = View.GONE
                }
                
            }
            textViewBoardDetailTitle.text = board!!.b_title
            textViewBoardDetailContent.text = board!!.b_content
            textViewBoardDetailCommentCount.text = board!!.b_comm_cnt.toString()
            textViewBoardDetailCommentCount2.text = board!!.b_comm_cnt.toString()
            textViewBoardDetailHitsCount.text = board!!.b_hits.toString()
            textViewBoardDetailRecommendCount.text = board!!.b_reco_cnt.toString()
            textViewBoardDetailNickName.text = uNickName
            var settingDate = LocalDateTime.parse(board.b_date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            textViewBoardDetailDate.text = settingDate
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