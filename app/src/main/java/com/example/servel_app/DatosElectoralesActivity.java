package com.example.servel_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.servel_app.db.AdminSQLiteOpenHelper;

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

        //instancia de atributos
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

        //instancia de botones
        botonVotoEnLinea = findViewById(R.id.boton_voto_en_linea);
        botonVolverAtras = findViewById(R.id.boton_volver_atras);

        //obtener RUT del usuario
        String rutUsuario = getIntent().getStringExtra("rutUsuario");

        //validar que no sea null o vacío
        if (rutUsuario == null || rutUsuario.isEmpty()) {
            Toast.makeText(this, "Error: el usuario no está registrado en SERVEL o no se obtuvieron los datos", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //conectar con la BD
        com.example.servel_app.db.AdminSQLiteOpenHelper admin =
                new com.example.servel_app.db.AdminSQLiteOpenHelper(this, "servel.db", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM datos_usuarios WHERE rut=?", new String[]{rutUsuario});

        //si no hay match, cerramos la Activity
        if (!cursor.moveToFirst()) {
            Toast.makeText(this, "No se encontraron datos para el usuario", Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            finish();
            return;
        }

        //llenamos los TextView
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

        cursor.close();
        db.close();

        botonVolverAtras.setOnClickListener(v -> {
            onBackPressed();
        });

        //boton voto en línea
        botonVotoEnLinea.setOnClickListener(v -> {
            EditText etClave = new EditText(this);
            etClave.setHint("Ingrese su clave");
            etClave.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Verificación de identidad")
                    .setMessage("Ingrese su clave para continuar con el voto en línea.")
                    .setView(etClave)
                    .setPositiveButton("Aceptar", null)
                    .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                    .create();

            dialog.setOnShowListener(d -> {
                Button btnAceptar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnAceptar.setOnClickListener(v1 -> {
                    String claveIngresada = etClave.getText().toString().trim();
                    if (claveIngresada.isEmpty()) {
                        Toast.makeText(this, "Debe ingresar su clave", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Usa la instancia ya declarada fuera, NO redeclarar
                    SQLiteDatabase dbVotos = admin.getReadableDatabase();

                    Cursor cursor2 = dbVotos.rawQuery("SELECT clave_unica FROM usuarios WHERE rut = ?", new String[]{rutUsuario});
                    if (cursor2.moveToFirst()) {
                        String claveHash = cursor2.getString(cursor2.getColumnIndexOrThrow("clave_unica"));
                        String claveEncriptada = RegistroActivity.Utils.encriptarSHA256(claveIngresada);

                        if (claveHash.equals(claveEncriptada)) {
                            Intent irVoto = new Intent(DatosElectoralesActivity.this, VotoEnLineaActivity.class);
                            irVoto.putExtra("rutUsuario", rutUsuario);
                            startActivity(irVoto);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Clave incorrecta", Toast.LENGTH_SHORT).show();
                            etClave.setText("");
                        }
                    } else {
                        Toast.makeText(this, "Usuario no registrado", Toast.LENGTH_SHORT).show();
                    }

                    cursor2.close();
                    dbVotos.close();
                });
            });

            dialog.show();
        });
    }

    //metodo para mostrar alerta
    private void mostrarAlertSinDatos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos insuficientes");
        builder.setMessage("Usted no posee datos o le faltan datos en SERVEL, por lo que no podemos verificar su autenticidad de votante.\n\nPor favor consulte en la sucursal o directo en la sección Trámites de la app.");
        builder.setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
