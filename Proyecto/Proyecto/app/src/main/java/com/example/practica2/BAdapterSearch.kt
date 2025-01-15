package com.example.practica2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BAdapterSearch (private val searchList: List<Search>) :
RecyclerView.Adapter<BAdapterSearch.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searchQuery: TextView = itemView.findViewById(R.id.search_query)
        val searchDate: TextView = itemView.findViewById(R.id.search_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val search = searchList[position]
        holder.searchQuery.text = search.searchQuery
        holder.searchDate.text = search.searchDate
    }

    override fun getItemCount() = searchList.size
}