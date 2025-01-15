package com.example.practica2.api

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.practica2.R


class FavoriteActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mostrar_favoritos)


        // Configura el RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.mostrarfav)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Asigna un adaptador vacío inicialmente
        // Llama a la función para cargar los favoritos desde el servidor
        loadFavorites(recyclerView)
    }

    private fun loadFavorites(recyclerView: RecyclerView) {
        val url = "http://10.0.2.2/practica2/get_favoritos.php"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val favorites = mutableListOf<Favorite>()

                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    favorites.add(
                        Favorite(
                            id = jsonObject.getInt("id"),
                            name = jsonObject.getString("name"),
                            description = jsonObject.getString("description"),
                            starring = jsonObject.getString("starring"),
                            posterUrl = jsonObject.getString("posterUrl")
                        )
                    )
                }

                // Asignar el adaptador después de obtener los datos
                val adapter = FavoriteAdapter(favorites) { favorite ->
                    Toast.makeText(this, "Clic en: ${favorite.name}", Toast.LENGTH_SHORT).show()
                }
                recyclerView.adapter = adapter

                Log.d("FavoriteActivity", "Adaptador asignado con ${favorites.size} elementos")
            },
            { error ->
                Toast.makeText(this, "Error al cargar favoritos: $error", Toast.LENGTH_SHORT).show()
                Log.e("FavoriteActivity", "Error al cargar datos: $error")
            }
        ).apply {
            // Configurar tiempo de espera, intentos y multiplicador
            retryPolicy = DefaultRetryPolicy(
                20000, // Tiempo de espera en milisegundos (20 segundos)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Número de reintentos
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Multiplicador para los intentos posteriores
            )
        }


        requestQueue.add(jsonArrayRequest)
    }



}

data class Favorite(
    val id: Int,                // Identificador único del favorito
    val name: String,           // Nombre del favorito
    val description: String,    // Descripción del favorito
    val starring: String,       // Actores o información destacada
    val posterUrl: String       // URL de la imagen del favorito
)