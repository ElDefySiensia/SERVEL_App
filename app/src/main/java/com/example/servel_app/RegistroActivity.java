package com.example.servel_app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.servel_app.db.AdminSQLiteOpenHelper;
import com.example.servel_app.repositorio.FirebaseRepositorio; // Importar Firebase

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistroActivity extends AppCompatActivity {

    // atributos
    private EditText rutRegistrar, claveRegistrar, claveConfirmar;
    private CheckBox checkCondiciones;
    private Button botonCrearCuenta, botonVolver;

    // INSTANCIA DEL REPOSITORIO DE FIREBASE
    private FirebaseRepositorio firebaseRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrate);

        // Inicializar Repositorio
        firebaseRepo = new FirebaseRepositorio();

        // ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // instancias de los atributos
        rutRegistrar = findViewById(R.id.rut_registrar);
        claveRegistrar = findViewById(R.id.clave_registrar);
        claveConfirmar = findViewById(R.id.clave_confirmar);
        checkCondiciones = findViewById(R.id.check_condiciones);
        botonCrearCuenta = findViewById(R.id.boton_crear_cuenta);
        botonVolver = findViewById(R.id.boton_volver_a_login);

        // boton volver al login
        botonVolver.setOnClickListener(v -> {
            Intent volver = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(volver);
            finish();
        });

        // boton crear cuenta (Lógica de Firebase)
        botonCrearCuenta.setOnClickListener(v -> {
            String rut = rutRegistrar.getText().toString().trim();
            String clave = claveRegistrar.getText().toString();
            String claveConf = claveConfirmar.getText().toString();

            // validaciones (se mantienen)
            if(rut.isEmpty()){
                rutRegistrar.setError("Ingrese su Rut");
                Toast.makeText(this, "Por favor ingrese su Rut", Toast.LENGTH_SHORT).show();
                return;
            }
            if(clave.isEmpty()){
                claveRegistrar.setError("Ingrese una clave");
                Toast.makeText(this, "Por favor ingrese su clave", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!clave.equals(claveConf)){
                claveConfirmar.setError("Las claves no coinciden");
                Toast.makeText(this, "Las claves no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!checkCondiciones.isChecked()){
                Toast.makeText(this, "Por favor, acepte los términos", Toast.LENGTH_SHORT).show();
                return;
            }

            // =================================================================
            // NUEVA LÓGICA: REGISTRAR EN FIREBASE AUTH
            // =================================================================

            Toast.makeText(this, "Creando cuenta, espere...", Toast.LENGTH_SHORT).show();

            firebaseRepo.registrarUsuario(rut, clave, new FirebaseRepositorio.AuthCallback() {
                @Override
                public void onSuccess(String nuevoRut) {
                    // PASO 1: Éxito en Firebase Auth (Cuenta de acceso creada)

                    // PASO 2: Guardar los datos en la tabla LOCAL 'usuarios' y 'datos_usuarios'
                    // NOTA: Debemos seguir insertando el RUT y la clave en la tabla LOCAL 'usuarios'
                    // si otras partes de la app (como las funciones de AdminSQLiteOpenHelper)
                    // dependen de que ese registro exista para la FK o para la BD local.

                    // En el modelo híbrido, si usamos SQLite para datos electorales,
                    // es mejor guardar una entrada simple en 'usuarios' para mantener
                    // la consistencia de la BD local (aunque no se use para login).

                    // La BD local también debe insertar en 'datos_usuarios' (simulación SERVEL).

                    insertarDatosEnSQLite(nuevoRut, clave);
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Error de Firebase (ej: Rut ya existe, Clave débil)
                    Toast.makeText(RegistroActivity.this, "Fallo: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // Método que maneja la inserción en SQLite (manteniendo la lógica previa)
    private void insertarDatosEnSQLite(String rut, String clave) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // 1. Encriptar clave (necesario para la tabla local 'usuarios')
        String claveEncriptada = Utils.encriptarSHA256(clave);

        // 2. Insertar datos en tabla 'usuarios' (Para consistencia local y FK)
        ContentValues registro = new ContentValues();
        registro.put("rut", rut);
        registro.put("clave_unica", claveEncriptada);

        long id = db.insert("usuarios", null, registro);

        // 3. *FALTA*: Insertar datos iniciales en 'datos_usuarios' (Simulación SERVEL)
        // Ya que la tabla 'datos_usuarios' tiene NOT NULL en varios campos,
        // deberías llamar a un método en AdminSQLiteOpenHelper que inserte valores
        // predeterminados o nulos aquí.
        // Por ahora, solo verificamos el éxito del registro de 'usuarios'.

        if(id > 0){
            Toast.makeText(this, "Cuenta local y remota creadas con éxito", Toast.LENGTH_SHORT).show();
            // volver al login
            Intent volverLogin = new Intent(this, LoginActivity.class);
            startActivity(volverLogin);
            finish();
        } else {
            // Este error puede ser un problema de concurrencia o de la BD local
            Toast.makeText(this, "Error local al finalizar el registro. Intente de nuevo.", Toast.LENGTH_SHORT).show();
            // Nota: Aquí se debería borrar el usuario en Firebase también,
            // pero es una lógica compleja para este ejercicio.
        }
        db.close();
    }

    // clase utilitaria para encriptar (se mantiene)
    public static class Utils {
        public static String encriptarSHA256(String input){
            // ... (código SHA-256)
            try{
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes());
                StringBuilder hexString = new StringBuilder();
                for(byte b: hash){
                    String hex = Integer.toHexString(0xff & b);
                    if(hex.length()==1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch(NoSuchAlgorithmException e){
                e.printStackTrace();
                return null;
            }
        }
    }
}