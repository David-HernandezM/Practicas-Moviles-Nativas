package com.example.practica2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import android.util.Base64  // Asegúrate de importar esta clase
import com.example.practica2.api.FavoriteActivity
import com.example.practica2.api.PrincipalProjectActivity
import com.example.practica2.api.RecomendacionesActivity
import java.net.URL
import kotlin.io.encoding.ExperimentalEncodingApi


class VistaUsuarioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vistausuario)

        // Botón de actualización
        val buttonupdate = findViewById<Button>(R.id.buttoncreate)
        val intentupdate = Intent(this, UpdateOnlyUserActivity::class.java)

        val buttonweb = findViewById<Button>(R.id.buttonweb)
        val intentweb = Intent(this, WebViewerUser::class.java)

        val buttonproject = findViewById<Button>(R.id.project)
        val intentproject = Intent(this, PrincipalProjectActivity::class.java)

        //boton favoritos
        val favoritos = findViewById<TextView>(R.id.fav)
        val intentfavoritos = Intent(this, FavoriteActivity::class.java)

        // Botón de cerrar sesión
        val cerrarSesionButton: Button = findViewById(R.id.cerrarsesion)

        // Vistas donde mostraremos el ID y la imagen del usuario
        val textViewId = findViewById<TextView>(R.id.textViewIdUsuario)
        val imageView = findViewById<ImageView>(R.id.imageView)

        //boton recomendaciones
        val recomendaciones = findViewById<TextView>(R.id.recomendaciones)
        val intentrecomendaciones = Intent(this, RecomendacionesActivity::class.java)




        // Acción al presionar el botón de actualizar usuario
        buttonupdate.setOnClickListener {
            Log.d("VistaUsuarioActivity", "Button clicked, starting Update usuario")
            startActivity(intentupdate)
        }

        buttonweb.setOnClickListener {
            Log.d("VistaUsuarioActivity", "Button clicked, starting paginaweb")
            startActivity(intentweb)
        }

        buttonproject.setOnClickListener {
            Log.d("VistaUsuarioActivity", "Button clicked, starting paginaweb")
            startActivity(intentproject)
        }

        favoritos.setOnClickListener {
            Log.d("VistaUsuarioActivity", "Button clicked, starting favoritos")
            startActivity(intentfavoritos)
        }

        recomendaciones.setOnClickListener {
            Log.d("VistaUsuarioActivity", "Button clicked, starting Recomendaciones")
            startActivity(intentrecomendaciones)
        }

        // Obtener y mostrar los datos del usuario (ID y la imagen)
        fetchUserDataAndDisplay(textViewId, imageView)

        cerrarSesionButton.setOnClickListener {
            cerrarSesion()
        }
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
}