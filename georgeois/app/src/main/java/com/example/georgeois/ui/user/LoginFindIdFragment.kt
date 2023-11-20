package com.example.georgeois.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentLoginFindIdBinding
import com.example.georgeois.databinding.RowLoginFindIdBinding
import com.example.georgeois.ui.main.MainActivity

class LoginFindIdFragment : Fragment() {
    private lateinit var loginFindIdBinding: FragmentLoginFindIdBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        loginFindIdBinding = FragmentLoginFindIdBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        loginFindIdBinding.run {

            // 툴바
            materialToolbarLoginFindId.run {
                title = "아이디 찾기"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.LOGIN_FIND_ID_FRAGMENT)
                }
            }

            // 전화번호로 인증번호 발송 클릭
            buttonLoginFindIdSendVerificationNumber.setOnClickListener {
                // TODO : 인증번호 발송 기능 구현
            }

            // 인증번호 확인 클릭
            buttonLoginFindIdCheckVerificationNumber.setOnClickListener {
                // TODO : 인증번호 확인 기능 구현
                val loginFindIdAdapter = LoginFindIdAdapter()

                // 인증 성공 시
                recyclerViewLoginFindIdIdList.apply {
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = loginFindIdAdapter
                }

                // recyclerView 에 전달될 테스트 리스트
                val testList = listOf(
                    FindId("test1", '1'),
                    FindId("test2", '2'),
                    FindId("test3", '3'),
                    FindId("test4", '4'),
                    FindId("test5", '1'),
                    FindId("test6", '2'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3'),
                    FindId("test7", '3')

                )
                loginFindIdAdapter.submitList(testList)
            }

        }

        return loginFindIdBinding.root
    }

    data class FindId (
        val id: String,
        val authType: Char
    )

    inner class LoginFindIdAdapter : ListAdapter<FindId, LoginFindIdAdapter.LoginFindIdViewHolder>(differ) {

        inner class LoginFindIdViewHolder(private val rowLoginFindIdBinding: RowLoginFindIdBinding) : RecyclerView.ViewHolder(rowLoginFindIdBinding.root) {
            fun bind(findId : FindId) {
                rowLoginFindIdBinding.run {
                    textViewItemLoginFindIdId.text = findId.id
                    textViewItemLoginFindIdAuthType.text = findId.authType.toString()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginFindIdViewHolder {
            return LoginFindIdViewHolder(
                RowLoginFindIdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: LoginFindIdViewHolder, position: Int) {
            holder.bind(currentList[position])
        }

        override fun getItemCount(): Int {
            return currentList.size
        }
    }

    private companion object {
        val differ = object : DiffUtil.ItemCallback<FindId>() {
            override fun areItemsTheSame(oldItem: FindId, newItem: FindId): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FindId, newItem: FindId): Boolean {
                return oldItem == newItem
            }
        }
    }
}