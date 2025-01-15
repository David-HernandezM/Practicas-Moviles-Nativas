package com.example.practica2
//ESTE ARCHIVO ES IMPORTANTE PARA LA INSERCION DE DATOS A LA BASE DE DATOS POR MEDIO DE PHP
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        val nombre = findViewById<EditText>(R.id.nombre)
        val apellidoP = findViewById<EditText>(R.id.apellidoP)
        val apellidoM = findViewById<EditText>(R.id.apellidoM)
        val correo = findViewById<EditText>(R.id.correo)
        val userU = findViewById<EditText>(R.id.userU)
        val passwordU = findViewById<EditText>(R.id.passwordU)
        val edad = findViewById<EditText>(R.id.edad)
        val genero = findViewById<EditText>(R.id.genero)
        val buttonsend = findViewById<Button>(R.id.buttonsend)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        // Configura el Toolbar como la ActionBar de la actividad
        setSupportActionBar(toolbar)

        // Habilita el botón de "Atrás"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        buttonsend.setOnClickListener {
            if (nombre.text.isEmpty() || apellidoP.text.isEmpty() || apellidoM.text.isEmpty() ||
                correo.text.isEmpty() || userU.text.isEmpty() || passwordU.text.isEmpty() ||
                edad.text.isEmpty() || genero.text.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://10.0.2.2/practica2/register.php"  // Cambia a tu IP local

            // Crear la solicitud de cadena
            val stringRequest = object : StringRequest(
                Method.POST, url,
                com.android.volley.Response.Listener { response ->
                    // Manejar la respuesta
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                },
                com.android.volley.Response.ErrorListener { error ->
                    // Manejar el error
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["nombre"] = nombre.text.toString().trim()
                    params["apellidoP"] = apellidoP.text.toString().trim()
                    params["apellidoM"] = apellidoM.text.toString().trim()
                    params["correo"] = correo.text.toString().trim()
                    params["userU"] = userU.text.toString().trim()
                    params["passwordU"] = passwordU.text.toString().trim()
                    params["edad"] = edad.text.toString().trim()
                    params["genero"] = genero.text.toString().trim()
                    return params
                }
            }

            // Agregar la solicitud a la cola
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(stringRequest)
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Esto hace que vuelva a la actividad anterior
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
