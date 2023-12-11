package com.example.georgeois.utill

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.example.georgeois.databinding.ItemSpinnerHomeRegisterBinding

// 카테고리 스피너 Adapter
class CategorySpinnerAdapter(context: Context, @LayoutRes private val resId:Int, private val categoryList : List<String>):
    ArrayAdapter<String>(context,resId,categoryList){
    // 드롭다운하지 않은 상태의 Spinner 항목 뷰
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemSpinnerHomeRegisterBinding = ItemSpinnerHomeRegisterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        itemSpinnerHomeRegisterBinding.textViewHomeRegisterSpinnerItem.text = categoryList[position]
        return itemSpinnerHomeRegisterBinding.root
    }

    // 드롭다운된 항목들 리스트의 뷰
    @SuppressLint("ViewHolder")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemSpinnerHomeRegisterBinding = ItemSpinnerHomeRegisterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        itemSpinnerHomeRegisterBinding.textViewHomeRegisterSpinnerItem.text = categoryList[position]
        return itemSpinnerHomeRegisterBinding.root
    }

    override fun getCount() = categoryList.size

}