package com.example.practica2

import androidx.appcompat.app.AppCompatActivity

//Aqui asignamos la funcion de algunos botones para adminvista
class ButtonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener la opción seleccionada
        val opcion = intent.getIntExtra("opcion", 0)

        // Cambiar el layout según la opción
        when (opcion) {
            0 -> {
                setContentView(R.layout.createuser)
            }
            1 -> {
                setContentView(R.layout.readuser)
            }
            2 -> {
                setContentView(R.layout.updateuser)
            }
            3 -> {
                setContentView(R.layout.deleteuser)
            }
        }
    }
}