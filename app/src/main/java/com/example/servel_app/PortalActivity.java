package com.example.servel_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PortalActivity extends AppCompatActivity {

    //atributos
    private Button botonDatosElectorales, botonTramites, botonVotoExterior, botonConoceCandidatos, botonJuntasElectorales, botonDemocraciaDesinformacion, botonFormacionCiudadana, botonCerrarSesion;

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mi_portal);

        //esto trae el rut desde login
        String rut = getIntent().getStringExtra("rutUsuario");

        //ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instancias de los atributos
        botonDatosElectorales = findViewById(R.id.boton_datos_electorales);
        botonTramites = findViewById(R.id.boton_tramites);
        botonVotoExterior = findViewById(R.id.boton_voto_exterior);
        botonConoceCandidatos = findViewById(R.id.boton_conoce_candidatos);
        botonJuntasElectorales = findViewById(R.id.boton_juntas_electorales);
        botonDemocraciaDesinformacion = findViewById(R.id.boton_democracia_desinformacion);
        botonFormacionCiudadana = findViewById(R.id.boton_formacion_ciudadana);
        botonCerrarSesion = findViewById(R.id.boton_cerrar_sesion);

        //metodo boton datos electorales
        botonDatosElectorales.setOnClickListener(v -> {
            //intent para dirigirnos a los datos electorales
            Intent datosElectorales = new Intent(PortalActivity.this, DatosElectoralesActivity.class);
            //esto trae el rut de login para mostrarlo mas tarde
            datosElectorales.putExtra("rutUsuario", rut);
            startActivity(datosElectorales);
        });

        //aqui van los demas metodos

        //metodo para cerrar sesion
        botonCerrarSesion.setOnClickListener(v -> {
            //intent para volver al login y cerrar sesión
            Intent cerrarSesion = new Intent(PortalActivity.this, LoginActivity.class);
            startActivity(cerrarSesion);
            finish();
        });
    }
}
