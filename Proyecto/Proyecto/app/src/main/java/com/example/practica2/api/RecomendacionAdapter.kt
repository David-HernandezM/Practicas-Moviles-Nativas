package com.example.practica2.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica2.R
import com.example.practica2.Recomendacion

class RecomendacionAdapter(private val recomendaciones: MutableList<Recomendacion>) :
    RecyclerView.Adapter<RecomendacionAdapter.RecomendacionViewHolder>() {

    class RecomendacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster) // Vincu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecomendacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recomendaciones, parent, false)
        return RecomendacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecomendacionViewHolder, position: Int) {
        val recomendacion = recomendaciones[position]
        holder.tvName.text = recomendacion.name

        Glide.with(holder.itemView.context)
            .load(recomendacion.posterUrl) // URL de la imagen
            .into(holder.ivPoster)
    }

    override fun getItemCount(): Int = recomendaciones.size
}

data class Recomendacion(
    val id: Int,
    val name: String,
    val description: String,
    var starring: String,
    val posterUrl: String
)