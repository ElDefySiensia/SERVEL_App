package com.example.servel_app;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.servel_app.db.AdminSQLiteOpenHelper;

public class ModificarPerfilActivity extends AppCompatActivity {

    private EditText etNuevoNombre;
    private Button botonGuardar;
    private String rut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_perfil);

        etNuevoNombre = findViewById(R.id.et_nuevo_nombre);
        botonGuardar = findViewById(R.id.boton_guardar);
        Button botonCancelar = findViewById(R.id.boton_cancelar);

        rut = getIntent().getStringExtra("rutUsuario");
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);

        // mostrar el nombre actual
        Cursor cursor = admin.obtenerDatosUsuario(rut);
        if (cursor.moveToFirst()) {
            etNuevoNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        }
        cursor.close();

        botonGuardar.setOnClickListener(v -> {
            String nuevoNombre = etNuevoNombre.getText().toString().trim();
            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "Ingrese un nombre válido", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean actualizado = admin.actualizarNombreUsuario(rut, nuevoNombre);

            if (actualizado) {
                Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar nombre", Toast.LENGTH_SHORT).show();
            }
        });

        botonCancelar.setOnClickListener(v -> finish());
    }
}
