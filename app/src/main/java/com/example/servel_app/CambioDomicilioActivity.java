package com.example.servel_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.servel_app.db.AdminSQLiteOpenHelper;

public class CambioDomicilioActivity extends AppCompatActivity {

    private EditText etDireccion, etComuna;
    private Button btnGuardar, btnCancelar;
    private String rutUsuario;
    private AdminSQLiteOpenHelper admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cambio_domicilio);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rutUsuario = getIntent().getStringExtra("rutUsuario");
        admin = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);

        etDireccion = findViewById(R.id.et_nueva_direccion);
        etComuna = findViewById(R.id.et_nueva_comuna);
        btnGuardar = findViewById(R.id.btn_guardar_domicilio);
        btnCancelar = findViewById(R.id.btn_cancelar);

        btnCancelar.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> {
            String nuevaDir = etDireccion.getText().toString();
            String nuevaComuna = etComuna.getText().toString();

            if (nuevaDir.isEmpty() || nuevaComuna.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Llamada a la Base de Datos para actualizar
            boolean exito = admin.actualizarDomicilio(rutUsuario, nuevaDir, nuevaComuna);

            if (exito) {
                mostrarConfirmacion();
            } else {
                Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarConfirmacion() {
        new AlertDialog.Builder(this)
                .setTitle("Solicitud Enviada")
                .setMessage("Su domicilio electoral ha sido actualizado exitosamente.")
                .setPositiveButton("Aceptar", (dialog, which) -> finish()) // Cierra la actividad
                .setCancelable(false)
                .show();
    }
}