package com.example.practica2.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica2.R

class FavoriteAdapter(
    private val favorites: List<Favorite>,
    private val onItemClick: (Favorite) -> Unit // Listener para clics en los ítems
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    // ViewHolder para almacenar referencias a las vistas
    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDescription: TextView = view.findViewById(R.id.description)
        val tvStarring: TextView = view.findViewById(R.id.starring)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pitem_showsearch, parent, false) // Asegúrate de que este layout tenga los IDs correctos
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favorites[position]
        holder.tvName.text = favorite.name
        holder.tvDescription.text = favorite.description
        holder.tvStarring.text = favorite.starring

        // Carga la imagen con Glide
        Glide.with(holder.itemView.context)
            .load(favorite.posterUrl)
            .into(holder.ivPoster)

        // Configurar el clic en el ítem
        holder.itemView.setOnClickListener {
            onItemClick(favorite) // Llama al listener pasando el elemento actual
        }
    }

    override fun getItemCount(): Int = favorites.size
}