package com.example.georgeois.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentBoardDetailBinding
import com.example.georgeois.ui.main.MainActivity

class BoardDetailFragment : Fragment() {
    lateinit var boardDetailBinding: FragmentBoardDetailBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        boardDetailBinding = FragmentBoardDetailBinding.inflate(inflater)
        boardDetailBinding.run {
            var boardTitle = arguments?.getString("boardTitle")!!
            materialToolbarBoardDetail.run {
                title = boardTitle
            }
            boardDetailBinding.textViewBoardDetailTitle.text = boardTitle
        }
        return boardDetailBinding.root
    }


}