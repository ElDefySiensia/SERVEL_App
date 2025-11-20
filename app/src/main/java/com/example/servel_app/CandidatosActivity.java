package com.example.servel_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import com.example.servel_app.adapters.CandidatosAdapter;
import com.example.servel_app.db.AdminSQLiteOpenHelper;

import java.util.ArrayList;

public class CandidatosActivity extends AppCompatActivity {

    RecyclerView listaCandidatos;
    Button botonVolver;
    AdminSQLiteOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidatos);

        //base de datos
        db = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);

        //recycler
        listaCandidatos = findViewById(R.id.listaCandidatos);
        listaCandidatos.setLayoutManager(new LinearLayoutManager(this));

        //cargar candidatos desde la BD
        ArrayList<CandidatoModelo> candidatos = obtenerCandidatosDesdeBD();

        //adaptador
        CandidatosAdapter adapter = new CandidatosAdapter(candidatos);
        listaCandidatos.setAdapter(adapter);

        //volver
        botonVolver = findViewById(R.id.botonVolver);
        botonVolver.setOnClickListener(v -> finish());
    }


    //obtener candidatos REALMENTE desde la BD
    private ArrayList<CandidatoModelo> obtenerCandidatosDesdeBD() {

        ArrayList<CandidatoModelo> lista = new ArrayList<>();

        Cursor cursor = db.obtenerTodosLosCandidatos();

        if (cursor != null && cursor.moveToFirst()) {

            do {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String partido = cursor.getString(cursor.getColumnIndexOrThrow("partido"));

                lista.add(new CandidatoModelo(nombre, partido));

            } while (cursor.moveToNext());

            cursor.close();
        }

        return lista;
    }
}
