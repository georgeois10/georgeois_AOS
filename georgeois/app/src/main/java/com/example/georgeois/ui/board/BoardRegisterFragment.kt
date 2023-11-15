package com.example.georgeois.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentBoardRegisterBinding
import com.example.georgeois.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar


class BoardRegisterFragment : Fragment() {
    lateinit var fragmentBoardRegisterBinding: FragmentBoardRegisterBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardRegisterBinding = FragmentBoardRegisterBinding.inflate(inflater)
        mainActivity = activity as MainActivity
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
            imageButtonBoardRegisterAdd.setOnClickListener {
                constraintLayoutBoardRegister.visibility = View.VISIBLE
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
            mainActivity.removeFragment(MainActivity.BOARD_REGISTER_FRAGMENT)
            Snackbar.make(fragmentBoardRegisterBinding.root, "저장되었습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }


}