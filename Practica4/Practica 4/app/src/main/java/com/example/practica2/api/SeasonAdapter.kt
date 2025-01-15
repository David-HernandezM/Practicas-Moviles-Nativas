package com.example.practica2.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica2.R


class SeasonAdapter(
    private val seasons: List<Season>,
    private val onSeasonClick: (Int) -> Unit  // Agrega un listener para los clics
) : RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder>() {

    class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSeasonNumber: TextView = itemView.findViewById(R.id.tvSeasonNumber)
        val tvEpisodeCount: TextView = itemView.findViewById(R.id.tvEpisodeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pitem_season, parent, false)
        return SeasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = seasons[position]
        holder.tvSeasonNumber.text = "Season ${season.number}"
        holder.tvEpisodeCount.text =
            "Episodes: ${season.episodeOrder ?: "N/A"}"  // Si no hay episodios, mostrar N/A

        // Agregar el listener al hacer clic
        holder.itemView.setOnClickListener {
            onSeasonClick(season.id)  // Pasa el ID de la temporada al hacer clic
        }
    }

    override fun getItemCount(): Int = seasons.size
}


