package com.example.servel_app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

// Administra la conexión y estructura de la base de datos SQLite
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    // Constructor
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name,
                                 @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Se ejecuta la primera vez que se crea la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla de usuarios
        db.execSQL("CREATE TABLE usuarios (" +
                "rut TEXT PRIMARY KEY NOT NULL," +
                "clave_unica INTEGER NOT NULL)");

        // Tabla de datos complementarios
        db.execSQL("CREATE TABLE datos_usuarios (" +
                "rut TEXT NOT NULL," +
                "nombre TEXT NOT NULL," +
                "circunscripcion TEXT NOT NULL," +
                "distrito TEXT NOT NULL," +
                "pais TEXT NOT NULL," +
                "sufragioHabilitado INTEGER NOT NULL," +  // 0 = false, 1 = true
                "localVotacion TEXT NOT NULL," +
                "direccionLocal TEXT NOT NULL," +
                "mesa TEXT NOT NULL," +
                "vocalMesa INTEGER NOT NULL," +
                "miembroEscrutador INTEGER NOT NULL," +
                "FOREIGN KEY(rut) REFERENCES usuarios(rut))");
    }

    // Se ejecuta al cambiar la versión de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS datos_usuarios");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }
}
