package com.example.practica2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BAdapterFav(private val favoritosList: List<Favoritos>) :
    RecyclerView.Adapter<BAdapterFav.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val apiUrl: TextView = itemView.findViewById(R.id.api_url)
        val apiRespuesta: TextView = itemView.findViewById(R.id.api_respuesta)
        val fechaGuardado: TextView = itemView.findViewById(R.id.fecha_guardado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favorito = favoritosList[position]
        holder.apiUrl.text = favorito.apiUrl
        holder.apiRespuesta.text = favorito.apiRespuesta
        holder.fechaGuardado.text = favorito.fechaGuardado
    }

    override fun getItemCount() = favoritosList.size
}
