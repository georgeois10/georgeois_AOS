package com.example.georgeois.ui.myInfo

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentInCategoryBinding
import com.example.georgeois.databinding.LayoutAddCategoryBinding
import com.example.georgeois.databinding.LayoutEditBudgetBinding
import com.example.georgeois.databinding.RowCategoryBinding
import com.example.georgeois.databinding.RowChatMainBinding
import com.example.georgeois.ui.chat.SpaceItemDecoration
import com.example.georgeois.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.math.BigInteger
import kotlin.concurrent.thread

class InCategoryFragment : Fragment() {
    lateinit var fragmentInCategoryBinding: FragmentInCategoryBinding
    lateinit var mainActivity: MainActivity
    var categoryList = arrayOf("월급","예금","적금","부동산","가상화폐","주식","펀드","현금")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentInCategoryBinding = FragmentInCategoryBinding.inflate(layoutInflater)

        fragmentInCategoryBinding.run {
            toolbarInCategory.run {
                title = "수입 카테고리 설정"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.IN_CATEGORY_FRAGMENT)
                }
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_add_category -> {
                            val dialogBinding = LayoutAddCategoryBinding.inflate(layoutInflater)

                            val builder =
                                MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                                    val customTitle = setCustomTitle("카테고리 추가")
                                    setCustomTitle(customTitle)
                                }

                            builder.setView(dialogBinding.root)

                            var inputBudget = dialogBinding.editTextInputCategory

                            inputBudget.requestFocus()
                            inputBudget.typeface = ResourcesCompat.getFont(mainActivity, R.font.space)

                            thread {
                                inputBudget.post {
                                    val length = inputBudget.text.length
                                    inputBudget.setSelection(length)
                                    val imm =
                                        mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.showSoftInput(inputBudget, InputMethodManager.SHOW_IMPLICIT)
                                }
                            }
                            builder.setNegativeButton("취소", null)
                            builder.setPositiveButton("추가") { dialogInterface: DialogInterface, i: Int ->
                                val inputText = inputBudget.text.toString()
                                if(inputText.isEmpty()){
                                    Toast.makeText(context, "추가할 카테고리 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                }else {
                                    val category = inputText
                                    toolbarInCategory.title = category
                                }
                            }

                            builder.show()
                        }
                    }
                    true
                }
            }
            recyclerViewInCategory.run {
                adapter = InCategoryRecyclerView()
                layoutManager = LinearLayoutManager(mainActivity)

                //row간 여백 설정
                val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_10dp) // 여백 크기를 리소스에서 가져옴
                addItemDecoration(SpaceItemDecoration(spacingInPixels))
            }
        }

        return fragmentInCategoryBinding.root
    }
    inner class InCategoryRecyclerView : RecyclerView.Adapter<InCategoryRecyclerView.ViewHolderClass>() {
        inner class ViewHolderClass(rowCategoryBinding: RowCategoryBinding) :
            RecyclerView.ViewHolder(rowCategoryBinding.root), View.OnClickListener {

            var textViewRowCategoryName: TextView
            var buttonRowCategoryDeleteButton: ImageButton

            init {
                // 사용하고자 하는 View를 변수에 담아준다.
                textViewRowCategoryName = rowCategoryBinding.textViewRowCategoryName
                buttonRowCategoryDeleteButton = rowCategoryBinding.buttonRowCategoryDeleteButton
            }

            override fun onClick(v: View?) {

            }
        }

        //viewHolder의 객체를 생성해서 반환한다
        //전체 행의 개수가 아닌 필요한 만큼만 행으로 사용할 view를 만들고 viewHolder도 생성한다
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            //viewBining
            val rowCategoryBinding = RowCategoryBinding.inflate(layoutInflater)
            //viewHolder
            val viewHolderClass = ViewHolderClass(rowCategoryBinding)

            // 클릭 이벤트를 설정해준다.
            rowCategoryBinding.root.setOnClickListener(viewHolderClass)

            val params = RecyclerView.LayoutParams(
                //가로길이
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            rowCategoryBinding.root.layoutParams = params

            return viewHolderClass
        }

        //전체 행의 개수를 반환
        override fun getItemCount(): Int {
            return categoryList.size
        }
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.textViewRowCategoryName.text = "${categoryList[position]}"
            holder.buttonRowCategoryDeleteButton.setOnClickListener {
                fragmentInCategoryBinding.toolbarInCategory.title = "${categoryList[position]} 삭제"
            }
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