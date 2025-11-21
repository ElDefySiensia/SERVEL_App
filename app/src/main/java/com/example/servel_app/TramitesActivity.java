package com.example.servel_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TramitesActivity extends AppCompatActivity {

    private Button botonCambioDomicilio, botonIncorporacion1, botonAfiliacionPartidos, botonIncorporacion2;
    private String rutUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tramites);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Recuperar el RUT del usuario que viene de la actividad anterior
        rutUsuario = getIntent().getStringExtra("rutUsuario");

        botonCambioDomicilio = findViewById(R.id.boton_cambio_domicilio);
        botonIncorporacion1 = findViewById(R.id.boton_incorporacion_1);
        botonAfiliacionPartidos = findViewById(R.id.boton_afiliacion);
        botonIncorporacion2 = findViewById(R.id.boton_incorporacion_2);

        // --- FUNCIONALIDAD BOTONES ---

        //CAMBIO DE DOMICILIO
        botonCambioDomicilio.setOnClickListener(v -> {
            if (rutUsuario != null) {
                Intent intent = new Intent(TramitesActivity.this, CambioDomicilioActivity.class);
                intent.putExtra("rutUsuario", rutUsuario); // Pasamos el RUT
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            }
        });

        //INCORPORACIÓN
        botonIncorporacion1.setOnClickListener(v -> {
            abrirURL("https://www.chileatiende.gob.cl/fichas/11079-solicitud-de-incorporacion-al-registro-electoral");
        });

        //AFILIACIÓN PARTIDOS
        botonAfiliacionPartidos.setOnClickListener(v -> {
            abrirURL("https://partidos.servel.cl/");
        });

        //CONSULTA DE DATOS
        botonIncorporacion2.setOnClickListener(v -> {
            // Nota: Asumí que el 4to botón era consulta por el nombre en tu XML anterior
            abrirURL("https://consulta.servel.cl/");
        });
    }

    private void abrirURL(String url){
        if(url == null || url.isEmpty()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}