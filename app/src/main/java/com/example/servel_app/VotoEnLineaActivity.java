package com.example.servel_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.servel_app.db.AdminSQLiteOpenHelper;

import java.util.ArrayList;

public class VotoEnLineaActivity extends AppCompatActivity {

    private ListView listaCandidatos;
    private CheckBox cbConfirmar;
    private Button botonEnviar, botonCancelar;
    private String rutUsuario;
    private ArrayList<String> nombresCandidatos = new ArrayList<>();
    private ArrayList<Integer> idsCandidatos = new ArrayList<>();
    private int candidatoSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voto_en_linea);

        // Inicializar vistas
        listaCandidatos = findViewById(R.id.lista_candidatos);
        cbConfirmar = findViewById(R.id.check_condiciones);
        botonEnviar = findViewById(R.id.boton_enviar_voto);
        botonCancelar = findViewById(R.id.boton_cancelar_voto);

        // Obtener rut
        rutUsuario = getIntent().getStringExtra("rutUsuario");
        if (rutUsuario == null || rutUsuario.isEmpty()) {
            Toast.makeText(this, "Error: no se recibió RUT del usuario", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Conectar a BD y cargar candidatos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id_candidato, nombre, partido FROM datos_candidatos", null);
        nombresCandidatos.clear();
        idsCandidatos.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_candidato"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String partido = cursor.getString(cursor.getColumnIndexOrThrow("partido"));
            nombresCandidatos.add(nombre + " (" + partido + ")");
            idsCandidatos.add(id);
        }

        cursor.close();
        db.close();

        // Adaptador para ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, nombresCandidatos);
        listaCandidatos.setAdapter(adapter);
        listaCandidatos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listaCandidatos.setOnItemClickListener((parent, view, position, id) -> candidatoSeleccionado = idsCandidatos.get(position));

        // Botón enviar voto
        botonEnviar.setOnClickListener(v -> {
            if (candidatoSeleccionado == -1) {
                Toast.makeText(this, "Seleccione un candidato", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cbConfirmar.isChecked()) {
                new AlertDialog.Builder(this)
                        .setTitle("Confirmación requerida")
                        .setMessage("Debe marcar la casilla para confirmar que su voto es correcto antes de continuar.")
                        .setPositiveButton("Aceptar", null)
                        .show();
                return;
            }

            // Guardar voto
            SQLiteDatabase dbVoto = admin.getWritableDatabase();
            try {
                dbVoto.execSQL("UPDATE usuarios SET votoRealizado = 1 WHERE rut = ?", new Object[]{rutUsuario});
                dbVoto.execSQL("UPDATE datos_candidatos SET votos = votos + 1 WHERE id_candidato = ?", new Object[]{candidatoSeleccionado});
            } finally {
                dbVoto.close();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Voto registrado")
                    .setMessage("Su voto ha sido registrado correctamente. ¡Gracias por participar!")
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        startActivity(new Intent(VotoEnLineaActivity.this, PortalActivity.class));
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        });

        // Botón cancelar voto
        botonCancelar.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancelar voto")
                    .setMessage("¿Está seguro que desea cancelar el voto?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        startActivity(new Intent(VotoEnLineaActivity.this, PortalActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
