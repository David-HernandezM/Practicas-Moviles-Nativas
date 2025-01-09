package com.example.practica2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class UpdateOnlyUserActivity : ComponentActivity() {
    private lateinit var imageButton: ImageButton
    private val requestImagePick = 100
    private var imageBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.updateonlyuser)

        // Inicializar el ImageButton
        imageButton = findViewById(R.id.imageButton)

        // Configurar el ImageButton para abrir la galería
        imageButton.setOnClickListener {
            openGallery()
        }

        // Referencias a los EditText (sin idUsuario y RadioButtons)
        val nombreEditText = findViewById<EditText>(R.id.nombre)
        val apellidoPEditText = findViewById<EditText>(R.id.apellidoP)
        val apellidoMEditText = findViewById<EditText>(R.id.apellidoM)
        val correoEditText = findViewById<EditText>(R.id.correo)
        val generoEditText = findViewById<EditText>(R.id.genero)
        val edadEditText = findViewById<EditText>(R.id.edad)
        val userUEditText = findViewById<EditText>(R.id.userU)
        val passwordUEditText = findViewById<EditText>(R.id.passwordU)
        val buttonSend = findViewById<Button>(R.id.buttonsend)

        buttonSend.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val apellidoP = apellidoPEditText.text.toString()
            val apellidoM = apellidoMEditText.text.toString()
            val correo = correoEditText.text.toString()
            val genero = generoEditText.text.toString()
            val edad = edadEditText.text.toString().toIntOrNull() ?: run {
                Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userU = userUEditText.text.toString()
            val passwordU = passwordUEditText.text.toString()

            val userUpdateRequest = UserUpdateRequest(
                nombre = nombre,
                apellidoP = apellidoP,
                apellidoM = apellidoM,
                userU = userU,
                passwordU = passwordU.ifEmpty { null },
                edad = edad,
                correo = correo,
                genero = genero,
                imagen = imageBase64 // Añadir la imagen en Base64
            )

            userUpdateRequest.updateUserData()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, requestImagePick)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestImagePick && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                imageButton.setImageURI(imageUri)

                // Convertir la imagen a Base64
                val imageStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(imageStream)
                imageBase64 = encodeImageToBase64(bitmap)
            }
        }
    }

    // Función para convertir la imagen en Base64
    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeBase64String(byteArray)
    }

    // Nueva clase de datos sin `idUsuario` ni `rol`
    data class UserUpdateRequest(
        val nombre: String,
        val apellidoP: String,
        val apellidoM: String,
        val userU: String,
        val passwordU: String?,
        val edad: Int,
        val correo: String,
        val genero: String,
        val imagen: String? // Campo opcional para la imagen
    )

    private fun UserUpdateRequest.updateUserData() {
        Thread {
            try {
                val url = URL("http://10.0.2.2/practica2/updateonlyuser.php")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Crear el objeto JSON dinámicamente
                val jsonObject = JSONObject()

                if (nombre.isNotEmpty()) jsonObject.put("nombre", nombre)
                if (apellidoP.isNotEmpty()) jsonObject.put("apellidoP", apellidoP)
                if (apellidoM.isNotEmpty()) jsonObject.put("apellidoM", apellidoM)
                if (userU.isNotEmpty()) jsonObject.put("userU", userU)
                passwordU?.let { jsonObject.put("passwordU", it) }
                if (edad > 0) jsonObject.put("edad", edad) // Asegurarse de que la edad es válida
                if (correo.isNotEmpty()) jsonObject.put("correo", correo)
                if (genero.isNotEmpty()) jsonObject.put("genero", genero)

                // Incluir la imagen si está presente
                imagen?.let {
                    jsonObject.put("imagen", it)
                }

                Log.d("UserUpdateRequest", jsonObject.toString())

                val outputStream = connection.outputStream
                outputStream.write(jsonObject.toString().toByteArray())
                outputStream.flush()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream.bufferedReader().use { it.readText() }
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Usuario actualizado con éxito", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error al actualizar usuario", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(applicationContext, "Ocurrió un error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

}

