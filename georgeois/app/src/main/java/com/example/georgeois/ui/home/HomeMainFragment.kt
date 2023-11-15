package com.example.georgeois.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentHomeMainBinding
import com.example.georgeois.ui.main.MainActivity


class HomeMainFragment : Fragment() {
    lateinit var fragmentHomeMainBinding: FragmentHomeMainBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeMainBinding = FragmentHomeMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        fragmentHomeMainBinding.run {
            materialToolbarHomeMain.run {
                title = "거르주아"
            }

            // 분석
            buttonHomeMainAnalysis.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.HOME_ANALYSIS_FRAGMENT,true,null)
            }

            // 등록
            floatingActionButtonHomeMainRegister.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.HOME_REGISTER_FRAGMENT,true,null)
            }


        }
        return fragmentHomeMainBinding.root
    }


}