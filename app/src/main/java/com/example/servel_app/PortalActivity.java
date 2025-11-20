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

public class PortalActivity extends AppCompatActivity {

    private Button botonDatosElectorales, botonTramites, botonVotoExterior, botonConoceCandidatos,
            botonJuntasElectorales, botonDemocraciaDesinformacion, botonFormacionCiudadana,
            botonCerrarSesion, botonPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mi_portal);

        String rut = getIntent().getStringExtra("rutUsuario");

        // Ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        // Instancias de botones
        botonDatosElectorales = findViewById(R.id.boton_datos_electorales);
        botonTramites = findViewById(R.id.boton_tramites);
        botonVotoExterior = findViewById(R.id.boton_voto_exterior);
        botonConoceCandidatos = findViewById(R.id.boton_conoce_candidatos);
        botonJuntasElectorales = findViewById(R.id.boton_juntas_electorales);
        botonDemocraciaDesinformacion = findViewById(R.id.boton_democracia_desinformacion);
        botonFormacionCiudadana = findViewById(R.id.boton_formacion_ciudadana);
        botonCerrarSesion = findViewById(R.id.boton_cerrar_sesion);
        botonPerfil = findViewById(R.id.boton_perfil);

        // Listeners
        botonDatosElectorales.setOnClickListener(v -> {
            Intent i = new Intent(this, DatosElectoralesActivity.class);
            i.putExtra("rutUsuario", rut);
            startActivity(i);
        });

        botonTramites.setOnClickListener(v -> {
            Intent i = new Intent(this, TramitesActivity.class);
            i.putExtra("rutUsuario", rut);
            startActivity(i);
        });

        botonVotoExterior.setOnClickListener(v -> {
            Intent i = new Intent(this, VotoExteriorActivity.class);
            i.putExtra("rutUsuario", rut);
            startActivity(i);
        });

        botonConoceCandidatos.setOnClickListener(v -> {
            Intent i = new Intent(this, CandidatosActivity.class);
            i.putExtra("rutUsuario", rut);
            startActivity(i);
        });

        botonJuntasElectorales.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.servel.cl/contacto/juntas-electorales/"));
            startActivity(i);
        });

        botonDemocraciaDesinformacion.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.servel.cl/desinformacion/"));
            startActivity(i);
        });

        botonFormacionCiudadana.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://formacionciudadana.servel.cl/"));
            startActivity(i);
        });

        botonPerfil.setOnClickListener(v -> {
            Intent i = new Intent(this, VerPerfilActivity.class);
            i.putExtra("rutUsuario", rut);
            startActivity(i);
        });

        botonCerrarSesion.setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }
}
