package com.example.practica2.api


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practica2.R
import com.example.practica2.R.id.toolbar2
import com.example.practica2.api.searchdb.AppDatabase
import com.example.practica2.api.searchdb.SearchHistory
import com.example.practica2.api.searchdb.SearchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieHandler
import java.net.CookieManager
import okhttp3.JavaNetCookieJar


@Suppress("DEPRECATION")
class PrincipalProjectActivity : AppCompatActivity() {


    private lateinit var etSearch: AutoCompleteTextView
    private lateinit var btnSearch: Button
    private lateinit var rvResults: RecyclerView
    private lateinit var rvHistory: RecyclerView
    private lateinit var historyAdapter: SearchHistoryAdapter
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var adapter: ShowAdapter  // Cambiado a global
    private val shows = mutableListOf<Show>()
    private val histories = mutableListOf<SearchHistory>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.psearchapi)

        // Inicializar vistas
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        rvResults = findViewById(R.id.rvResults)
        rvHistory = findViewById(R.id.rvHistory)

        // Inicializar el adapter global
        adapter = ShowAdapter(shows) { show ->
            // Al hacer clic en un show, crea el Intent para ir a la nueva actividad
            val intent = Intent(this, ShowDetailActivity::class.java)
            intent.putExtra("show_id", show.id)  // Pasa el ID del show al intent
            startActivity(intent)
        }

        rvResults.layoutManager = LinearLayoutManager(this)
        rvResults.adapter = adapter

        // Configurar el AutoCompleteTextView para sugerencias de historial
        val suggestionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>() // Lista inicial vacía para las sugerencias
        )
        etSearch.setAdapter(suggestionAdapter)

        // Configurar RecyclerView para historial de búsqueda
        historyAdapter = SearchHistoryAdapter(histories) { query ->
            etSearch.setText(query) // Rellena el campo de texto con el historial seleccionado
            searchShows(query)      // Realiza la búsqueda con el texto seleccionado
        }

        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter

        val toolbar: Toolbar = findViewById(R.id.toolbar2)

        // Configura el Toolbar como la ActionBar de la actividad
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Busqueda"

        // Habilita el botón de "Atrás"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Configura Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.tvmaze.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(TvMazeApi::class.java)

        // Configuración de Room
        val db = AppDatabase.getDatabase(applicationContext)
        searchHistoryRepository = SearchHistoryRepository(db.searchHistoryDao())

        // Cargar historial de búsqueda al inicio
        loadSearchHistory(suggestionAdapter)

        // Botón de búsqueda
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                saveSearchQuery(query, suggestionAdapter)  // Pasa el suggestionAdapter aquí
                searchShows(query)                         // Realiza la búsqueda en la API
                rvResults.visibility = View.VISIBLE       // Mostrar los resultados
            } else {
                Toast.makeText(this, "Por favor, ingresa un texto para buscar", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveSearchQuery(query: String, suggestionAdapter: ArrayAdapter<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val searchHistory = SearchHistory(query = query)
            searchHistoryRepository.insertSearchHistory(searchHistory) // Guarda localmente
            saveSearchToServer(query) // Envía la búsqueda al servidor
            loadSearchHistory(suggestionAdapter) // Recarga el historial después de guardar
            Log.d("Database", "Búsqueda guardada: $query")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadSearchHistory(suggestionAdapter: ArrayAdapter<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val historyList = searchHistoryRepository.getAllSearchHistories()
            histories.clear()
            histories.addAll(historyList)

            val historyQueries = historyList.map { it.query }  // Extrae solo las palabras del historial

            withContext(Dispatchers.Main) {
                // Actualiza el ArrayAdapter de sugerencias
                suggestionAdapter.clear()
                suggestionAdapter.addAll(historyQueries)
                suggestionAdapter.notifyDataSetChanged()

                // Actualiza el RecyclerView de historial
                historyAdapter.notifyDataSetChanged()

                // Si tienes historial, muestra rvHistory, sino ocúltalo


                // Oculta los resultados si no estás mostrando búsqueda
                rvResults.visibility = View.GONE
            }
        }
    }


    private fun searchShows(query: String) {
        rvResults.adapter = adapter // Cambia a mostrar los resultados de búsqueda

        // Retrofit para buscar shows
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.tvmaze.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(TvMazeApi::class.java)

        api.searchShows(query).enqueue(object : Callback<List<ShowResponse>> {
            override fun onResponse(call: Call<List<ShowResponse>>, response: Response<List<ShowResponse>>) {
                if (response.isSuccessful) {
                    val results = response.body() ?: emptyList()
                    shows.clear()

                    for (showResponse in results) {
                        val show = showResponse.show
                        api.getShowCast(show.id).enqueue(object : Callback<List<CastMember>> {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onResponse(call: Call<List<CastMember>>, response: Response<List<CastMember>>) {
                                if (response.isSuccessful) {
                                    show.cast = response.body() ?: emptyList()
                                    shows.add(show)
                                    adapter.notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(call: Call<List<CastMember>>, t: Throwable) {
                                t.printStackTrace()
                            }
                        })
                    }
                    rvResults.visibility = View.VISIBLE // Asegúrate de mostrar los resultados
                }
            }

            override fun onFailure(call: Call<List<ShowResponse>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Esto hace que vuelva a la actividad anterior
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    ///Para la base de datos sistemausuarios
    private fun saveSearchToServer(query: String) {
        // URL del endpoint PHP
        val url = "http://10.0.2.2/practica2/searchdb.php"  // Reemplaza con tu servidor

        // Obtener el CookieManager global
        val cookieManager = CookieHandler.getDefault() as CookieManager

        // Configurar OkHttp para usar el CookieManager global
        val client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .build()

        // Construir el cuerpo del formulario
        val formBody = FormBody.Builder()
            .add("search_query", query) // El término de búsqueda
            .build()

        // Crear la solicitud
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        // Realizar la solicitud en un hilo de fondo usando corrutinas
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("Server", "Búsqueda guardada en el servidor: $responseBody")
                } else {
                    Log.e("Server", "Error al guardar la búsqueda en el servidor: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("Server", "Error de conexión: ${e.message}")
            }
        }
    }

}




class SearchHistoryAdapter(
    private val histories: List<SearchHistory>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pitem_search_history, parent, false)
        return SearchHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val history = histories[position]
        holder.tvQuery.text = history.query

        holder.itemView.setOnClickListener {
            onItemClick(history.query)  // Callback para manejar clics
        }
    }

    override fun getItemCount(): Int = histories.size

    class SearchHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuery: TextView = itemView.findViewById(R.id.tvQuery)
    }
}

