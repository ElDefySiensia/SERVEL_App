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
    private AdminSQLiteOpenHelper admin; // Declarar variable global

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voto_en_linea);

        // Inicializar vistas
        listaCandidatos = findViewById(R.id.lista_candidatos);
        cbConfirmar = findViewById(R.id.check_condiciones);
        botonEnviar = findViewById(R.id.boton_enviar_voto);
        botonCancelar = findViewById(R.id.boton_cancelar_voto);

        rutUsuario = getIntent().getStringExtra("rutUsuario");
        if (rutUsuario == null || rutUsuario.isEmpty()) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicializamos la BD con versión 1
        admin = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);

        cargarCandidatos();

        // Lógica de selección de la lista
        listaCandidatos.setOnItemClickListener((parent, view, position, id) -> {
            candidatoSeleccionado = idsCandidatos.get(position);
        });

        botonEnviar.setOnClickListener(v -> procesarVoto());

        botonCancelar.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancelar")
                    .setMessage("¿Desea salir sin votar?")
                    .setPositiveButton("Sí", (d, w) -> {
                        // AQUÍ ESTÁ LA CLAVE:
                        Intent intent = new Intent(VotoEnLineaActivity.this, PortalActivity.class);

                        // Pasamos el RUT de nuevo para que el Portal sepa quién es
                        intent.putExtra("rutUsuario", rutUsuario);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void cargarCandidatos() {
        SQLiteDatabase db = admin.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id_candidato, nombre, partido FROM datos_candidatos", null);

        nombresCandidatos.clear();
        idsCandidatos.clear();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            String partido = cursor.getString(2);
            nombresCandidatos.add(nombre + "\n(" + partido + ")");
            idsCandidatos.add(id);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, nombresCandidatos);
        listaCandidatos.setAdapter(adapter);
        listaCandidatos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void procesarVoto() {
        if (candidatoSeleccionado == -1) {
            Toast.makeText(this, "Debe seleccionar un candidato", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbConfirmar.isChecked()) {
            Toast.makeText(this, "Confirme su voto marcando la casilla", Toast.LENGTH_SHORT).show();
            return;
        }

        //metodo del helper
        boolean exito = admin.registrarVoto(rutUsuario, candidatoSeleccionado);

        if (exito) {
            new AlertDialog.Builder(this)
                    .setTitle("¡Voto Exitoso!")
                    .setMessage("Su sufragio ha sido registrado correctamente.")
                    .setPositiveButton("Volver al inicio", (dialog, which) -> {

                        Intent intent = new Intent(VotoEnLineaActivity.this, PortalActivity.class);

                        // AQUÍ TAMBIÉN: Pasamos el RUT de vuelta
                        intent.putExtra("rutUsuario", rutUsuario);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            Toast.makeText(this, "Error: Ya se registró un voto para este usuario.", Toast.LENGTH_LONG).show();
            botonEnviar.setEnabled(false); // Desactivar botón si ya votó
        }
    }
}