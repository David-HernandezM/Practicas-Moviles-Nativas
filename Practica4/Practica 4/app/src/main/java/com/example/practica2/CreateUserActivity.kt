package com.example.practica2
//http://localhost/practica2/crud.php

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

@Suppress("DEPRECATION")
class CreateUserActivity : AppCompatActivity() {
    // Retrofit interface definition for the createUser API endpoint
    interface ApiService {
        @POST("practica2/crud.php")  // URL del archivo PHP en tu servidor XAMPP
        fun createUser(@Body user: User): Call<Map<String, Any>>
    }

    // RetrofitInstance: configuramos Retrofit en un objeto singleton
    object RetrofitInstance {
        private const val BASE_URL = "http://10.0.2.2/" // Para emulador de Android Studio, usa la IP de tu PC para dispositivos físicos

        val api: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }

    // Modelo de datos User para la petición POST
    data class User(
        val nombre: String,
        val apellidoP: String,
        val apellidoM: String,
        val correo: String,
        val edad: Int,
        val userU: String,
        val passwordU: String,
        val rol: Int // 1 = Admin, 2 = Usuario
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createuser) // Usamos el layout directamente

        // Obtener las referencias a los componentes del layout usando findViewById
        val nombreEditText: EditText = findViewById(R.id.nombre)
        val apellidoPEditText: EditText = findViewById(R.id.apellidoP)
        val apellidoMEditText: EditText = findViewById(R.id.apellidoM)
        val correoEditText: EditText = findViewById(R.id.correo)
        val edadEditText: EditText = findViewById(R.id.edad)
        val userUEditText: EditText = findViewById(R.id.userU)
        val passwordUEditText: EditText = findViewById(R.id.passwordU)
        val radioAdmin: RadioButton = findViewById(R.id.radioAdmin)
        val radioUsuario: RadioButton = findViewById(R.id.radioUsuario)
        val buttonSend: Button = findViewById(R.id.buttonsend)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        // Configura el Toolbar como la ActionBar de la actividad
        setSupportActionBar(toolbar)

        // Habilita el botón de "Atrás"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        // Configurar el botón para enviar los datos
        buttonSend.setOnClickListener {
            // Obtener los datos del formulario
            val nombre = nombreEditText.text.toString()
            val apellidoP = apellidoPEditText.text.toString()
            val apellidoM = apellidoMEditText.text.toString()
            val correo = correoEditText.text.toString()
            val edad = edadEditText.text.toString().toIntOrNull() ?: 0
            val userU = userUEditText.text.toString()
            val passwordU = passwordUEditText.text.toString()

            // Determinar el rol del usuario (Admin o Usuario)
            val rol = if (radioAdmin.isChecked) 1 else 2

            // Crear un objeto User con los datos
            val user = User(nombre, apellidoP, apellidoM, correo, edad, userU, passwordU, rol)

            // Llamar al API para crear el usuario
            createUser(user)
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Aquí puedes poner la lógica para lo que quieres hacer cuando se presione "Atrás"
                onBackPressed() // Esto hace que vuelva a la actividad anterior
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Función que hace la solicitud POST a PHP para crear el usuario
    private fun createUser(user: User) {
        // Hacer la llamada al servidor usando Retrofit
        val call = RetrofitInstance.api.createUser(user)

        // Ejecutar la llamada de forma asíncrona
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    // Si la respuesta es exitosa, mostrar un mensaje
                    Toast.makeText(this@CreateUserActivity, "Usuario creado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    // Si hubo un error con la respuesta (ejemplo, error 400 o 500)
                    Toast.makeText(this@CreateUserActivity, "Error al crear usuario: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                // Si hubo un error de conexión o algo salió mal
                Toast.makeText(this@CreateUserActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}