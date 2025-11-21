package com.example.servel_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.servel_app.repositorio.FirebaseRepositorio; // Importar el repositorio de Firebase

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    // atributos
    private EditText rutLogin, claveLogin;
    private Button botonIngresar, botonRegistrate;

    // INSTANCIA DEL REPOSITORIO DE FIREBASE
    private FirebaseRepositorio firebaseRepo;

    // onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Inicializar Repositorio
        firebaseRepo = new FirebaseRepositorio();

        // Si ya hay un usuario logueado en Firebase, navegamos al Portal inmediatamente
        String rutActual = firebaseRepo.getRUTUsuarioActual();
        if (rutActual != null) {
            navegarAPortal(rutActual);
            return; // Detenemos la ejecución de onCreate si navegamos
        }

        // ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // instancias de los atributos
        rutLogin = findViewById(R.id.rut_login);
        claveLogin = findViewById(R.id.clave_login);
        botonIngresar = findViewById(R.id.boton_ingresar);
        botonRegistrate = findViewById(R.id.boton_registrate);

        // metodo para ingresar a la app
        botonIngresar.setOnClickListener(v -> {
            String rut = rutLogin.getText().toString().trim();
            String clave = claveLogin.getText().toString().trim();

            // validacion de datos
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

            // AHORA USAMOS FIREBASE PARA LA AUTENTICACIÓN
            iniciarSesionConFirebase(rut, clave);

            // Nota: La encriptación SHA-256 ya no es necesaria aquí para el login,
            // ya que Firebase maneja la clave. Si la necesitas para otra cosa,
            // la clase Utils se mantiene al final.
        });

        // metodo para el boton registrate
        botonRegistrate.setOnClickListener(v -> {
            Intent registrate = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(registrate);
            finish();
        });
    }

    // ------------------------------------
    // LÓGICA DE FIREBASE AUTENTICACIÓN
    // ------------------------------------

    private void iniciarSesionConFirebase(String rut, String clave) {
        Toast.makeText(this, "Verificando credenciales...", Toast.LENGTH_SHORT).show();

        firebaseRepo.iniciarSesion(rut, clave, new FirebaseRepositorio.AuthCallback() {
            @Override
            public void onSuccess(String usuarioRut) {
                // Login exitoso
                Toast.makeText(LoginActivity.this, "¡Bienvenido/a!", Toast.LENGTH_SHORT).show();
                navegarAPortal(usuarioRut);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Error de login
                Toast.makeText(LoginActivity.this, "Error de autenticación: Rut o Clave incorrectos", Toast.LENGTH_LONG).show();
                rutLogin.setError(null); // Limpiamos errores previos si son internos de Firebase
                claveLogin.setError(null);
            }
        });
    }

    private void navegarAPortal(String rut) {
        Intent loginExitoso = new Intent(LoginActivity.this, PortalActivity.class);
        loginExitoso.putExtra("rutUsuario", rut);
        startActivity(loginExitoso);
        finish();
    }

    // Convertir claves encriptadas (se mantiene por si acaso, aunque no se usa en el login de Firebase)
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