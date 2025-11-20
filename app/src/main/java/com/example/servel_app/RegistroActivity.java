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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistroActivity extends AppCompatActivity {

    //atributos
    private EditText rutRegistrar, claveRegistrar, claveConfirmar;
    private CheckBox checkCondiciones;
    private Button botonCrearCuenta, botonVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrate);

        //ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instancias de los atributos
        rutRegistrar = findViewById(R.id.rut_registrar);
        claveRegistrar = findViewById(R.id.clave_registrar);
        claveConfirmar = findViewById(R.id.clave_confirmar);
        checkCondiciones = findViewById(R.id.check_condiciones);
        botonCrearCuenta = findViewById(R.id.boton_crear_cuenta);
        botonVolver = findViewById(R.id.boton_volver_a_login);

        //boton volver al login
        botonVolver.setOnClickListener(v -> {
            Intent volver = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(volver);
            finish();
        });

        //boton crear cuenta
        botonCrearCuenta.setOnClickListener(v -> {
            String rut = rutRegistrar.getText().toString().trim();
            String clave = claveRegistrar.getText().toString();
            String claveConf = claveConfirmar.getText().toString();

            //validaciones
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

            //conexion a la bd
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "servel.db", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            //VALIDACIÓN: verificar si el rut ya existe
            Cursor cursor = db.rawQuery("SELECT rut FROM usuarios WHERE rut = ?", new String[]{rut});
            if(cursor.moveToFirst()){
                Toast.makeText(this, "Este Rut ya tiene una cuenta registrada", Toast.LENGTH_LONG).show();
                cursor.close();
                db.close();
                return; //cancelar creación
            }
            cursor.close();

            //encriptar clave
            String claveEncriptada = Utils.encriptarSHA256(clave);

            //insertar datos en tabla usuarios
            ContentValues registro = new ContentValues();
            registro.put("rut", rut);
            registro.put("clave_unica", claveEncriptada);

            long id = db.insert("usuarios", null, registro);

            if(id > 0){
                Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                db.close();
                //volver al login
                Intent volverLogin = new Intent(this, LoginActivity.class);
                startActivity(volverLogin);
                finish();
            } else {
                Toast.makeText(this, "Error al crear cuenta. Rut ya existe?", Toast.LENGTH_SHORT).show();
                db.close();
            }
        });
    }

    //clase utilitaria para encriptar
    public static class Utils {
        public static String encriptarSHA256(String input){
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
