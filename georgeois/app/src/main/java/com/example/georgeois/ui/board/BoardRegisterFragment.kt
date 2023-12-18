package com.example.georgeois.ui.board

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentBoardRegisterBinding
import com.example.georgeois.dataclass.BoardClass
import com.example.georgeois.dataclass.InAccountBookClass
import com.example.georgeois.repository.BoardRepository
import com.example.georgeois.repository.InAccountBookRepository
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.ContentTextWatcher
import com.example.georgeois.utill.MoneyTextWatcher
import com.example.georgeois.viewModel.BoardViewModel
import com.example.georgeois.viewModel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.properties.Delegates


class BoardRegisterFragment : Fragment() {
    lateinit var fragmentBoardRegisterBinding: FragmentBoardRegisterBinding
    lateinit var mainActivity: MainActivity
    lateinit var userViewModel : UserViewModel
    lateinit var boardViewModel: BoardViewModel
    lateinit var contentTextWatcher: ContentTextWatcher
    var uIdx = 0
    var uNicknm = ""
    var cre_user = ""
    var boardIdx : Int? = null
    lateinit var result : BoardClass
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardRegisterBinding = FragmentBoardRegisterBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        boardViewModel = ViewModelProvider(requireActivity())[BoardViewModel::class.java]
        userViewModel.user.observe(viewLifecycleOwner) {
            uIdx = it!!.u_idx
            uNicknm = it!!.u_nickNm
            cre_user = it!!.cre_user
        }

        fragmentBoardRegisterBinding.run {
            boardIdx = arguments?.getInt("boardIdx")

            if (boardIdx != null) {
                lifecycleScope.launch {
                    boardViewModel.fetchAllBoard()
                }
                boardViewModel.allBoardList.observe(viewLifecycleOwner) {
                    result = it.find { it.b_idx == boardIdx }!!
                    textInputEditTextBoardRegisterTitle.setText("${result!!.b_title}")
                    editTextBoardRegisterContext.setText("${result!!.b_content}")
                    materialToolbarBoardRegister.title = "게시글 수정"
                }
            } else{
                materialToolbarBoardRegister.title = "게시글 작성"
            }
            materialToolbarBoardRegister.run {
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_REGISTER_FRAGMENT)
                }
                inflateMenu(R.menu.menu_save)
                setOnMenuItemClickListener{
                    when(it.itemId){
                        R.id.save_menu->{
                            if(boardIdx==null){
                                save()
                            }else{
                                update(result)
                            }

                        }
                    }
                    true
                }
            }

            contentTextWatcher = ContentTextWatcher(textViewBoardRegisterTextCount)
            editTextBoardRegisterContext.addTextChangedListener(contentTextWatcher)
            fragmentBoardRegisterBinding.textInputEditTextBoardRegisterTitle.requestFocus()
            fragmentBoardRegisterBinding.textInputEditTextBoardRegisterTitle.postDelayed({
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(fragmentBoardRegisterBinding.textInputEditTextBoardRegisterTitle, InputMethodManager.SHOW_IMPLICIT)
            }, 200)
            imageButtonBoardRegisterAdd.setOnClickListener {
                constraintLayoutBoardRegister.visibility = View.VISIBLE
                scrollToBottom()
            }
            imageButtonBoardRegisterDelete.setOnClickListener {
                constraintLayoutBoardRegister.visibility = View.GONE
            }
        }

        return fragmentBoardRegisterBinding.root
    }
    // 입력된 거 저장
    fun save(){
        fragmentBoardRegisterBinding.run {

            val Content = editTextBoardRegisterContext.text?.toString()
            val Title = textInputEditTextBoardRegisterTitle.text?.toString()
            if(!Content.isNullOrBlank() && !Title.isNullOrBlank()){
                var boardClass = BoardClass(
                    null,
                    uIdx,
                    Title,
                    uNicknm,
                    Content,
                    "none",
                    0,
                    0,
                    0,
                    0,
                    LocalDate.now().toString(),
                    cre_user
                )
                BoardRepository.insertBoard(boardClass)
                mainActivity.removeFragment(MainActivity.BOARD_REGISTER_FRAGMENT)
            }
            Snackbar.make(fragmentBoardRegisterBinding.root, "저장되었습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun scrollToBottom() {
        fragmentBoardRegisterBinding.scrollViewBoardRegister.post {
            fragmentBoardRegisterBinding.scrollViewBoardRegister.fullScroll(View.FOCUS_DOWN)
        }
    }

    fun update(boardClass: BoardClass) {
        fragmentBoardRegisterBinding.run {
            val content = editTextBoardRegisterContext.text?.toString()
            val title = textInputEditTextBoardRegisterTitle.text?.toString()

            if (!content.isNullOrBlank() && !title.isNullOrBlank()) {
                val updateBoard = BoardClass(
                    boardClass.b_idx,
                    uIdx,
                    title,
                    uNicknm,
                    content,
                    "none",
                    boardClass.b_hits,
                    boardClass.b_reco_cnt,
                    boardClass.b_comm_cnt,
                    boardClass.b_noti_cnt,
                    boardClass.b_date,
                    cre_user
                )

                BoardRepository.updateBoard(updateBoard)
                mainActivity.removeFragment(MainActivity.BOARD_REGISTER_FRAGMENT)
            }

            Snackbar.make(fragmentBoardRegisterBinding.root, "수정되었습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }



}