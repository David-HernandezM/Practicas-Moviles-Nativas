package com.example.cerraduras

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cerraduras.ui.theme.CerradurasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CerradurasTheme {      ////////////Importante
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    // Usamos el paddingValues en el Modifier para evitar el warning
                    InputScreen(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        // URL de la app web que deseas abrir
                        val url = "https://localhost:8080"
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
}           //////////Importante

@Composable
fun InputScreen(modifier: Modifier = Modifier, onButtonClick: () -> Unit) {
    Button(onClick = { onButtonClick() }, modifier = modifier) {
        Text("Abrir Web")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CerradurasTheme {
        InputScreen {}
    }
}

