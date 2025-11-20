package com.example.servel_app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.servel_app.db.AdminSQLiteOpenHelper;

public class VerPerfilActivity extends AppCompatActivity {

    private Button botonVolver, botonModificar, botonEliminar;
    private TextView tvRut, tvNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        tvRut = findViewById(R.id.tv_rut);
        tvNombre = findViewById(R.id.tv_nombre);
        botonVolver = findViewById(R.id.boton_volver_atras);
        botonModificar = findViewById(R.id.boton_modificar_nombre);
        botonEliminar = findViewById(R.id.boton_eliminar_cuenta);

        String rut = getIntent().getStringExtra("rutUsuario");
        tvRut.setText(rut);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);
        Cursor cursor = admin.obtenerDatosCompletos(rut);

        if (cursor.moveToFirst()) {
            tvNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        } else {
            tvNombre.setText("Nombre no disponible");
        }

        cursor.close();

        botonVolver.setOnClickListener(v -> {
            Intent i = new Intent(this, PortalActivity.class);
            i.putExtra("rutUsuario", rut);
            startActivity(i);
            finish();
        });

        botonModificar.setOnClickListener(v -> {
            Intent i = new Intent(this, ModificarPerfilActivity.class);
            i.putExtra("rutUsuario", rut);
            startActivity(i);
        });


        botonEliminar.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        boolean eliminado = admin.eliminarUsuarioPorRut(rut);
                        if (eliminado) {
                            Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }
}
