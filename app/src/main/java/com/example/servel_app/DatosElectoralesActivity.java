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
import androidx.appcompat.app.AppCompatActivity;
import com.example.servel_app.db.AdminSQLiteOpenHelper;

public class DatosElectoralesActivity extends AppCompatActivity {

    private TextView tvRun, tvNombre, tvCircunscripcion, tvDistrito, tvPais, tvHabilitadoSufragar,
            tvLocalVotacion, tvDireccionLocal, tvMesa, tvVocalMesa, tvMiembroColegioEscrutador;
    private Button botonVotoEnLinea, botonVolverAtras;
    private AdminSQLiteOpenHelper admin;
    private String rutUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_electorales);

        // Vincular Vistas
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
        botonVotoEnLinea = findViewById(R.id.boton_voto_en_linea);
        botonVolverAtras = findViewById(R.id.boton_volver_atras);

        // IMPORTANTE: Versión 1
        admin = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);

        rutUsuario = getIntent().getStringExtra("rutUsuario");

        if (rutUsuario == null) {
            Toast.makeText(this, "Error al cargar usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarDatosUsuario();

        botonVolverAtras.setOnClickListener(v -> onBackPressed());
        botonVotoEnLinea.setOnClickListener(v -> iniciarProcesoVoto());
    }

    private void cargarDatosUsuario() {
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM datos_usuarios WHERE rut=?", new String[]{rutUsuario});

        if (cursor.moveToFirst()) {
            tvRun.setText(cursor.getString(cursor.getColumnIndexOrThrow("rut")));
            tvNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            tvCircunscripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow("circunscripcion")));
            tvDistrito.setText(cursor.getString(cursor.getColumnIndexOrThrow("distrito")));
            tvPais.setText(cursor.getString(cursor.getColumnIndexOrThrow("pais")));

            boolean habilitado = cursor.getInt(cursor.getColumnIndexOrThrow("sufragioHabilitado")) == 1;
            tvHabilitadoSufragar.setText(habilitado ? "Sí" : "No");

            tvLocalVotacion.setText(cursor.getString(cursor.getColumnIndexOrThrow("localVotacion")));
            tvDireccionLocal.setText(cursor.getString(cursor.getColumnIndexOrThrow("direccionLocal")));
            tvMesa.setText(cursor.getString(cursor.getColumnIndexOrThrow("mesa")));

            tvVocalMesa.setText(cursor.getInt(cursor.getColumnIndexOrThrow("vocalMesa")) == 1 ? "Sí" : "No");
            tvMiembroColegioEscrutador.setText(cursor.getInt(cursor.getColumnIndexOrThrow("miembroEscrutador")) == 1 ? "Sí" : "No");

            // Si no está habilitado, deshabilitar botón
            if (!habilitado) {
                botonVotoEnLinea.setEnabled(false);
                botonVotoEnLinea.setText("No habilitado");
            }

        } else {
            Toast.makeText(this, "Datos no encontrados", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void iniciarProcesoVoto() {
        // Verificar si ya votó antes de pedir clave
        if (admin.votoRealizado(rutUsuario)) {
            Toast.makeText(this, "Usted ya ha emitido su voto.", Toast.LENGTH_LONG).show();
            botonVotoEnLinea.setEnabled(false);
            return;
        }

        EditText etClave = new EditText(this);
        etClave.setHint("Ingrese su contraseña");
        etClave.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Verificación de Seguridad")
                .setMessage("Para votar, confirme su contraseña:")
                .setView(etClave)
                .setPositiveButton("Ingresar", null) // Null para manejar el click manualmente
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setOnClickListener(v -> {
                String pass = etClave.getText().toString().trim();
                if (pass.isEmpty()) {
                    etClave.setError("Requerido");
                    return;
                }

                verificarPassword(pass, dialog);
            });
        });

        dialog.show();
    }

    private void verificarPassword(String passwordIngresada, AlertDialog dialog) {
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT clave_unica FROM usuarios WHERE rut=?", new String[]{rutUsuario});

        if (c.moveToFirst()) {
            String hashReal = c.getString(0);
            // Asegúrate de tener tu clase de utilidad para encriptar
            String hashIngresado = RegistroActivity.Utils.encriptarSHA256(passwordIngresada);

            if (hashReal.equals(hashIngresado)) {
                dialog.dismiss();
                Intent intent = new Intent(DatosElectoralesActivity.this, VotoEnLineaActivity.class);
                intent.putExtra("rutUsuario", rutUsuario);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        }
        c.close();
    }
}