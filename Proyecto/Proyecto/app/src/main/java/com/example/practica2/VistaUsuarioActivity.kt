package com.example.practica2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import android.util.Base64  // Asegúrate de importar esta clase
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.practica2.api.FavoriteActivity
import com.example.practica2.api.PrincipalProjectActivity
import com.example.practica2.api.RecomendacionAdapter
import java.net.URL
import kotlin.io.encoding.ExperimentalEncodingApi


class VistaUsuarioActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecomendacionAdapter
    private val recomendaciones = mutableListOf<Recomendacion>()
    private lateinit var requestQueue: RequestQueue



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vistausuario)

        // Configuración de RecyclerView
        recyclerView = findViewById(R.id.mostrarrecomendaciones)
        adapter = RecomendacionAdapter(recomendaciones)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val textViewId = findViewById<TextView>(R.id.textViewIdUsuario)
        val imageView = findViewById<ImageView>(R.id.imageView)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "PeliClick"

        fetchUserDataAndDisplay(textViewId, imageView)

        requestQueue = Volley.newRequestQueue(this)

        // Obtener recomendaciones al azar
        obtenerRecomendaciones()

    }

    @SuppressLint("SetTextI18n")
    private fun fetchUserDataAndDisplay(textView: TextView, imageView: ImageView) {
        // Usamos una corrutina para hacer la llamada en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            val response = fetchUserData()

            withContext(Dispatchers.Main) {
                if (response != null) {
                    val jsonResponse = JSONObject(response)
                    val userId = jsonResponse.optString("id_usuario")
                    val base64Image = jsonResponse.optString("imagen", null)  // Usamos "null" por defecto

                    // Mostrar el ID del usuario en el TextView
                    textView.text = "ID de Usuario: $userId"

                    // Mostrar la imagen del usuario en el ImageView (decodificando la imagen base64)
                    if (!base64Image.isNullOrEmpty()) {
                        // Si la imagen existe y no está vacía, decodificamos la imagen base64
                        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageView.setImageBitmap(bitmap)
                    } else {
                        // Si no hay imagen, oculta la ImageView o muestra un valor predeterminado
                        imageView.setImageResource(R.drawable.nousuario)  // Aquí puedes poner una imagen predeterminada si lo deseas
                    }
                } else {
                    Toast.makeText(this@VistaUsuarioActivity, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchUserData(): String? {
        // Reemplaza con la URL de tu servidor PHP
        val url = URL("http://10.0.2.2/practica2/getuserdata.php")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                return inputStream.bufferedReader().use { it.readText() }
            } else {
                return null
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun cerrarSesion() {
        // Obtener el SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("session", MODE_PRIVATE)

        // Editar el SharedPreferences para eliminar los datos de sesión
        val editor = sharedPreferences.edit()
        editor.clear() // Elimina todos los datos almacenados
        editor.apply() // Confirma los cambios

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Limpiar el historial de actividades
        startActivity(intent)

        // Finalizar la actividad actual
        finish()
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun showUserData(response: String) {
        try {
            // Parsear la respuesta JSON
            val jsonResponse = JSONObject(response)

            // Obtener el id_usuario y la imagen en base64
            val userId = jsonResponse.getString("id_usuario")
            val base64Image = jsonResponse.getString("imagen")

            // Log para depurar
            Log.d("VistaUsuarioActivity", "ID Usuario: $userId")
            Log.d("VistaUsuarioActivity", "Imagen Base64: $base64Image")

            // Decodificar la imagen base64
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT) // Asegúrate de que `base64Image` es un String
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Mostrar la imagen en el ImageView
            val imageView = findViewById<ImageView>(R.id.imageView)
            imageView.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("VistaUsuarioActivity", "Error al procesar los datos: ${e.message}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuusuario, menu)

        if (menu != null) {
            for (i in 0 until menu.size()) {
                val menuItem = menu.getItem(i)
                val spannableTitle = SpannableString(menuItem.title)
                spannableTitle.setSpan(ForegroundColorSpan(Color.BLACK), 0, spannableTitle.length, 0)
                menuItem.title = spannableTitle
            }
        }

        return true
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




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                // Finaliza la actividad actual al pulsar el botón "Atrás"
                finish()
                true
            }
            R.id.cambiar_datos -> {
                // Acción para "Cambiar datos"
                val intent = Intent(this, UpdateOnlyUserActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.navegador -> {
                // Acción para "Navegador"
                val intent = Intent(this, WebViewerUser::class.java)
                startActivity(intent)
                true
            }
            R.id.favoritos -> {
                // Acción para "Favoritos"
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                // Acción para "Cerrar sesión"
                cerrarSesion() // Usa el método cerrarSesion ya definido
                true
            }

            R.id.action_search -> {
                // Navegar a SearchActivity
                val intent = Intent(this, PrincipalProjectActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

data class Recomendacion(
    val id: Int,
    val name: String,
    val description: String,
    var starring: String,
    val posterUrl: String
)