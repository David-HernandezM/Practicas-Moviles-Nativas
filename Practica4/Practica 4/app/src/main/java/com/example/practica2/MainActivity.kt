package com.example.practica2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cargar el archivo XML "principal.xml" ubicado en res/layout
        setContentView(R.layout.principal)
        //Variables para el login
       val buttonLogin = findViewById<Button>(R.id.buttonlogin)
        val intentLogin = Intent(this, LoginActivity::class.java)

        //Variables para el registro
        val buttonRegister = findViewById<Button>(R.id.buttonregister)
        val intentRegister = Intent(this, RegisterActivity::class.java)

        buttonLogin.setOnClickListener {
           Log.d("MainActivity", "Button clicked, starting LoginActivity")
            startActivity(intentLogin)
        }

        buttonRegister.setOnClickListener {
            Log.d("MainActivity", "Button clicked, starting RegisterActivity")
            startActivity(intentRegister)
        }


    }
}
