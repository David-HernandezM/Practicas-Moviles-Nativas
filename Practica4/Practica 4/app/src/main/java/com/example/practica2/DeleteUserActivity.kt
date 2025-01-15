package com.example.practica2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson

import com.google.gson.JsonObject
import org.json.JSONObject


class DeleteUserActivity : AppCompatActivity() {
    private lateinit var iduserEditText: EditText
    private lateinit var userUEditText: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deleteuser)

        // Inicializar las vistas
        iduserEditText = findViewById(R.id.iduser)
        userUEditText = findViewById(R.id.userU)
        sendButton = findViewById(R.id.buttonsend)

        // Configurar el botón para enviar la solicitud de eliminación
        sendButton.setOnClickListener {
            val idUsuarioStr = iduserEditText.text.toString()
            val userU = userUEditText.text.toString()

            // Validar los campos de entrada
            if (idUsuarioStr.isNotEmpty() && userU.isNotEmpty()) {
                // Verificar que idUsuario sea un número entero válido
                val idUsuario = idUsuarioStr.toIntOrNull()
                if (idUsuario != null) {
                    // Si es un número válido, proceder con la eliminación
                    deleteUser(idUsuario, userU)
                } else {
                    Toast.makeText(this, "El ID de usuario debe ser un número válido.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Debe proporcionar id_usuario y userU", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para eliminar un usuario
    private fun deleteUser(idUsuario: Int, userU: String) {
        val url = "http://10.0.2.2/practica2/delete.php"  // Cambia esta URL a la ruta de tu archivo PHP

        // Crear el objeto JSON que se enviará en el cuerpo de la solicitud
        val jsonObject = JSONObject()
        jsonObject.put("id_usuario", idUsuario)
        jsonObject.put("userU", userU)

        // Crear una solicitud POST usando Volley
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonObject,
            { response ->
                // Manejar la respuesta
                val message = response.optString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            },
            { error ->
                // Manejar el error
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            // Agregar los encabezados si es necesario
            override fun getHeaders(): Map<String, String> {
                val headers = mutableMapOf<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        // Crear una cola de solicitudes
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest)
    }

}
