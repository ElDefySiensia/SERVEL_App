package com.example.servel_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DatosElectoralesActivity extends AppCompatActivity {

    private TextView tvRun, tvNombre, tvCircunscripcion, tvDistrito, tvPais, tvHabilitadoSufragar, tvLocalVotacion, tvDireccionLocal, tvMesa, tvVocalMesa, tvMiembroColegioEscrutador;
    private Button botonVotoEnLinea, botonVolverAtras;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_electorales);

        //ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instancia de los atributos
        tvRun = findViewById(R.id.tv_run);
        tvNombre = findViewById(R.id.tv_nombre);
        tvCircunscripcion = findViewById(R.id.tv_circunscripcion);
        tvDistrito = findViewById(R.id.tv_distrito);
        tvPais = findViewById(R.id.tv_pais);
        tvHabilitadoSufragar = findViewById(R.id.tv_habilitado_sufragar);
        tvLocalVotacion = findViewById(R.id.tv_local_votacion);
        tvDireccionLocal = findViewById(R.id.tv_direccion_local);
        tvMesa = findViewById(R.id.tv_mesa);

        tvVocalMesa = findViewById(R.id.tv_vocal_mesa);
        tvMiembroColegioEscrutador = findViewById(R.id.tv_miembro_colegio_escrutador);

        //instancia de los botones
        botonVotoEnLinea = findViewById(R.id.boton_voto_en_linea);
        botonVolverAtras = findViewById(R.id.boton_volver_atras);

        //metodo para mostrar la informacion

        //metodo para dirigirnos al voto en linea


        //metodo para volver atrás y borrar la info para resguardar la seguridad de los datos
        botonVolverAtras.setOnClickListener(v -> {
            Intent volverAtras = new Intent(DatosElectoralesActivity.this, PortalActivity.class);
            startActivity(volverAtras);
            finish();
        });
    }
}