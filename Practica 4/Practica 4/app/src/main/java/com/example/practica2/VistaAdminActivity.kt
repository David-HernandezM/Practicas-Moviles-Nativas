package com.example.practica2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class VistaAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vistaadmin)

        val buttononlyuser = findViewById<Button>(R.id.modificadatos)
        val intentonlyuser = Intent(this, UpdateOnlyUserActivity::class.java)

        //Variables para boton de crear
        val buttoncreate = findViewById<Button>(R.id.buttoncreate)
        val intentcreate = Intent(this, CreateUserActivity::class.java)

        //Variables para boton de leer
        val buttonread = findViewById<Button>(R.id.buttonread)
        val intentread = Intent(this, ReadUserActivity::class.java)

        val buttonfavs = findViewById<Button>(R.id.buttonfavshist)
        val intentbuttonfavs = Intent(this, HistorialFavsActivity::class.java)

        //variables para boton de actualizar
        val buttonupdate = findViewById<Button>(R.id.updateuser)
        val intentupdate = Intent(this, UpdateUserActivity::class.java)

        //Variables para boton de borrar
        val buttondelete = findViewById<Button>(R.id.buttondelete)
        val intentdelete = Intent(this, DeleteUserActivity::class.java)

        // Vistas donde mostraremos el ID y la imagen del usuario
        val textViewId = findViewById<TextView>(R.id.textViewIdUsuario)
        val imageView = findViewById<ImageView>(R.id.imageView)


        // Obtener y mostrar los datos del usuario (ID y la imagen)
        fetchUserDataAndDisplay(textViewId, imageView)

        val cerrarSesionButton: Button = findViewById(R.id.cerrarsesion)

        buttononlyuser.setOnClickListener {
            Log.d("MainActivity", "Button clicked, starting Actualizar mis datos")
            startActivity(intentonlyuser)
        }


        buttoncreate.setOnClickListener {
            Log.d("MainActivity", "Button clicked, starting Crear usuario")
            startActivity(intentcreate)
        }

        buttonread.setOnClickListener {
            Log.d("MainActivity", "Button clicked, starting buscar usuarios")
            startActivity(intentread)
        }

        buttonfavs.setOnClickListener {
            Log.d("MainActivity", "Button clicked, starting buscar favoritos")
            startActivity(intentbuttonfavs)
        }

        buttonupdate.setOnClickListener {
            Log.d("MainActivity", "Button clicked, starting actualizar datos")
            startActivity(intentupdate)
        }

        buttondelete.setOnClickListener {
            Log.d("MainActivity", "Button clicked, starting borrar datos")
            startActivity(intentdelete)
        }


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
                    Toast.makeText(this@VistaAdminActivity, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
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

    }



