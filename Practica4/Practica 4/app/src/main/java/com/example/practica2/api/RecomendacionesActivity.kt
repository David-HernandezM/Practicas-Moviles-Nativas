package com.example.practica2.api

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.practica2.R
import org.json.JSONObject
import java.net.CookieHandler
import java.net.CookieManager

class RecomendacionesActivity : ComponentActivity()  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecomendacionAdapter
    private val recomendaciones = mutableListOf<Recomendacion>()
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recomendaciones)

        // Configuración de RecyclerView
        recyclerView = findViewById(R.id.mostrarrecomendaciones)
        adapter = RecomendacionAdapter(recomendaciones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Crear la cola de peticiones de Volley
        requestQueue = Volley.newRequestQueue(this)

        // Obtener recomendaciones al azar
        obtenerRecomendaciones()
    }

    private fun obtenerRecomendaciones() {
        val url = "http://10.0.2.2/practica2/get_recomendaciones.php"

        val timeout = 30000 // 30 segundos
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val recommendationsArray = response.getJSONArray("recommendations")
                    for (i in 0 until recommendationsArray.length()) {
                        val item = recommendationsArray.getJSONObject(i)

                        val recomendacion = Recomendacion(
                            id = item.getInt("id"),
                            name = item.getString("name"),
                            description = item.optString("summary", "Sin descripción disponible").replace(Regex("<.*?>"), ""),
                            starring = "Cargando actores...",
                            posterUrl = item.optString("posterUrl", "https://via.placeholder.com/150")
                        )
                        Log.d("Poster URL", recomendacion.posterUrl)
                        recomendaciones.add(recomendacion)
                        obtenerActores(item.getInt("id"), recomendaciones.lastIndex)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.e("Recomendaciones", "Error procesando la API", e)
                }
            },
            { error ->
                Log.e("Recomendaciones", "Error en la petición a la API", error)
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

        requestQueue.add(jsonObjectRequest)
    }

    private fun obtenerActores(showId: Int, index: Int) {
        val url = "https://api.tvmaze.com/shows/$showId/cast"

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null, { response ->
            try {
                val nombresActores = mutableListOf<String>()
                for (i in 0 until response.length()) {
                    val actor = response.getJSONObject(i).getJSONObject("person")
                    nombresActores.add(actor.getString("name"))
                }
                recomendaciones[index].starring = nombresActores.joinToString(", ")
                adapter.notifyDataSetChanged() // Actualiza la vista
            } catch (e: Exception) {
                Log.e("Recomendaciones", "Error obteniendo actores para show ID $showId", e)
            }
        }, { error ->
            Log.e("Recomendaciones", "Error en la petición de actores para show ID $showId", error)
        })

        requestQueue.add(jsonArrayRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        requestQueue.cancelAll { true } // Cancelar todas las peticiones al destruir la actividad
    }
}



data class Recomendacion(
    val id: Int,
    val name: String,
    val description: String,
    var starring: String,
    val posterUrl: String
)

