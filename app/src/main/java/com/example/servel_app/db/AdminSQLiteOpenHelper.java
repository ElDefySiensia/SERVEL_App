package com.example.servel_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

//administra la conexión y estructura de la base de datos SQLite
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    //constructor
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name,
                                 @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //se ejecuta la primera vez que se crea la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {

        //tabla de la app
        db.execSQL("CREATE TABLE usuarios (" +
                "rut TEXT PRIMARY KEY NOT NULL," +
                "clave_unica TEXT NOT NULL)");

        //tabla de datos "SERVEL"
        db.execSQL("CREATE TABLE datos_usuarios (" +
                "rut TEXT NOT NULL," +
                "nombre TEXT NOT NULL," +
                "circunscripcion TEXT NOT NULL," +
                "distrito TEXT NOT NULL," +
                "pais TEXT NOT NULL," +
                "sufragioHabilitado INTEGER NOT NULL," +
                "localVotacion TEXT NOT NULL," +
                "direccionLocal TEXT NOT NULL," +
                "mesa TEXT NOT NULL," +
                "vocalMesa INTEGER NOT NULL," +
                "miembroEscrutador INTEGER NOT NULL," +
                "votoRealizado INTEGER NOT NULL DEFAULT 0," +
                "FOREIGN KEY(rut) REFERENCES usuarios(rut))");

        //tabla de datos "SERVEL 2"
        db.execSQL("CREATE TABLE datos_candidatos (" +
                "id_candidato INTEGER PRIMARY KEY NOT NULL," +
                "nombre TEXT NOT NULL," +
                "partido TEXT NOT NULL," +
                "votos INTEGER NOT NULL DEFAULT 0)");

        //datos en SERVEL
        db.execSQL("INSERT INTO datos_usuarios (rut, nombre, circunscripcion, distrito, pais, sufragioHabilitado, localVotacion, direccionLocal, mesa, vocalMesa, miembroEscrutador) VALUES " +
                "('11222333-4', 'Carlos Rivera', 'Santiago Centro', 'Distrito 10', 'Chile', 1, 'Liceo Santiago 7', 'Av. Alameda 123', 'Mesa 45', 0, 0)," +
                "('99888777-6', 'Lucía Herrera', 'Providencia', 'Distrito 11', 'Chile', 1, 'Colegio Providencia', 'Manuel Montt 540', 'Mesa 12', 0, 1)," +
                "('77666555-3', 'Jorge Silva', 'Ñuñoa', 'Distrito 9', 'Chile', 1, 'Liceo Manuel de Salas', 'Av. Brown Norte 120', 'Mesa 88', 1, 0)," +
                "('55444333-2', 'María Vergara', 'Maipú', 'Distrito 8', 'Chile', 1, 'Escuela Los Héroes', 'Pajaritos 3000', 'Mesa 21', 0, 0)," +
                "('44333222-1', 'Esteban Torres', 'La Florida', 'Distrito 12', 'Chile', 1, 'Colegio Los Pensamientos', 'Walker Martínez 900', 'Mesa 51', 0, 1)");

        //candidatos
        db.execSQL("INSERT INTO datos_candidatos (id_candidato, nombre, partido) VALUES " +
                "(1, 'Ignacio Kaiser', 'PC - Personal Computer')," +
                "(2, 'Benjamín Kast', 'RN - Red Neural')," +
                "(3, 'Bryan Jara', 'PL - Programación Libertaria')," +
                "(4, 'Victoria Nichols', 'DC - Data Center')," +
                "(5, 'Antonio Artés', 'PA - Proceso Automatizado')," +
                "(6, 'Marcelo Ominami', 'PRO - Protocolo Remoto Operativo')," +
                "(7, 'Felipe Parisi', 'UDI - Unidad de Datos Integrados')," +
                "(8, 'Joselyn Matthei', 'PS - Proyecto de Software')");

        // tabla de votos para registrar el voto de cada usuario
        db.execSQL("CREATE TABLE votos (" +
                "rut TEXT PRIMARY KEY NOT NULL," +
                "id_candidato INTEGER NOT NULL," +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(rut) REFERENCES usuarios(rut)," +
                "FOREIGN KEY(id_candidato) REFERENCES datos_candidatos(id_candidato))");
    }

    public boolean eliminarUsuarioPorRut(String rut) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("usuarios", "rut = ?", new String[]{rut});
        return filas > 0;
    }

    public Cursor obtenerDatosCompletos(String rut) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT usuarios.rut, datos_usuarios.nombre " +
                        "FROM usuarios " +
                        "INNER JOIN datos_usuarios ON usuarios.rut = datos_usuarios.rut " +
                        "WHERE usuarios.rut = ? LIMIT 1";

        return db.rawQuery(query, new String[]{rut});
    }

    public boolean actualizarNombre(String rut, String nuevoNombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nuevoNombre);

        int filas = db.update("datos_usuarios", valores, "rut = ?", new String[]{rut});

        return filas > 0;
    }

    //verifica si un usuario ya ha votado
    public boolean votoRealizado(String rut) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT rut FROM votos WHERE rut = ?", new String[]{rut});
        boolean votoExistente = cursor.moveToFirst();
        cursor.close();
        return votoExistente;
    }

    //registra un voto para un usuario y candidato
    public boolean registrarVoto(String rut, int idCandidato) {
        SQLiteDatabase db = this.getWritableDatabase();

        // revisa primero si ya votó
        if (votoRealizado(rut)) {
            return false; // no puede votar otra vez
        }

        ContentValues valores = new ContentValues();
        valores.put("rut", rut);
        valores.put("id_candidato", idCandidato);

        long insertado = db.insert("votos", null, valores);

        if (insertado > 0) {
            // aumenta el conteo de votos del candidato
            db.execSQL("UPDATE datos_candidatos SET votos = votos + 1 WHERE id_candidato = ?", new Object[]{idCandidato});
            return true;
        } else {
            return false;
        }
    }


    public Cursor obtenerTodosLosCandidatos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT nombre, partido FROM datos_candidatos", null);
    }

    //se ejecuta al cambiar la versión de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS datos_usuarios");
        db.execSQL("DROP TABLE IF EXISTS datos_candidatos");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }

}
