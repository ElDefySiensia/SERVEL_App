package com.example.servel_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DatosElectoralesActivity extends AppCompatActivity {

    private TextView tvRun, tvNombre, tvCircunscripcion, tvDistrito, tvPais, tvHabilitadoSufragar,
            tvLocalVotacion, tvDireccionLocal, tvMesa, tvVocalMesa, tvMiembroColegioEscrutador;
    private Button botonVotoEnLinea, botonVolverAtras;

    @Override
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

        //obtener el rut del usuario enviado desde el LoginActivity
        String rutUsuario = getIntent().getStringExtra("rutUsuario");

        //conectar con la BD y mostrar la información
        com.example.servel_app.db.AdminSQLiteOpenHelper admin =
                new com.example.servel_app.db.AdminSQLiteOpenHelper(this, "servel.db", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM datos_usuarios WHERE rut=?", new String[]{rutUsuario});

        if (cursor.moveToFirst()) {
            //llenamos los TextView con los datos del usuario
            tvRun.setText(cursor.getString(cursor.getColumnIndexOrThrow("rut")));
            tvNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            tvCircunscripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow("circunscripcion")));
            tvDistrito.setText(cursor.getString(cursor.getColumnIndexOrThrow("distrito")));
            tvPais.setText(cursor.getString(cursor.getColumnIndexOrThrow("pais")));
            tvHabilitadoSufragar.setText(cursor.getInt(cursor.getColumnIndexOrThrow("sufragioHabilitado")) == 1 ? "Sí" : "No");
            tvLocalVotacion.setText(cursor.getString(cursor.getColumnIndexOrThrow("localVotacion")));
            tvDireccionLocal.setText(cursor.getString(cursor.getColumnIndexOrThrow("direccionLocal")));
            tvMesa.setText(cursor.getString(cursor.getColumnIndexOrThrow("mesa")));
            tvVocalMesa.setText(cursor.getInt(cursor.getColumnIndexOrThrow("vocalMesa")) == 1 ? "Sí" : "No");
            tvMiembroColegioEscrutador.setText(cursor.getInt(cursor.getColumnIndexOrThrow("miembroEscrutador")) == 1 ? "Sí" : "No");
        } else {
            Toast.makeText(this, "No se encontraron datos para el usuario", Toast.LENGTH_SHORT).show();
        }

        //cerrar cursor y BD
        cursor.close();
        db.close();

        //metodo para volver atrás y borrar la info para resguardar la seguridad de los datos
        botonVolverAtras.setOnClickListener(v -> {
            Intent volverAtras = new Intent(DatosElectoralesActivity.this, PortalActivity.class);
            startActivity(volverAtras);
            finish();
        });

        //metodo para dirigirnos al voto en línea
        botonVotoEnLinea.setOnClickListener(v -> {
            Intent irVoto = new Intent(DatosElectoralesActivity.this, VotoEnLineaActivity.class);
            irVoto.putExtra("rutUsuario", rutUsuario);
            startActivity(irVoto);
        });
    }
}
