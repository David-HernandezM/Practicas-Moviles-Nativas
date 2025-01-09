package com.example.practica2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

data class Usuario(
    val id_usuario: Int,
    val nombre: String,
    val apellidoP: String,
    val apellidoM: String,
    val correo: String,
    val edad: Int,
    val genero: String,
    val userU: String

)

class UsuarioAdapter(private val usuarios: List<Usuario>) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuarios, parent, false)
        return UsuarioViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.nombreTextView.text = "Nombre: ${usuario.nombre}"
        holder.apellidoPTextView.text = "Apellido Paterno: ${usuario.apellidoP}"
        holder.apellidoMTextView.text = "Apellido Materno: ${usuario.apellidoM}"
        holder.correoTextView.text = "Correo: ${usuario.correo}"
        holder.edadTextView.text = "Edad: ${usuario.edad}"
        holder.generoTextView.text = "Genero: ${usuario.genero}"
        holder.idTextView.text = "Id_Usuario: ${usuario.id_usuario.toString()}"
        holder.userTextView.text = "Usuario: ${usuario.userU}"

    }

    override fun getItemCount(): Int {
        return usuarios.size
    }

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.textViewNombre)
        val apellidoPTextView: TextView = itemView.findViewById(R.id.textViewApellidoP)
        val apellidoMTextView: TextView = itemView.findViewById(R.id.textViewApellidoM)
        val correoTextView: TextView = itemView.findViewById(R.id.textViewCorreo)
        val edadTextView: TextView = itemView.findViewById(R.id.textViewEdad)
        val generoTextView: TextView = itemView.findViewById(R.id.textViewGenero)
        val idTextView: TextView = itemView.findViewById(R.id.textViewId)
        val userTextView: TextView = itemView.findViewById(R.id.textViewUser)
    }
}

class ReadUserActivity : AppCompatActivity() {
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.readuser)  // Aquí cargamos el layout con el RecyclerView

        // Inicialización de elementos del layout
        val idUsuarioEditText: EditText = findViewById(R.id.iduser)
        val userUEditText: EditText = findViewById(R.id.userU)
        val nombreEditText: EditText = findViewById(R.id.nombre)
        val apellidoPEditText: EditText = findViewById(R.id.apellidoP)
        val searchButton: Button = findViewById(R.id.buttonsend)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewUsuarios)

        // Lista de usuarios que se actualizará dinámicamente
        val usuarios = mutableListOf<Usuario>()
        val adapter = UsuarioAdapter(usuarios)

        // Configuración del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Acción cuando el usuario presiona el botón "Buscar"
        searchButton.setOnClickListener {
            // Obtener los valores de los EditTexts
            val idUsuario = idUsuarioEditText.text.toString()
            val userU = userUEditText.text.toString()
            val nombre = nombreEditText.text.toString()
            val apellidoP = apellidoPEditText.text.toString()

            // Construir la URL base
            var url = "http://10.0.2.2/practica2/read.php?"  // URL base

            // Crear una lista de parámetros que no estén vacíos
            val params = mutableListOf<String>()
            if (idUsuario.isNotEmpty()) {
                params.add("id_usuario=$idUsuario")
            }
            if (userU.isNotEmpty()) {
                params.add("userU=$userU")
            }
            if (nombre.isNotEmpty()) {
                params.add("nombre=$nombre")
            }
            if (apellidoP.isNotEmpty()) {
                params.add("apellidoP=$apellidoP")
            }

            // Si hay parámetros, agregarlos a la URL
            if (params.isNotEmpty()) {
                url += params.joinToString("&")
            }

            // Realizar la solicitud HTTP para obtener los datos
            val stringRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    // Parsear la respuesta JSON
                    try {
                        val jsonArray = JSONArray(response)
                        usuarios.clear()  // Limpiar la lista de usuarios antes de agregar los nuevos resultados

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)

                            // Crear un objeto Usuario con los datos obtenidos
                            val usuario = Usuario(
                                jsonObject.getInt("id_usuario"),
                                jsonObject.getString("nombre"),
                                jsonObject.getString("apellidoP"),
                                jsonObject.getString("apellidoM"),
                                jsonObject.getString("correo"),
                                jsonObject.getInt("edad"),
                                jsonObject.getString("genero"),
                                jsonObject.getString("userU")

                            )
                            usuarios.add(usuario)
                        }

                        // Notificar al adapter para que se actualice la vista
                        adapter.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            applicationContext,
                            "Error al procesar los datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                { error ->
                    Toast.makeText(applicationContext, "Error en la conexión", Toast.LENGTH_SHORT)
                        .show()
                })

            // Agregar la solicitud a la cola de solicitudes de Volley
            val queue = Volley.newRequestQueue(this)
            queue.add(stringRequest)
        }
    }
}