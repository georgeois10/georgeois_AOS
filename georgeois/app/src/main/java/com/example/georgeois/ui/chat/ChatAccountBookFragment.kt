package com.example.georgeois.ui.chat

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.georgeois.R
import com.example.georgeois.databinding.FragmentChatAccountBookBinding
import com.example.georgeois.databinding.RowChatAccountBookUserListBinding
import com.example.georgeois.databinding.RowChatMainBinding
import com.example.georgeois.databinding.RowChatRoomUserBinding
import com.example.georgeois.databinding.RowUserConsumptionBinding
import com.example.georgeois.ui.main.MainActivity
import com.example.georgeois.utill.SpaceItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale

data class userConsumptionClass(val date : String, val category : String, val content : String, val out : String)

class ChatAccountBookFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var fragmentChatAccountBookFragment: FragmentChatAccountBookBinding
    var userConsumptionList = mutableListOf<userConsumptionClass>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatAccountBookFragment = FragmentChatAccountBookBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentChatAccountBookFragment.run {
            toolbarChatAccountBook.run {
                title = "거지방0~1 어딘가"
                setNavigationIcon(R.drawable.ic_arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.CHAT_ACCOUT_BOOK_FRAGMENT)
                }
            }

            recyclerViewChatAccountBook.run {
                adapter = ChatAccountBookRecyclerView()
                layoutManager = LinearLayoutManager(mainActivity)
                //row간 여백 설정
                val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_10dp) // 여백 크기를 리소스에서 가져옴
                addItemDecoration(SpaceItemDecoration(spacingInPixels))
            }
            textViewChatAccountBookTotalConsumption.setText("500,000 원 / 1,500,000 원")
            textViewChatAccountBookAverageConsumption.setText("100,000 원 / 300,000 원")
        }


        return fragmentChatAccountBookFragment.root
    }
    inner class ChatAccountBookRecyclerView : RecyclerView.Adapter<ChatAccountBookRecyclerView.ViewHolderClass>() {
        inner class ViewHolderClass(rowChatAccountBookUserListBinding: RowChatAccountBookUserListBinding) :
            RecyclerView.ViewHolder(rowChatAccountBookUserListBinding.root), View.OnClickListener {

            var imageViewRowChatAccountBookUserListUserImage : ImageView
            var textViewRowChatAccountBookUserListUserNickName: TextView
            var textViewRowChatAccountBookUserListUserConsumption: TextView

            init {
                // 사용하고자 하는 View를 변수에 담아준다.
                imageViewRowChatAccountBookUserListUserImage = rowChatAccountBookUserListBinding.imageViewRowChatAccountBookUserListUserImage
                textViewRowChatAccountBookUserListUserNickName = rowChatAccountBookUserListBinding.textViewRowChatAccountBookUserListUserNickName
                textViewRowChatAccountBookUserListUserConsumption = rowChatAccountBookUserListBinding.textViewRowChatAccountBookUserListUserConsumption
            }

            override fun onClick(v: View?) {
                val money = formatNumberWithCommas(BigInteger("${adapterPosition*50000}"))+" 원"

                //커스텀 다이얼로그
                val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_account_book_user_consumption, null,false)
                val totalTextView = dialogView.findViewById<TextView>(R.id.textViewaccount_book_user_consumption_total)
                totalTextView.setText("총 금액: ${money}")
                val recyclerViewAccountBookUserConsumption = dialogView.findViewById<RecyclerView>(R.id.recyclerView_account_book_user_consumption).apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = ChatAccountBookUserConsumptionRecyclerView()
                    val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_10dp)
                    addItemDecoration(SpaceItemDecorationAll(spacingInPixels))
                }

                val builder = MaterialAlertDialogBuilder(mainActivity, R.style.DialogTheme).apply {
                    val customTitle = setCustomTitle("유저${adapterPosition}님의 지출 내역")
                    setCustomTitle(customTitle)
                    setView(dialogView)
//                    setMessage("총 금액 : ${money}")
                    setPositiveButton("닫기",null)
                }
                // 다이얼로그 생성 및 표시
                val dialog = builder.create()
                dialog.show()

//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).visibility = View.VISIBLE

                // 화면의 높이를 계산하고 다이얼로그의 최대 높이를 설정합니다.
                val displayMetrics = Resources.getSystem().displayMetrics
                val maxHeight = (displayMetrics.heightPixels * 1).toInt()

                // 다이얼로그 윈도우의 크기를 조절합니다. 이 부분은 dialog.show() 이후에 위치해야 합니다.
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, maxHeight)

                // RecyclerView의 높이를 조절합니다. 이 부분도 dialog.show() 이후에 위치해야 합니다.
                recyclerViewAccountBookUserConsumption.layoutParams = recyclerViewAccountBookUserConsumption.layoutParams.apply {
                    height = maxHeight // RecyclerView의 최대 높이를 설정합니다.
                }
            }
        }

        //viewHolder의 객체를 생성해서 반환한다
        //전체 행의 개수가 아닌 필요한 만큼만 행으로 사용할 view를 만들고 viewHolder도 생성한다
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            //viewBining
            val rowChatAccountBookUserListBinding = RowChatAccountBookUserListBinding.inflate(layoutInflater)
            //viewHolder
            val viewHolderClass = ViewHolderClass(rowChatAccountBookUserListBinding)

            // 클릭 이벤트를 설정해준다.
            rowChatAccountBookUserListBinding.root.setOnClickListener(viewHolderClass)

            val params = RecyclerView.LayoutParams(
                //가로길이
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            rowChatAccountBookUserListBinding.root.layoutParams = params

            return viewHolderClass
        }

        //전체 행의 개수를 반환
        override fun getItemCount(): Int {
            return 5
        }
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.textViewRowChatAccountBookUserListUserNickName.text = "유저$position"
            val money = formatNumberWithCommas(BigInteger("${position*50000}"))+" 원"
            holder.textViewRowChatAccountBookUserListUserConsumption.text = money
        }
    }

    //유저 개인 지출 리사이클러뷰
    inner class ChatAccountBookUserConsumptionRecyclerView : RecyclerView.Adapter<ChatAccountBookUserConsumptionRecyclerView.ViewHolderClass>() {
        inner class ViewHolderClass(rowUserConsumptionBinding: RowUserConsumptionBinding) :
            RecyclerView.ViewHolder(rowUserConsumptionBinding.root), View.OnClickListener {

            var textViewUserConsumptionDate : TextView
            var textViewUserConsumptionCategory : TextView
            var textViewUserConsumptionContent : TextView
            var textViewUserConsumptionOut : TextView

            init {
                // 사용하고자 하는 View를 변수에 담아준다.
                textViewUserConsumptionDate = rowUserConsumptionBinding.textViewUserConsumptionDate
                textViewUserConsumptionCategory = rowUserConsumptionBinding.textViewUserConsumptionCategory
                textViewUserConsumptionContent = rowUserConsumptionBinding.textViewUserConsumptionContent
                textViewUserConsumptionOut = rowUserConsumptionBinding.textViewUserConsumptionOut
            }

            override fun onClick(v: View?) {
            }
        }

        //viewHolder의 객체를 생성해서 반환한다
        //전체 행의 개수가 아닌 필요한 만큼만 행으로 사용할 view를 만들고 viewHolder도 생성한다
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            //viewBining
            val rowUserConsumptionBinding = RowUserConsumptionBinding.inflate(layoutInflater)
            //viewHolder
            val viewHolderClass = ViewHolderClass(rowUserConsumptionBinding)

            // 클릭 이벤트를 설정해준다.
            rowUserConsumptionBinding.root.setOnClickListener(viewHolderClass)

            val params = RecyclerView.LayoutParams(
                //가로길이
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            rowUserConsumptionBinding.root.layoutParams = params

            return viewHolderClass
        }

        //전체 행의 개수를 반환
        override fun getItemCount(): Int {
            return 10
        }
        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.textViewUserConsumptionDate.text = "2023-11-11"
            holder.textViewUserConsumptionCategory.text = "운동"
            holder.textViewUserConsumptionContent.text = "헬스장 등록"
            holder.textViewUserConsumptionOut.text = "- 300,000 원"
        }
    }
    fun formatNumberWithCommas(number: BigInteger): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        return formatter.format(number)
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
class SpaceItemDecorationAll(private val bottomSpace: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = bottomSpace
        outRect.left = bottomSpace
        outRect.right = bottomSpace

    }
}
