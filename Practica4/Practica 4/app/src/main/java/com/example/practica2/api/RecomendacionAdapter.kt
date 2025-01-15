package com.example.practica2.api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica2.R
import com.example.practica2.api.ui.theme.Practica2Theme

class RecomendacionAdapter(private val recomendaciones: List<Recomendacion>) :
    RecyclerView.Adapter<RecomendacionAdapter.RecomendacionViewHolder>() {

    class RecomendacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val description: TextView = view.findViewById(R.id.description)
        val starring: TextView = view.findViewById(R.id.starring)
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster) // Vincu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecomendacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pitem_showsearch, parent, false)
        return RecomendacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecomendacionViewHolder, position: Int) {
        val recomendacion = recomendaciones[position]
        holder.tvName.text = recomendacion.name
        holder.description.text = recomendacion.description
        holder.starring.text = recomendacion.starring
        holder.starring.text = recomendacion.starring

        Glide.with(holder.itemView.context)
            .load(recomendacion.posterUrl) // URL de la imagen
            .into(holder.ivPoster)
    }

    override fun getItemCount(): Int = recomendaciones.size
}

