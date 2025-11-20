package com.example.servel_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//Activity que muestra los trámites disponibles
public class TramitesActivity extends AppCompatActivity {

    //atributos
    private Button botonCambioDomicilio, botonIncorporacion1, botonAfiliacionPartidos, botonIncorporacion2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        //vista asociada a los trámites
        setContentView(R.layout.activity_tramites);

        //ajuste de márgenes (para barras del sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instancias de los botones (pon exactamente los IDs de tu XML)
        botonCambioDomicilio = findViewById(R.id.boton_cambio_domicilio);
        botonIncorporacion1 = findViewById(R.id.boton_incorporacion_1);
        botonAfiliacionPartidos = findViewById(R.id.boton_afiliacion);
        botonIncorporacion2 = findViewById(R.id.boton_incorporacion_2);

        //boton Cambio de Domicilio
        botonCambioDomicilio.setOnClickListener(v -> {
            //URL del trámite Cambio de Domicilio (la añades tú aquí)
            String url = "";  // <-- pega aquí tu URL
            abrirURL(url);
        });

        //incorporación al registro electoral (primer botón)
        botonIncorporacion1.setOnClickListener(v -> {
            //URL del trámite que quieras enlazar
            String url = "";  // <-- pega aquí tu URL
            abrirURL(url);
        });

        //solicitud de Afiliación a Partidos Políticos
        botonAfiliacionPartidos.setOnClickListener(v -> {
            //URL del trámite de afiliación
            String url = "";  // <-- pega aquí tu URL
            abrirURL(url);
        });

        //incorporación al registro electoral (segundo botón)
        botonIncorporacion2.setOnClickListener(v -> {
            //URL correspondiente
            String url = ""; // <-- pega aquí tu URL
            abrirURL(url);
        });
    }

    //metodo para abrir URLs en el navegador
    private void abrirURL(String url){
        if(url == null || url.isEmpty()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
