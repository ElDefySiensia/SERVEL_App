package com.example.servel_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    //atributos
    private EditText rutLogin, claveLogin;
    private Button botonIngresar, botonRegistrate;

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instancias de los atributos
        rutLogin = findViewById(R.id.rut_login);
        claveLogin = findViewById(R.id.clave_login);
        botonIngresar = findViewById(R.id.boton_ingresar);
        botonRegistrate = findViewById(R.id.boton_registrate);

        //metodo para ingresar a la app
        botonIngresar.setOnClickListener(v -> {
            //definimos variables
            String rut = rutLogin.getText().toString();
            String clave = claveLogin.getText().toString();

            //validacion de datos
            if (rut.isEmpty()) {
                rutLogin.setError("Ingrese su Rut");
                Toast.makeText(this, "Por favor ingrese su Rut", Toast.LENGTH_SHORT).show();
                return;
            }
            if (clave.isEmpty()) {
                claveLogin.setError("Ingrese su Clave");
                Toast.makeText(this, "Por favor ingrese su Clave", Toast.LENGTH_SHORT).show();
                return;
            }

            //CONEXIÓN A LA BD
            com.example.servel_app.db.AdminSQLiteOpenHelper admin =
                    new com.example.servel_app.db.AdminSQLiteOpenHelper(this, "servel.db", null, 1);
            SQLiteDatabase db = admin.getReadableDatabase();

            //convertimos la clave a SHA-256 para comparar con la BD
            String claveHash = Utils.encriptarSHA256(clave);

            //consulta SQL para verificar el usuario
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM usuarios WHERE rut=? AND clave_unica=?",
                    new String[]{rut, claveHash}
            );

            if (cursor.moveToFirst()) {
                //login exitoso: enviamos al portal
                Intent loginExitoso = new Intent(LoginActivity.this, PortalActivity.class);
                loginExitoso.putExtra("rutUsuario", rut);
                startActivity(loginExitoso);
                finish();
            } else {
                //error de login
                Toast.makeText(this, "Rut o Clave incorrectos, vuelva a intentar", Toast.LENGTH_SHORT).show();
                rutLogin.setError("Rut incorrecto");
                claveLogin.setError("Clave incorrecta");
            }

            //cerrar conexión
            cursor.close();
            db.close();
        });

        //metodo para el boton registrate
        botonRegistrate.setOnClickListener(v -> {
            Intent registrate = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(registrate);
            finish();
        });
    }

    //convertir claves encriptadas para login
    public static class Utils {
        public static String encriptarSHA256(String input) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if(hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}