package com.example.practica2

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Search
import androidx.compose.ui.text.input.ImeAction.Companion.Search
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HistorialFavsActivity : AppCompatActivity() {

    private lateinit var recyclerViewFavoritos: RecyclerView
    private lateinit var recyclerViewHistorial: RecyclerView
    private lateinit var idUserInput: EditText
    private lateinit var userUInput: EditText
    private lateinit var nombreInput: EditText
    private lateinit var apellidoPInput: EditText
    private lateinit var buttonSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.buscar_historial_favoritos)

        // Inicializar vistas
        recyclerViewFavoritos = findViewById(R.id.recyclerViewFavoritos)
        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial)
        idUserInput = findViewById(R.id.iduser)
        userUInput = findViewById(R.id.userU)
        nombreInput = findViewById(R.id.nombre)
        apellidoPInput = findViewById(R.id.apellidoP)
        buttonSearch = findViewById(R.id.buttonsendfav)

        // Configurar RecyclerViews
        recyclerViewFavoritos.layoutManager = LinearLayoutManager(this)
        recyclerViewHistorial.layoutManager = LinearLayoutManager(this)

        // Configurar botón de búsqueda
        buttonSearch.setOnClickListener {
            // Obtener los valores de los campos
            val idUser = idUserInput.text.toString().trim()
            val userU = userUInput.text.toString().trim()
            val nombre = nombreInput.text.toString().trim()
            val apellidoP = apellidoPInput.text.toString().trim()

            // Determinar cuál campo usar como parámetro de búsqueda
            val searchParam = when {
                idUser.isNotEmpty() -> idUser
                userU.isNotEmpty() -> userU
                nombre.isNotEmpty() -> nombre
                apellidoP.isNotEmpty() -> apellidoP
                else -> null
            }

            // Validar si se ingresó algún dato
            if (searchParam != null) {
                // Ejecutar la tarea de búsqueda
                FetchDataTask(recyclerViewFavoritos, recyclerViewHistorial).execute(searchParam)
            } else {
                // Mostrar mensaje de error
                Toast.makeText(this, "Por favor, introduce al menos un dato para buscar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // AsyncTask en Kotlin
    @Suppress("DEPRECATION")
    private class FetchDataTask(
        private val recyclerViewFavoritos: RecyclerView,
        private val recyclerViewHistorial: RecyclerView
    ) : AsyncTask<String, Void, String>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String {
            val searchParam = params[0] ?: ""
            val response = StringBuilder()
            try {
                val url = URL("http://10.0.2.2/practica2/get_favoritos_admin.php") // Cambia por tu URL real
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write("search_param=$searchParam")
                writer.flush()

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                reader.forEachLine { response.append(it) }

                writer.close()
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return response.toString()
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)

                // Procesar favoritos
                val favoritosArray = jsonObject.getJSONArray("favoritos")
                val favoritosList = mutableListOf<Favoritos>()
                for (i in 0 until favoritosArray.length()) {
                    val obj = favoritosArray.getJSONObject(i)
                    favoritosList.add(
                        Favoritos(
                            obj.optInt("id_favorito", -1),
                            obj.optString("api_url", "N/A"),
                            obj.optString("api_respuesta", "N/A"),
                            obj.optString("fecha_guardado", "N/A")
                        )
                    )
                }

                // Procesar search
                val searchArray = jsonObject.getJSONArray("search")
                val searchList = mutableListOf<Search>()
                for (i in 0 until searchArray.length()) {
                    val obj = searchArray.getJSONObject(i)
                    searchList.add(
                        Search(
                            obj.optInt("id_search", -1),
                            obj.optString("search_query", "N/A"),
                            obj.optString("search_date", "N/A")
                        )
                    )
                }

                // Asignar adaptadores
                recyclerViewFavoritos.adapter = BAdapterFav(favoritosList)
                recyclerViewHistorial.adapter = BAdapterSearch(searchList)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(recyclerViewFavoritos.context, "Error al procesar datos", Toast.LENGTH_SHORT).show()
            }
        }

    }

}

data class Favoritos(
    val idFavorito: Int,
    val apiUrl: String,
    val apiRespuesta: String,
    val fechaGuardado: String
)

data class Search(
    val idSearch: Int,
    val searchQuery: String,
    val searchDate: String
)

