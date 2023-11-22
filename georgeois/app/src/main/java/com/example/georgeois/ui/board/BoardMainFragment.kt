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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentBoardMainBinding
import com.example.georgeois.databinding.RowBoardMainBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.SpaceItemDecoration

class BoardMainFragment : Fragment() {
    lateinit var fragmentBoardMainBinding: FragmentBoardMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardMainBinding = FragmentBoardMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentBoardMainBinding.run {
            floatingActionButtonBoardMainRegister.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.BOARD_REGISTER_FRAGMENT,true,null)
            }
            var itemList = mutableListOf("Item 1", "Item 2", "Item 3","Item 3","Item 3","Item 3","Item 3","Item 3","Item 4", "item5")

            val adapter = BoardMainAdapter(itemList)
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
                    updateSearchResults(s.toString(), itemList, adapter)
                }
            })



        }
        return fragmentBoardMainBinding.root
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun updateSearchResults(query: String?, itemList: MutableList<String>, adapter: BoardMainAdapter) {
        val resultList = itemList.filter { it.contains(query.orEmpty()) }.toMutableList()
        adapter.itemList = resultList
        adapter.notifyDataSetChanged()
    }

    inner class BoardMainAdapter(var itemList: MutableList<String>) :
        RecyclerView.Adapter<BoardMainAdapter.BoardMainViewHolder>() {

        inner class BoardMainViewHolder(val rowBoardMainBinding: RowBoardMainBinding) :
            RecyclerView.ViewHolder(rowBoardMainBinding.root) {
            private val boardTitle :TextView = rowBoardMainBinding.textViewRowBoardMainTitle

            init {

                rowBoardMainBinding.root.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("boardTitle", boardTitle.text.toString())
                    if(boardTitle.text == "Item 2") bundle.putString("boardOwner","Owner")
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
            holder.rowBoardMainBinding.textViewRowBoardMainTitle.text = itemList[position]
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }

}
