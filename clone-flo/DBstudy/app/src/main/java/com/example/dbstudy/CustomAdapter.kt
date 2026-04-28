package com.example.dbstudy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdapter(private val list: ArrayList<Profile>, private val context: Context) : BaseAdapter() {

    // 데이터 세트의 개수를 반환
    override fun getCount(): Int {
        return list.size
    }

    // 지정된 위치와 관련된 데이터 항목을 반환
    override fun getItem(position: Int): Any {
        return list[position]
    }

    // 지정된 위치와 관련된 항목 ID를 반환
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 항목 뷰를 생성하거나 재사용하여 데이터를 바인딩
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_layout, null)

            holder = ViewHolder()
            holder.text1 = view.findViewById(R.id.text1)
            holder.text2 = view.findViewById(R.id.text2)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val profile = list[position]

        holder.text1?.text = "이름: ${profile.name} (${profile.age})"
        holder.text2?.text = "연락처: ${profile.phone}"

        return view
    }

    private class ViewHolder {
        var text1: TextView? = null
        var text2: TextView? = null
    }
}