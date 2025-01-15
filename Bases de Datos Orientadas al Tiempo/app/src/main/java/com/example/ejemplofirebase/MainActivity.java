package com.example.ejemplofirebase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refValor = database.getReference("Entrada/Valor");
    DatabaseReference refTexto = database.getReference("Texto");
    DatabaseReference refHistorial = database.getReference("Historial");

    // Declaración de objetos Java
    Button obtnEnviar_1, obtnEnviar_0;
    TextView otvTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Relación de objetos Java con Objetos XML
        obtnEnviar_1 = findViewById(R.id.btnEnviar_1);
        obtnEnviar_0 = findViewById(R.id.btnEnviar_0);
        otvTexto = findViewById(R.id.tvTexto);

        // Enviar información a la base de datos
        obtnEnviar_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refValor.setValue(1, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        if (error == null) {
                            registrarHistorial(1); // Registra el cambio en el historial
                        }
                    }
                });
            }
        });

        obtnEnviar_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refValor.setValue(0, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        if (error == null) {
                            registrarHistorial(0); // Registra el cambio en el historial
                        }
                    }
                });
            }
        });

        refTexto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Este método es llamado cuando el valor cambia en Firebase
                String value = dataSnapshot.getValue(String.class);
                otvTexto.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Manejo de error al leer el valor
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    // Método para registrar el historial
    private void registrarHistorial(int valor) {
        // Generar la fecha actual
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Crear un objeto con el valor y la fecha
        Map<String, Object> registro = new HashMap<>();
        registro.put("Valor", valor);
        registro.put("Fecha", fecha);

        // Usar push() para agregar al historial
        refHistorial.push().setValue(registro);
    }
}
