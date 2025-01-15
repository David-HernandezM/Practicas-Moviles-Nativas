package com.example.practica2.api

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.jsoup.Jsoup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.practica2.FavoriteManager
import com.example.practica2.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.practica2.api.EpisodeAdapter
import com.android.volley.Request
import org.json.JSONObject
import org.json.JSONException

//Aqui se muestra lo relacionado a la serie seleccionada en la busqueda realizada...
class ShowDetailActivity : AppCompatActivity() {
    private lateinit var tvShowName: TextView
    private lateinit var tvShowDescription: TextView
    private lateinit var tvStarring: TextView // Para mostrar los actores
    private lateinit var ivPoster: ImageView
    private lateinit var api: TvMazeApi
    private lateinit var rvSeasons: RecyclerView
    private lateinit var rvEpisodes: RecyclerView
    private lateinit var btnFavorite: ImageButton
    private lateinit var favoritesManager: FavoriteManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showmovie) // Asegúrate de que este es el layout correcto

        // Inicializar las vistas
        tvShowName = findViewById(R.id.tvName)
        tvShowDescription = findViewById(R.id.description)
        tvStarring = findViewById(R.id.starring)
        ivPoster = findViewById(R.id.ivPoster)
        rvSeasons = findViewById(R.id.rvSeasons)
        rvEpisodes = findViewById(R.id.rvEpisodes)
        btnFavorite = findViewById(R.id.btnFavorite)

        // Configurar el RecyclerView
        rvSeasons.layoutManager = LinearLayoutManager(this)
        rvEpisodes.layoutManager = LinearLayoutManager(this)

        favoritesManager = FavoriteManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.tvmaze.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(TvMazeApi::class.java)

        // Obtener el ID del show desde el Intent
        val showId = intent.getIntExtra("show_id", -1)
        if (showId != -1) {
            fetchShowDetails(showId)
            fetchShowSeasons(showId)
            setupFavoriteButton(showId)
        } else {
            Toast.makeText(this, "Show ID no válido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupFavoriteButton(showId: Int) {
        updateFavoriteIcon(showId)

        btnFavorite.setOnClickListener {
            val apiUrl = "https://api.tvmaze.com/shows/$showId" // Construye la URL completa
            val apiResponse = "Detalles de la API" // Reemplaza con la respuesta real si es necesario
            val userId = 1 // Reemplaza con el ID del usuario actual

            if (favoritesManager.isFavorite(showId.toString())) {
                favoritesManager.removeFavorite(showId.toString())
                Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
            } else {
                favoritesManager.addFavorite(showId.toString())
                addFavoriteToDatabase(userId, apiUrl, apiResponse) // Agregar favorito a la base de datos
                Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
            }
            updateFavoriteIcon(showId)
        }
    }

    private fun updateFavoriteIcon(showId: Int) {
        if (favoritesManager.isFavorite(showId.toString())) {
            btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24) // Icono de favorito
        } else {
            btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24) // Icono sin favorito
        }
    }

    private fun fetchShowSeasons(showId: Int) {
        api.getShowSeasons(showId).enqueue(object : Callback<List<Season>> {
            override fun onResponse(call: Call<List<Season>>, response: Response<List<Season>>) {
                if (response.isSuccessful) {
                    val seasons = response.body() ?: emptyList()

                    rvSeasons.adapter = SeasonAdapter(seasons) { seasonId ->
                        fetchSeasonEpisodes(seasonId)
                    }
                }
            }

            override fun onFailure(call: Call<List<Season>>, t: Throwable) {
                Toast.makeText(this@ShowDetailActivity, "Error al cargar temporadas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchSeasonEpisodes(seasonId: Int) {
        api.getSeasonEpisodes(seasonId).enqueue(object : Callback<List<Episode>> {
            override fun onResponse(call: Call<List<Episode>>, response: Response<List<Episode>>) {
                if (response.isSuccessful) {
                    val episodes = response.body() ?: emptyList()
                    rvEpisodes.adapter = EpisodeAdapter(episodes)
                    rvEpisodes.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@ShowDetailActivity, "No se pudieron cargar los episodios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Episode>>, t: Throwable) {
                Toast.makeText(this@ShowDetailActivity, "Error al cargar episodios", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchShowDetails(showId: Int) {
        api.getShowDetails(showId).enqueue(object : Callback<Show> {
            override fun onResponse(call: Call<Show>, response: Response<Show>) {
                if (response.isSuccessful) {
                    val show = response.body()
                    show?.let {
                        tvShowName.text = it.name

                        val cleanDescription = it.summary?.let { summary ->
                            Jsoup.parse(summary).text()
                        } ?: "Sin descripción"
                        tvShowDescription.text = cleanDescription

                        fetchShowCast(showId)

                        val imageUrl = it.image?.medium
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this@ShowDetailActivity)
                                .load(imageUrl)
                                .into(ivPoster)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Show>, t: Throwable) {
                Toast.makeText(this@ShowDetailActivity, "Error al cargar detalles", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchShowCast(showId: Int) {
        api.getShowCast(showId).enqueue(object : Callback<List<CastMember>> {
            override fun onResponse(call: Call<List<CastMember>>, response: Response<List<CastMember>>) {
                if (response.isSuccessful) {
                    val castMembers = response.body()
                    castMembers?.let {
                        if (it.isNotEmpty()) {
                            val actors = it.joinToString { castMember -> castMember.person.name }
                            tvStarring.text = "Starring: $actors"
                        } else {
                            tvStarring.text = "No actors available"
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<CastMember>>, t: Throwable) {
                Toast.makeText(this@ShowDetailActivity, "Error al cargar actores", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addFavoriteToDatabase(userId: Int, apiUrl: String, apiResponse: String) {
        val url = "http://10.0.2.2/practica2/favoritos.php"
        val requestQueue = Volley.newRequestQueue(this)

        val postRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val success = jsonResponse.getBoolean("success")
                    val message = jsonResponse.getString("message")

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error en la respuesta: $e", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error en la solicitud: $error", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "id_usuario" to userId.toString(),
                    "api_url" to apiUrl,
                    "api_respuesta" to apiResponse
                )
            }
        }

        requestQueue.add(postRequest)
    }

}
data class Season(
    val id: Int,
    val number: Int,
    val episodeOrder: Int?  // Cantidad de episodios
)

data class Episode(
    val id: Int,
    val name: String,       // Nombre del capítulo
    val number: Int,        // Número del capítulo
    val airdate: String?,   // Fecha de emisión
    val rating: Rating?     // Puntaje del capítulo
)

data class Rating(
    val average: Double?    // Calificación promedio
)



