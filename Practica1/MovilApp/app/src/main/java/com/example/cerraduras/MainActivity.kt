package com.example.cerraduras

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cerraduras.ui.theme.CerradurasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CerradurasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    InputScreen(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        // URL de la app web que deseas abrir
                        val url = "http://10.0.2.2:8080/cerradura"
                        openWebPage(url)
                    }
                }
            }
        }
    }

    // Función para abrir el navegador con la URL
    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

/**
 * Componente que retorna la vista principal de la app
 * @return componente principal
 */
@Composable
fun InputScreen(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,  // Centra el contenido horizontalmente
        verticalArrangement = Arrangement.Center  // Centra el contenido verticalmente
    ) {
        // Texto en la parte superior
        Text(
            text = "Cerradura de Kleene",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)  // Espacio debajo del texto
        )

        // Botón centrado
        Button(onClick = { onButtonClick() }) {
            Text("Abrir Web")
        }
    }
}

/**
 * Componente que muestra el preview del componente
 * "InputScreen"
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CerradurasTheme {
        InputScreen {}
    }
}
