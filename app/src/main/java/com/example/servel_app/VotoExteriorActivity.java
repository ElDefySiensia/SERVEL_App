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

public class VotoExteriorActivity extends AppCompatActivity {

    //atributos
    private Button botonVolver, botonMasInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        //vista asociada al voto exterior
        setContentView(R.layout.activity_voto_exterior);

        //ajuste de márgenes (para barras del sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instancias de los botones (debe coincidir con tus IDs del XML)
        botonMasInfo = findViewById(R.id.boton_mas_info);
        botonVolver = findViewById(R.id.boton_volver);

        //cuando se presione, abrirá el enlace oficial del Servel
        botonMasInfo.setOnClickListener(v -> {
            String url = "https://www.servel.cl/electorado/voto-en-el-exterior/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        //boton volver
        botonVolver.setOnClickListener(v -> {
            finish();
        });
    }

    //metodo para abrir URLs en el navegador
    private void abrirURL(String url){
        if(url == null || url.isEmpty()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
