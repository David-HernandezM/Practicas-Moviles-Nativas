package com.example.practica2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.net.CookieHandler
import java.net.CookieManager

class LoginActivity : AppCompatActivity() { // Constante para los 5 minutos en milisegundos
    private val SESSION_DURATION = 5 * 60 * 1000 // 5 minutos en milisegundos

    private val CHANNEL_ID = "login_notification_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val buttonSend2 = findViewById<Button>(R.id.buttonSend2)
        val editTextUsuario = findViewById<EditText>(R.id.usuarioU)
        val editTextPassword = findViewById<EditText>(R.id.passwordU)

        // Verificar si la sesión está activa al iniciar
        if (isSessionActive()) {
            redirectToActivityBasedOnRole()
            return
        }

        buttonSend2.setOnClickListener {
            val userU = editTextUsuario.text.toString().trim()
            val passwordU = editTextPassword.text.toString().trim()

            if (userU.isEmpty() || passwordU.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val url = "http://10.0.2.2/practica2/login.php"

            val stringRequest = @RequiresApi(Build.VERSION_CODES.O)
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    try {
                        val jsonResponse = JSONObject(response)
                        val status = jsonResponse.getString("status")
                        val role = jsonResponse.getString("role")

                        if (status == "success") {
                            saveSessionStartTime()

                            // Guardar el rol en SharedPreferences
                            saveUserRole(role)

                            // Enviar la notificación de inicio de sesión exitoso
                            sendLoginNotification()

                            // Redirigir según el rol
                            if (role == "admin") {
                                navigateToAdminActivity()
                            } else {
                                navigateToUserActivity()
                            }
                        } else {
                            Toast.makeText(this, "Error en el inicio de sesión", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this, "Error al parsear la respuesta", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userU"] = userU
                    params["passwordU"] = passwordU
                    return params
                }
            }

            // Habilitar manejo de cookies
            val requestQueue = Volley.newRequestQueue(this)
            val cookieManager = CookieManager()
            CookieHandler.setDefault(cookieManager)
            requestQueue.add(stringRequest)
        }
    }

    private fun saveSessionStartTime() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("session_start", System.currentTimeMillis())
        editor.apply()
    }

    private fun isSessionActive(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        val sessionStart = sharedPreferences.getLong("session_start", 0)
        val currentTime = System.currentTimeMillis()
        return sessionStart != 0L && (currentTime - sessionStart) < SESSION_DURATION
    }

    private fun saveUserRole(role: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_role", role)
        editor.apply()
    }

    private fun redirectToActivityBasedOnRole() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        val userRole = sharedPreferences.getString("user_role", "")

        when (userRole) {
            "admin" -> navigateToAdminActivity()
            "usuario" -> navigateToUserActivity()
            else -> navigateToLoginActivity()
        }
    }

    private fun navigateToAdminActivity() {
        val intent = Intent(this, VistaAdminActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToUserActivity() {
        val intent = Intent(this, VistaUsuarioActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
/////NOTIFICACION/////
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendLoginNotification() {
        // Crea un canal de notificación si está en Android 8.0 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Login Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de inicio de sesión"
            }

            // Registrar el canal en el sistema
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Construir la notificación
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Inicio de sesión exitoso")
            .setContentText("Has iniciado sesión correctamente.")
            .setSmallIcon(R.drawable.baseline_color_lens_24) // Cambia esto según tu icono
            .build()

        // Mostrar la notificación
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}