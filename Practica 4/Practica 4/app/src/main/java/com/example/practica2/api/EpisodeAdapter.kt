package com.example.practica2.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica2.R


class EpisodeAdapter(private val episodes: List<Episode>) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pitem_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.bind(episode)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    inner class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val episodeName: TextView = itemView.findViewById(R.id.tvEpisodeName)
        private val episodeNumber: TextView = itemView.findViewById(R.id.tvEpisodeNumber)
        private val episodeDate: TextView = itemView.findViewById(R.id.tvEpisodeAirDate)
        private val episodeScore: TextView = itemView.findViewById(R.id.tvEpisodeRating)

        fun bind(episode: Episode) {
            episodeName.text = episode.name
            episodeNumber.text = "Episode: ${episode.number}"
            episodeDate.text = "Air date: ${episode.airdate}"
            episodeScore.text = "Score: ${episode.rating?.average ?: "N/A"}"
        }
    }
}

