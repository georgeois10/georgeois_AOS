package com.example.georgeois.ui.board

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentBoardMainBinding
import com.example.georgeois.databinding.RowBoardMainBinding
import com.example.georgeois.dataclass.AccountBookClass
import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.SpaceItemDecoration
import com.example.georgeois.viewModel.BoardViewModel
import com.example.georgeois.viewModel.UserViewModel
import kotlinx.coroutines.launch
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BoardMainFragment : Fragment() {
    lateinit var fragmentBoardMainBinding: FragmentBoardMainBinding
    lateinit var mainActivity: MainActivity
    lateinit var userViewModel : UserViewModel
    lateinit var boardViewModel: BoardViewModel
    val adapter = BoardMainAdapter()
    var uIdx = 0
    var uNicknm = ""
    var cre_user = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardMainBinding = FragmentBoardMainBinding.inflate(inflater)
        var boardList : List<BoardClass> = emptyList()
        mainActivity = activity as MainActivity

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        boardViewModel = ViewModelProvider(requireActivity())[BoardViewModel::class.java]
        userViewModel.user.observe(viewLifecycleOwner) {
            uIdx = it!!.u_idx
            uNicknm = it!!.u_nickNm
            cre_user = it!!.cre_user
            lifecycleScope.launch{
                boardViewModel.fetchAllBoard()
            }
        }
        fragmentBoardMainBinding.run {
            floatingActionButtonBoardMainRegister.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.BOARD_REGISTER_FRAGMENT,true,null)
            }
            boardViewModel.allBoardList.observe(viewLifecycleOwner){
                adapter.submitList(it!!)
                boardList = it
            }
            recyclerViewBoardMainBoard.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewBoardMainBoard.adapter = adapter
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_10dp) // 여백 크기를 리소스에서 가져옴
            recyclerViewBoardMainBoard.addItemDecoration(SpaceItemDecoration(spacingInPixels))

            searchBarBoardMain.run {
                hint = "검색어를 입력해주세요."
            }
            searchViewBoardMain.run {
                hint = "검색어를 입력해주세요."
            }

            searchViewBoardMain
                .getEditText()
                .setOnEditorActionListener { v, actionId, event ->
                    searchBarBoardMain.setText(searchViewBoardMain.getText())
                    searchViewBoardMain.hide()
                    val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }

            searchViewBoardMain.addTransitionListener { searchView, previousState, newState ->
                if(newState == com.google.android.material.search.SearchView.TransitionState.SHOWING){
                    floatingActionButtonBoardMainRegister.visibility = View.GONE
                }
                // 서치뷰의 백버튼을 눌러 서치뷰가 사라지고 서치바가 보일 때
                else if(newState == com.google.android.material.search.SearchView.TransitionState.HIDING){
                    floatingActionButtonBoardMainRegister.visibility = View.VISIBLE
                }
            }


            searchViewBoardMain.getEditText().addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    updateSearchResults(s.toString(), boardList.toMutableList(), adapter)
                }
            })



        }
        return fragmentBoardMainBinding.root
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun updateSearchResults(query: String?, itemList: MutableList<BoardClass>, adapter: BoardMainAdapter) {
        val resultList = itemList.filter {
            it.b_content.contains(query.orEmpty()) || it.b_title.contains(query.orEmpty())
        }

        adapter.submitList(resultList)
        adapter.notifyDataSetChanged()
    }

    inner class BoardMainAdapter() :
        RecyclerView.Adapter<BoardMainAdapter.BoardMainViewHolder>() {
        var boardList : List<BoardClass> = emptyList()
        fun submitList(items: List<BoardClass>) {
            boardList = items
            notifyDataSetChanged()
        }

        inner class BoardMainViewHolder(val rowBoardMainBinding: RowBoardMainBinding) :
            RecyclerView.ViewHolder(rowBoardMainBinding.root) {

            init {

                rowBoardMainBinding.root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable("board", boardList[position] as Serializable)

                    mainActivity.replaceFragment(MainActivity.BOARD_DETAIL_FRAGMENT,true,bundle)
                }
            }


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardMainViewHolder {
            val rowBoardMainBinding = RowBoardMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val boardMainViewHolder = BoardMainViewHolder(rowBoardMainBinding)
            rowBoardMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return boardMainViewHolder
        }

        override fun onBindViewHolder(holder: BoardMainViewHolder, position: Int) {
            holder.rowBoardMainBinding.textViewRowBoardMainTitle.text = boardList[position].b_title
            val originalContent = boardList[position].b_content
            val sanitizedContent = originalContent.replace(Regex("\\n"), "")
            val truncatedContent = if (sanitizedContent.length > 15) {
                "${sanitizedContent.substring(0, 15)}..."
            } else {
                sanitizedContent
            }
            holder.rowBoardMainBinding.textViewRowBoardMainContext.text = truncatedContent
            holder.rowBoardMainBinding.textViewRowBoardMainHits.text = boardList[position].b_hits.toString()
            holder.rowBoardMainBinding.textViewRowBoardMainComment.text = boardList[position].b_comm_cnt.toString()
            holder.rowBoardMainBinding.textViewRowBoardMainRecommend.text = boardList[position].b_reco_cnt.toString()
            var settingDate = LocalDateTime.parse(boardList[position].b_date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            holder.rowBoardMainBinding.textViewRowBoardMainDate.text = settingDate
        }

        override fun getItemCount(): Int {
            return boardList.size
        }
    }

}
