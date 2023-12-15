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
            materialToolbarBoardRegister.run {
                title = "게시글 작성"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_REGISTER_FRAGMENT)
                }
                inflateMenu(R.menu.menu_save)
                setOnMenuItemClickListener{
                    when(it.itemId){
                        R.id.save_menu->save()
                    }
                    true
                }
            }

            contentTextWatcher = ContentTextWatcher(textIputEditTextBoardRegisterContent,textViewBoardRegisterTextCount)
            textInputLayoutBoardRegisterContent.editText?.addTextChangedListener(contentTextWatcher)
            fragmentBoardRegisterBinding.textInputEditTextBoardRegisterTitle.requestFocus()
            fragmentBoardRegisterBinding.textInputEditTextBoardRegisterTitle.postDelayed({
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(fragmentBoardRegisterBinding.textInputEditTextBoardRegisterTitle, InputMethodManager.SHOW_IMPLICIT)
            }, 200) // Delay for 200 milliseconds

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

            val Content = textIputEditTextBoardRegisterContent.text?.toString()
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
            // This ensures that the scrolling takes place after the layout is complete
            fragmentBoardRegisterBinding.scrollViewBoardRegister.fullScroll(View.FOCUS_DOWN)
        }
    }


}