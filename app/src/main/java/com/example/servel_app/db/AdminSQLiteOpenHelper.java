package com.example.servel_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    //Asegúrate de usar siempre la misma versión en todas las Activities
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name,
                                 @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Tabla usuarios
        db.execSQL("CREATE TABLE usuarios (" +
                "rut TEXT PRIMARY KEY NOT NULL," +
                "clave_unica TEXT NOT NULL)");

        //Datos del Servel
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

        //Candidatos
        db.execSQL("CREATE TABLE datos_candidatos (" +
                "id_candidato INTEGER PRIMARY KEY NOT NULL," +
                "nombre TEXT NOT NULL," +
                "partido TEXT NOT NULL," +
                "votos INTEGER NOT NULL DEFAULT 0)");

        //Registro de votos
        db.execSQL("CREATE TABLE votos (" +
                "rut TEXT PRIMARY KEY NOT NULL," +
                "id_candidato INTEGER NOT NULL," +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(rut) REFERENCES usuarios(rut)," +
                "FOREIGN KEY(id_candidato) REFERENCES datos_candidatos(id_candidato))");

        // --- INSERTS INICIALES ---
        db.execSQL("INSERT INTO datos_usuarios " +
                "(rut, nombre, circunscripcion, distrito, pais, sufragioHabilitado, localVotacion, direccionLocal, mesa, vocalMesa, miembroEscrutador) VALUES " +
                "('11222333-4', 'Carlos Rivera', 'Santiago Centro', 'Distrito 10', 'Chile', 1, 'Liceo Santiago 7', 'Av. Alameda 123', 'Mesa 45', 0, 0)," +
                "('99888777-6', 'Lucía Herrera', 'Providencia', 'Distrito 11', 'Chile', 1, 'Colegio Providencia', 'Manuel Montt 540', 'Mesa 12', 0, 1)," +
                "('77666555-3', 'Jorge Silva', 'Ñuñoa', 'Distrito 9', 'Chile', 1, 'Liceo Manuel de Salas', 'Av. Brown Norte 120', 'Mesa 88', 1, 0)," +
                "('55444333-2', 'María Vergara', 'Maipú', 'Distrito 8', 'Chile', 1, 'Escuela Los Héroes', 'Pajaritos 3000', 'Mesa 21', 0, 0)," +
                "('44333222-1', 'Esteban Torres', 'La Florida', 'Distrito 12', 'Chile', 1, 'Colegio Los Pensamientos', 'Walker Martínez 900', 'Mesa 51', 0, 1)");

        db.execSQL("INSERT INTO datos_candidatos (id_candidato, nombre, partido) VALUES " +
                "(1, 'Ignacio Kaiser', 'PC - Personal Computer')," +
                "(2, 'Benjamín Kast', 'RN - Red Neural')," +
                "(3, 'Bryan Jara', 'PL - Programación Libertaria')," +
                "(4, 'Victoria Nichols', 'DC - Data Center')," +
                "(5, 'Antonio Artés', 'PA - Proceso Automatizado')," +
                "(6, 'Marcelo Ominami', 'PRO - Protocolo Remoto Operativo')," +
                "(7, 'Felipe Parisi', 'UDI - Unidad de Datos Integrados')," +
                "(8, 'Joselyn Matthei', 'PS - Proyecto de Software')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS votos");
        db.execSQL("DROP TABLE IF EXISTS datos_usuarios");
        db.execSQL("DROP TABLE IF EXISTS datos_candidatos");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }

    //Ver perfil completo
    public Cursor obtenerDatosCompletos(String rut) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM datos_usuarios WHERE rut = ?", new String[]{rut});
    }

    //Modificar nombre
    public boolean actualizarNombreUsuario(String rut, String nuevoNombre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nuevoNombre);

        int filas = db.update("datos_usuarios", valores, "rut = ?", new String[]{rut});
        return filas > 0;
    }

    //Obtener solo nombre para ModificarPerfil
    public Cursor obtenerDatosUsuario(String rut) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT nombre FROM datos_usuarios WHERE rut = ?", new String[]{rut});
    }

    //actualizar domicilio
    public boolean actualizarDomicilio(String rut, String nuevaDireccion, String nuevaCircunscripcion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("direccionLocal", nuevaDireccion);
        valores.put("circunscripcion", nuevaCircunscripcion);
        valores.put("localVotacion", "Escuela " + nuevaCircunscripcion + " N°1");
        int filas = db.update("datos_usuarios", valores, "rut = ?", new String[]{rut});
        return filas > 0;
    }

    //Eliminar cuenta
    public boolean eliminarUsuarioPorRut(String rut) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("votos", "rut = ?", new String[]{rut});
        int user = db.delete("usuarios", "rut = ?", new String[]{rut});
        return user > 0;
    }

    public Cursor obtenerTodosLosCandidatos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM datos_candidatos", null);
    }

    public boolean votoRealizado(String rut) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT rut FROM votos WHERE rut = ?";
        Cursor cursor = db.rawQuery(query, new String[]{rut});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    public boolean registrarVoto(String rut, int idCandidato) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Verificación de seguridad extra
        if (votoRealizado(rut)) return false;
        ContentValues valores = new ContentValues();
        valores.put("rut", rut);
        valores.put("id_candidato", idCandidato);
        //Insertar en tabla votos (historial)
        long insertado = db.insert("votos", null, valores);
        if (insertado == -1) return false;
        //Aumentar contador del candidato
        db.execSQL("UPDATE datos_candidatos SET votos = votos + 1 WHERE id_candidato = ?",
                new Object[]{idCandidato});
        //Marcar que el usuario ya votó en su tabla de datos
        db.execSQL("UPDATE datos_usuarios SET votoRealizado = 1 WHERE rut = ?",
                new Object[]{rut});
        return true;
    }
}