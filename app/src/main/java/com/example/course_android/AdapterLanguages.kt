package com.example.course_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.course_android.base.adapter.BaseAdapter
import com.example.course_android.model.allCountries.Language
import com.example.course_android.model.oneCountry.LanguageOfOneCountry

class AdapterLanguages : BaseAdapter<LanguageOfOneCountry>() {

    class LanguageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvLanguages: AppCompatTextView = view.findViewById(R.id.item_lang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_language, parent, false
            )
        return LanguageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LanguageViewHolder) {
            val item = mDataList[position]
            holder.tvLanguages.text = item.name
        }
    }

}