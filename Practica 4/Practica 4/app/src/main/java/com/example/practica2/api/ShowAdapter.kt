package com.example.practica2.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica2.R

class ShowAdapter(private val shows: List<Show>, private val onItemClick: (Show) -> Unit) :
    RecyclerView.Adapter<ShowAdapter.ShowViewHolder>() {

    class ShowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val starring: TextView = view.findViewById(R.id.starring)
        val tvDescription: TextView = view.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pitem_showsearch, parent, false)
        return ShowViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        val show = shows[position]
        holder.tvName.text = show.name
        holder.starring.text = if (show.cast.isNotEmpty()) {
            "Starring: " + show.cast.joinToString { it.person.name }
        } else {
            "Sin actores disponibles"
        }

        Glide.with(holder.itemView.context)
            .load(show.image?.medium)
            .into(holder.ivPoster)

        holder.tvDescription.text = show.summary?.let {
            android.text.Html.fromHtml(it, android.text.Html.FROM_HTML_MODE_LEGACY).toString()
        } ?: "Sin descripción"

        // Configurar el listener para el clic
        holder.itemView.setOnClickListener {
            onItemClick(show)  // Llamar al listener pasando el show
        }
    }

    override fun getItemCount(): Int = shows.size
}


