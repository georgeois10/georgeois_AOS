package com.example.georgeois.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentLoginFindIdBinding
import com.example.georgeois.databinding.RowLoginFindIdBinding
import com.example.georgeois.resource.FieldState
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.viewModel.AuthViewModel
import com.example.georgeois.viewModel.UserViewModel

class LoginFindIdFragment : Fragment() {
    private lateinit var loginFindIdBinding: FragmentLoginFindIdBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        loginFindIdBinding = FragmentLoginFindIdBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        authViewModel.run {
            // 휴대폰 인증번호 전송 요청에 따른 Toast 메시지 update
            phoneNumberFieldState.observe(requireActivity()) {
                when(it) {
                    is FieldState.Success -> {
                        Toast.makeText(requireContext(), "${it.data}", Toast.LENGTH_SHORT).show()
                    }

                    is FieldState.Fail -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }

            // 휴대폰 인증 hint, Error message update
            confirmCodeFieldState.observe(requireActivity()) {
//                updateValidateMessage(joinMainBinding.textInputLayoutJoinMainVerificationNumber, it)

                val textInputLayout = loginFindIdBinding.textInputLayoutLoginFindIdVerificationNumber
                when (it) {
                    is FieldState.Success -> {
                        // 핸드폰 인증 성공
                        val helperMessage = it.data.toString()
                        textInputLayout.error = null
                        textInputLayout.isErrorEnabled = false
                        textInputLayout.helperText = helperMessage
                        // 인증 완료되면 인증번호 발송, 확인 버튼 클릭 불가능하게 변경
                        loginFindIdBinding.buttonLoginFindIdSendVerificationNumber.isClickable = false
                        loginFindIdBinding.buttonLoginFindIdCheckVerificationNumber.isClickable = false

                        // 핸드폰 번호로 등록된 유저 아이디 찾기
                        val phoneNumber = loginFindIdBinding.textInputEditTextLoginFindIdPNumber.text.toString()
                        userViewModel.findIdByPhoneNumber(phoneNumber)
                    }

                    is FieldState.Fail -> {
                        val errorMessage = it.message.toString()
                        textInputLayout.requestFocus()
                        textInputLayout.isErrorEnabled = true
                        textInputLayout.error = errorMessage
                    }

                    is FieldState.Error -> {
                        Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        userViewModel.run {
            findIdList.observe(requireActivity()) {
                when (it) {
                    is FieldState.Success -> {
                        val ids = it.data

                        if (ids == null) {
                            Toast.makeText(requireContext(), "가입된 계정이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                            return@observe
                        }

                        val idList: List<FindId> = ids.map {user ->
                            FindId(user["u_id"]!!, user["u_auth"]!!.toCharArray()[0])
                        }

                        showUserIds(idList)
                    }

                    is FieldState.Fail -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }

                    is FieldState.Error -> {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


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
                val phoneNumber = textInputEditTextLoginFindIdPNumber.text.toString()
                authViewModel.sendVerificationNumber(phoneNumber, requireActivity())
            }

            // 인증번호 확인 클릭
            buttonLoginFindIdCheckVerificationNumber.setOnClickListener {
                val code = textInputEditTextLoginFindIdVerificationNumber.text.toString()
                authViewModel.checkVerificationNumber(code)
            }

        }

        return loginFindIdBinding.root
    }


    /**
     * 인증 성공시 호출
     * 등록된 아이디들 불러와 RecyclerView에 넣어줌
     */
    private fun showUserIds(idList: List<FindId>) {
        val loginFindIdAdapter = LoginFindIdAdapter()
        loginFindIdBinding.recyclerViewLoginFindIdIdList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = loginFindIdAdapter
        }

        loginFindIdAdapter.submitList(idList)
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
                    textViewItemLoginFindIdAuthType.text = when (findId.authType) {
                        'K' -> "카카오 로그인"
                        'N' -> "네이버 로그인"
                        else -> ""
                    }
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